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

import org.activityinfo.login.server.LoginServiceServlet;
import org.activityinfo.login.shared.LoginException;
import org.activityinfo.server.bootstrap.model.LoginPageModel;
import org.activityinfo.server.mail.MailSender;
import org.activityinfo.server.util.logging.LogException;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import freemarker.template.Configuration;

@Singleton
@Path(LoginController.ENDPOINT)
public class LoginController extends AbstractController {
    public static final String ENDPOINT = "/login";

    @Inject
    private LoginServiceServlet loginService;

    @Override
    @LogException(emailAlert = true)
    protected void onGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		writeView(resp, req, new LoginPageModel(parseUrlSuffix(req)));
    }

	@Override
	protected void onPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		boolean ajax = "true".equals(req.getParameter("ajax"));
		try {
			loginService.login(req.getParameter("email"), req.getParameter("password"), 
					false);
			if(ajax) {
				resp.setStatus(HttpServletResponse.SC_OK);
			} else {
				resp.sendRedirect("/");
			}
		} catch (LoginException e) {
			if(ajax) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			} else {
				writeView(resp, req,
						LoginPageModel.unsuccessful(parseUrlSuffix(req)));
			}
		}
	}
    
    

}
