/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.activityinfo.server.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.activityinfo.server.util.logging.Trace;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.appengine.api.utils.SystemProperty;
import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class ConfigModule extends AbstractModule {
    private static Logger logger = Logger.getLogger(ConfigModule.class.getName());

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    @Trace
    public DeploymentConfiguration provideDeploymentConfig(ServletContext context) {
        Properties properties = new Properties();

        tryToLoadFrom(properties, webInfDirectory(context));
        tryToLoadFrom(properties, systemSettings());
        tryToLoadFrom(properties, userSettings());
        tryToLoadFromS3(properties);
        tryToLoadFrom(properties, versionSpecific(context));

        return new DeploymentConfiguration(properties);
    }

    @Provides
    public AWSCredentials provideAWSCredentials(DeploymentConfiguration config) {
    	return config.getAWSCredentials();
    }
    
    private boolean tryToLoadFrom(Properties properties, File file) {
        try {
            logger.info("Trying to read properties from: " + file.getAbsolutePath());
            if(file.exists()) {
                logger.info("Reading properties from " + file.getAbsolutePath());
                properties.load(new FileInputStream(file));
                return true;
            }
        } catch (IOException e) {
            return false;
        } catch (AccessControlException e) {
        	return false;
        }
        return false;
    }

    /** 
     * 
     * Tries to load a configuration file from an external object stored in Amazon S3.
     * 
     * This is very useful in automating production and continuous deployment scenarios
     * because it allows us to keep the config and the app artifact seperate. 
     * 
     * This method expects the following environment variables to be set: 
     * <ul>
     * <li>AWS_ACCESS_KEY_ID: access key for the AWS account authorized to access the S3 bucket</li>
     * <li>AWS_SECRET_KEY: secret key for this account</li>
     * <li>PARAM1: the name of the bucket where the config file is stored</li>
     * <li>PARAM2: the key to the config properties file</li>
     * </ul>
     * 
     * @param properties the properties object into which to load the config
     */
    private void tryToLoadFromS3(Properties properties) {
        if(!BeanstalkEnvironment.credentialsArePresent()) {
            logger.info("AWS Credentials not provided, not attempting to load config from S3");
            return;
        }

        String awsAccessKeyId = BeanstalkEnvironment.getAccessKeyId();
        String awsSecretAccessKey = BeanstalkEnvironment.getSecretKey();
        String bucket = BeanstalkEnvironment.getConfigurationPropertiesBucket();
        if(Strings.isNullOrEmpty(bucket)) {
            logger.log(Level.SEVERE, "AWS Credentials provided, but PARAM1 does not contain the bucket name in which the configuration file is stored");
            return;
        }
        String key = BeanstalkEnvironment.getConfigurationPropertiesKey();
        if(Strings.isNullOrEmpty(key)) {
        	logger.log(Level.SEVERE, "AWS Credentials provided, but PARAM2 does not contain the configuration file's key");
        }	
        
        AmazonS3Client client = new AmazonS3Client(new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey));
        try {
            properties.load(client.getObject(bucket, key).getObjectContent());
            logger.info("Loaded configuration from S3 " + bucket + "/" + key);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception reading configuration from S3: " + e.getMessage(), e);
        }
    }
    
    private File webInfDirectory(ServletContext context) {
        return new File(context.getRealPath("WEB-INF") + File.separator + "activityinfo.properties");
    }
    
    /**
     * Path to an override properties files for the current appEngine version.
     * For example, if we have the appengine version staging, the path will be
     * WEB-INF/activityinfo.staging.properties.
     *
     */
    private File versionSpecific(ServletContext context) {
    	String appVersion = Strings.nullToEmpty(SystemProperty.applicationVersion.get());
    	int suffixStart = appVersion.lastIndexOf('.');
    	if(suffixStart != -1) {
    		appVersion = appVersion.substring(0, suffixStart);
    	}
        return new File(context.getRealPath("WEB-INF") + File.separator + "activityinfo." + appVersion + ".properties");
    }
    
    private File systemSettings() {
        return new File("/etc/activityinfo.properties");
    }
    
    private File userSettings() {
    	return new File(System.getProperty("user.home") + File.separator + "activityinfo.properties");
    }
}
