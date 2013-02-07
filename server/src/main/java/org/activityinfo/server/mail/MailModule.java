/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.activityinfo.server.mail;

import com.google.inject.AbstractModule;

public class MailModule extends AbstractModule {

    @Override
    protected void configure() {
    	boolean notGae = null != System.getProperty("gae.disable");
    	
    	if (notGae) {
    		bind(MailSender.class).to(CommonMailSenderImpl.class);
    	} else {
    		bind(MailSender.class).to(MailSenderImpl.class);
    	}
    }
}
