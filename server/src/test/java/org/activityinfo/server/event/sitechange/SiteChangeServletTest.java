package org.activityinfo.server.event.sitechange;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.persistence.EntityManager;

import org.activityinfo.server.authentication.ServerSideAuthProvider;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.mail.MailSender;
import org.activityinfo.server.mail.MailSenderStub;
import org.activityinfo.server.mail.MailSenderStubModule;
import org.activityinfo.test.InjectionSupport;
import org.activityinfo.test.Modules;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Provider;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
@Modules({MailSenderStubModule.class})
public class SiteChangeServletTest extends CommandTestCase2 {
	@Inject
    private EntityManager entityManager;

	@Inject
	private MailSender mailSender;
	
	@Test
    public void testSubscriptions() throws Exception {
		SiteChangeServlet underTest = createServlet();
		
		List<User> users = underTest.findRecipients(1);
		assertThat(users.size(), is(equalTo(2)));
		
		User alex = users.get(0);
		assertThat(alex.getName(), is(equalTo("Alex")));
		assertThat(alex.isEmailNotification(), is(true));
		assertThat(alex.getLocale(), is(equalTo("fr")));
		
		User marlene = users.get(1);
		assertThat(marlene.getName(), is(equalTo("Marlene")));
		assertThat(marlene.isEmailNotification(), is(true));
		assertThat(marlene.getLocale(), is(equalTo("en")));
    }

	@Test
	public void testSendNotifications() throws Exception {
		SiteChangeServlet underTest = createServlet();
		underTest.sendNotifications(1, 1, true);
	
		List<Message> msgs = ((MailSenderStub)mailSender).sentMail;
		assertThat(msgs.size(), is(equalTo(1)));
		
		Message msgToMarlene = msgs.get(0);
		assertThat(msgToMarlene.getRecipients(RecipientType.TO)[0].toString(), is(equalTo("Marlene <marlene@solidarites>")));
		assertTrue(msgToMarlene.getContentType().startsWith("text/html"));
		assertThat(msgToMarlene.getSubject(), is(equalTo("PEAR: New NFI at Penekusu Kivu by NRC")));
	}
	
	private Matcher<String> startsWith(final String expected) {
		return new TypeSafeMatcher<String>(String.class) {

			@Override
			public void describeTo(Description d) {
				d.appendText("starts with ");
				d.appendValue(expected);
			}

			@Override
			public boolean matchesSafely(String item) {
				return item.startsWith(expected);
			}
			
		};
	}

	private SiteChangeServlet createServlet() {
		return new SiteChangeServlet(
				new Provider<EntityManager>() {
					@Override public EntityManager get() { return entityManager; }
				}, 
				new Provider<MailSender>() {
					@Override public MailSender get() { return mailSender; }
				}, 
				new ServerSideAuthProvider(),
				getDispatcherSync());
	}
	
	public static void main(String[] args) {
		String s = "<html><head><title>FR Database PEAR has been edited</title></head><body><p>FR Hi Alex,</p><p>FR User Alex Alex (akbertram@gmail.com) edited database PEAR on 13-11-2012 at 00:22.</p><p><p class='comments'><span class='groupName'>Commentaires:</span> He said 'booyah'</p><table class='indicatorTable' cellspacing='0'><tr><td class='indicatorGroupHeading'>outputs</td><td>&nbsp;</td></tr><tr><td class='indicatorHeading indicatorGroupChild'>baches</td><td class='indicatorValue'>500</td><td class='indicatorUnits'>menages</td></tr><tr><td class='indicatorHeading indicatorGroupChild'>Nb. of distributions</td><td class='indicatorValue'>1</td><td class='indicatorUnits'>distributions</td></tr><tr><td class='indicatorHeading indicatorGroupChild'>A kivu thing</td><td class='indicatorValue'>1</td><td class='indicatorUnits'>distributions</td></tr><tr><td class='indicatorGroupHeading'>inputs</td><td>&nbsp;</td></tr><tr><td class='indicatorHeading indicatorGroupChild'>beneficiaries</td><td class='indicatorValue'>1,500</td><td class='indicatorUnits'>menages</td></tr></table></p><p>FR Best regards,<br>The ActivityInfo Team</p></body></html>";
	}
}