/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.activityinfo.server.bootstrap;

import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * The Bootstrap module is responsible for the minimal static
 * html necessary to login, retrieve lost passwords, etc.
 */
public class BootstrapModule extends ServletModule {

    @Override
    protected void configureServlets() {
    	bind(HostController.class);
    	bind(LoginController.class);
    	bind(ConfirmInviteController.class);
    	bind(LogoutController.class);
    	bind(ResetPasswordController.class);
    	bind(ChangePasswordController.class);
    	
		serve(HostController.ENDPOINT, LoginController.ENDPOINT, ConfirmInviteController.ENDPOINT, LogoutController.ENDPOINT, ResetPasswordController.ENDPOINT, ChangePasswordController.ENDPOINT).with(GuiceContainer.class);
        
        serve("/ActivityInfo/ActivityInfo.nocache.js").with(SelectionServlet.class);
        serve("/ActivityInfo/ActivityInfo.appcache").with(SelectionServlet.class);
        serve("/ActivityInfo/ActivityInfo.gears.manifest").with(SelectionServlet.class);
    }
}
