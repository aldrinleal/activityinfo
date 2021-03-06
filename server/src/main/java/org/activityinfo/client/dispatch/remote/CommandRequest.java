/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.activityinfo.client.dispatch.remote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.activityinfo.shared.command.Command;
import org.activityinfo.shared.command.MutatingCommand;

import org.activityinfo.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * Encapsulates a pending command request to the server.
 *
 * @author Alex Bertram
 */
class CommandRequest implements AsyncCallback {
    /**
     * The pending command
     */
    private final Command command;
    private final List<AsyncCallback> callbacks = new ArrayList<AsyncCallback>();


    public CommandRequest(Command command, AsyncCallback callback) {
        this.command = command;
        this.callbacks.add(callback);
    }

    public Command getCommand() {
        return command;
    }

    public Collection<AsyncCallback> getCallbacks() {
        return Collections.unmodifiableCollection(this.callbacks);
    }
    
    public boolean mergeSuccessfulInto(List<CommandRequest> list) {
        for (CommandRequest request : list) {
            if (command.equals(request.getCommand())) {
                request.merge(this);
                return true;
            }
        }
        return false;
    }

    private void merge(CommandRequest request) {
        Log.debug("Dispatcher: merging " + request.getCommand().toString() + " with pending/executing command " +
                getCommand().toString());

        callbacks.addAll(request.callbacks);
    }

    /**
     * True if this CommandRequest is expected to mutate (change) the state
     * of the remote server.
     */
    public boolean isMutating() {
        return command instanceof MutatingCommand;
    }

	@Override
	public void onFailure(Throwable caught) {
        for (AsyncCallback c : callbacks) {
        	try {
        		c.onFailure(caught);
        	} catch(Exception e) {
        		Log.error("Uncaught exception during onFailure()", e);
        	}
        }
	}

	@Override
	public void onSuccess(Object result) {
		List<AsyncCallback> toCallback = new ArrayList<AsyncCallback>(callbacks);
        for (AsyncCallback c : toCallback) {
            try {
                c.onSuccess(result);
            } catch (Exception e) {
                Log.error("Exception thrown during callback on AsyncCallback.onSuccess() for " + command.toString(), e);
            }
        }
	}
}
