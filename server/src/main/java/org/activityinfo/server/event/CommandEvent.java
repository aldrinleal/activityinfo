package org.activityinfo.server.event;

import org.activityinfo.login.shared.AuthenticatedUser;
import org.activityinfo.server.endpoint.gwtrpc.ServerExecutionContext;
import org.activityinfo.shared.command.Command;
import org.activityinfo.shared.command.CreateSite;
import org.activityinfo.shared.command.SiteCommand;
import org.activityinfo.shared.command.UpdateSite;
import org.activityinfo.shared.command.result.CommandResult;

import com.extjs.gxt.ui.client.data.RpcMap;

@SuppressWarnings("rawtypes")
public class CommandEvent {
	private Command command;
	private CommandResult result;
	private ServerExecutionContext context;
	
	public CommandEvent(Command command, CommandResult result, ServerExecutionContext context) {
		this.command = command;
		this.result = result;
		this.context = context;
	}
	
	public Command getCommand() {
		return this.command;
	}
	
	public CommandResult getResult() {
		return result;
	}
	
	public ServerExecutionContext getContext() {
		return context;
	}
	
	@Override
	public String toString() {
		return "CommandEvent ["+getCommand().getClass().getSimpleName()+"]";
	}
	
	public Integer getUserId() {
		AuthenticatedUser au = getContext().getUser();
		if (au != null) {
			return au.getUserId();
		}
		return null;
	}
	
	public Integer getSiteId() {
		if (getCommand() instanceof SiteCommand) {
			return ((SiteCommand)getCommand()).getSiteId();
		}
		return null;
	}
	
	public RpcMap getRpcMap() {
		if (getCommand() instanceof SiteCommand) {
			return ((SiteCommand)getCommand()).getProperties();
		}
		return null;
	}
}
