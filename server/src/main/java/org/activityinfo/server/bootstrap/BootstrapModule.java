/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.activityinfo.server.bootstrap;

import java.util.Map;
import java.util.TreeMap;

import org.activityinfo.server.bootstrap.jaxrs.LocaleContextProvider;
import org.activityinfo.server.bootstrap.jaxrs.RedirectMessageBodyWriter;
import org.activityinfo.server.bootstrap.jaxrs.TemplateDirectiveMessageBodyWriter;

import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * The Bootstrap module is responsible for the minimal static
 * html necessary to login, retrieve lost passwords, etc.
 */
public class BootstrapModule extends ServletModule {

    @Override
    protected void configureServlets() {
    	bind(LocaleContextProvider.class);
    	bind(RedirectMessageBodyWriter.class);
    	bind(TemplateDirectiveMessageBodyWriter.class);
    	
        serve("/ActivityInfo/ActivityInfo.nocache.js").with(SelectionServlet.class);
        serve("/ActivityInfo/ActivityInfo.appcache").with(SelectionServlet.class);
        serve("/ActivityInfo/ActivityInfo.gears.manifest").with(SelectionServlet.class);
        
        bind(HostController.class, LoginController.class, ConfirmInviteController.class, LogoutController.class, ResetPasswordController.class, ChangePasswordController.class);

        Map<String, String> initParams = new TreeMap<String, String>();
        
        initParams.put("com.sun.jersey.config.property.WebPageContentRegex", "/.*\\.(jpg|ico|png|gif|html|id|txt|css|js)");
        
		filter("/*").through(GuiceContainer.class, initParams);
    }

	private void bind(Class<?>... endpointClasses) {
		for (Class<?> c : endpointClasses)
			bind(c);
	}
}
