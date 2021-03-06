package org.activityinfo.server.mail;

import org.activityinfo.server.database.hibernate.entity.User;

public abstract class MailMessage {
	
	public abstract User getRecipient();
	
	public String getSubjectKey() {
		String key = getMessageName() + "Subject";
		return key.substring(0,1).toLowerCase() + key.substring(1);
	}
	
    public String getTemplateName() {
        return "mail/" + getMessageName() + ".ftl";
    }
    
    private String getMessageName() {
    	if(!getClass().getSimpleName().endsWith("Message")) {
    		throw new RuntimeException("MailMessage subclasses must end in 'Message'!");
    	}
    	return getClass().getSimpleName().substring(0, getClass().getSimpleName().length() - "Message".length());
    }
}
