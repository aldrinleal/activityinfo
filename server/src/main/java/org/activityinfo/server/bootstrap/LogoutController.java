/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.activityinfo.server.bootstrap;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;

import org.activityinfo.login.shared.AuthenticatedUser;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import freemarker.template.Configuration;

@Singleton
@Path(LogoutController.ENDPOINT)
public class LogoutController extends AbstractController {
    public static final String ENDPOINT = "/logout";

    @Override
    protected void onGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logUserOut(resp);
        resp.sendRedirect("/login");
    }

    protected void logUserOut(HttpServletResponse resp) {
        removeCookie(resp, AuthenticatedUser.AUTH_TOKEN_COOKIE);
        removeCookie(resp, AuthenticatedUser.EMAIL_COOKIE);
        removeCookie(resp, AuthenticatedUser.USER_ID_COOKIE);
		removeCookie(resp, AuthenticatedUser.USER_LOCAL_COOKIE);
    }
}
