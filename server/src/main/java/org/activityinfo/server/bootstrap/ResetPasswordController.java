/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.activityinfo.server.bootstrap;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;

import org.activityinfo.server.authentication.SecureTokenGenerator;
import org.activityinfo.server.bootstrap.model.ResetPasswordPageModel;
import org.activityinfo.server.database.hibernate.dao.Transactional;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.mail.MailSender;
import org.activityinfo.server.mail.ResetPasswordMessage;
import org.activityinfo.server.util.logging.LogException;
import org.activityinfo.shared.exception.InvalidLoginException;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import freemarker.template.Configuration;

@Singleton
@Path(ResetPasswordController.ENDPOINT)
public class ResetPasswordController extends AbstractController {
	public static final String ENDPOINT = "/loginProblem";

	@Inject
	private MailSender mailer;

	@Override
	@LogException(emailAlert = true)
	protected void onGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		writeView(resp, req, new ResetPasswordPageModel());
	}

	@Override
    @LogException(emailAlert = true)
    @Transactional
    protected void onPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
        	
            User user = findUserByEmail(req.getParameter("email"));
            user.setChangePasswordKey(SecureTokenGenerator.generate());
            user.setDateChangePasswordKeyIssued(new Date());
            
            mailer.send(new ResetPasswordMessage(user));
                        
        	ResetPasswordPageModel model = new ResetPasswordPageModel();
        	model.setEmailSent(true);
        	
			writeView(resp, req, model);
        	
        } catch (InvalidLoginException e) {
        	ResetPasswordPageModel model = new ResetPasswordPageModel();
        	model.setLoginError(true);
        	
			writeView(resp, req, model);
        } catch (Exception e) {
        	ResetPasswordPageModel model = new ResetPasswordPageModel();
        	model.setEmailError(true);
        	
			writeView(resp, req, model);
		}
    }


}
