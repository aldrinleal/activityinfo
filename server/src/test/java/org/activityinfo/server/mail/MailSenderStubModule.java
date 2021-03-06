package org.activityinfo.server.mail;

import org.activityinfo.server.mail.MailSender;

import com.google.inject.AbstractModule;

public class MailSenderStubModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(MailSender.class).to(MailSenderStub.class);
	}

}
