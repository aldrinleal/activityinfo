/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.activityinfo.server.bootstrap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.activityinfo.login.server.LoginServiceServlet;
import org.activityinfo.login.shared.AuthenticatedUser;
import org.activityinfo.login.shared.LoginException;
import org.activityinfo.server.bootstrap.model.LoginPageModel;
import org.activityinfo.server.bootstrap.model.Redirect;
import org.activityinfo.server.util.logging.LogException;

import com.google.inject.Inject;

@Path(LoginController.ENDPOINT)
public class LoginController extends AbstractController {
    public static final String ENDPOINT = "/login";

    @Inject
    private LoginServiceServlet loginService;
    
    @GET
    @LogException(emailAlert = true)
    public Response onGet(@Context HttpServletRequest req) throws Exception {
		return writeView(req, new LoginPageModel(parseUrlSuffix(req)));
    }

    @POST
    @LogException(emailAlert = true)
	public Response onPost(@Context HttpServletRequest req) throws Exception {
		boolean ajax = "true".equals(req.getParameter("ajax"));
		ResponseBuilder response = Response.ok();
		
		try {
			AuthenticatedUser user = loginService.login(req.getParameter("email"), req.getParameter("password"), 
					false);
			addLocaleCookie(response, user);
			if(!ajax)
				response = Response.ok(new Redirect("/"));
		} catch (LoginException e) {
			if(ajax) {
				response.status(HttpServletResponse.SC_BAD_REQUEST);
			} else {
				return writeView(req,
						LoginPageModel.unsuccessful(parseUrlSuffix(req)));
			}
		}
		
		return response.build();
	}

	/**
	 * Adds the user's locale as a cookie, so that we preserve the right
	 * language even after they log out.
	 * @param response
	 * @param user
	 */
	private void addLocaleCookie(ResponseBuilder response,
			AuthenticatedUser user) {
		response.cookie(new NewCookie("locale", user.getUserLocale(), "/", null, null, 60 * 60 * 24 * 365, false));
	}
}
