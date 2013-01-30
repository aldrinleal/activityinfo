/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.activityinfo.server.bootstrap;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import javax.servlet.ServletException;

import org.activityinfo.server.bootstrap.model.LoginPageModel;
import org.activityinfo.server.mail.MailSender;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Module;
import com.google.inject.util.Modules;

public class LoginControllerTest extends ControllerTestCase<LoginController> {
    @Before
    public void setUp() {
    	replay(sender);
    	
        req.setRequestURL("http://activityinfo.org/login");
    }

    @Test
    public void requestShouldReceiveView() throws IOException, ServletException {

        get();

        assertNull(resp.redirectUrl);
        assertTemplateUsed(LoginPageModel.class);
    }

    @Test
    public void urlSuffixIsParsedCorrectly() throws IOException, ServletException {

        get("http://activityinfo.org/login#charts");

        assertEquals("#charts", controller.parseUrlSuffix(req));
    }

    @Test
    public void bookmarkShouldBeIncludedInModel() throws IOException, ServletException {

        get("http://activityinfo.org/login#charts");

        assertNull(resp.redirectUrl);
        assertTemplateUsed(LoginPageModel.class);
        assertEquals("#charts", lastLoginPageModel().getUrlSuffix());
    }
}
