/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.activityinfo.client.offline.command;

import org.activityinfo.client.EventBus;
import org.activityinfo.client.dispatch.Dispatcher;
import org.activityinfo.client.dispatch.remote.AbstractDispatcher;
import org.activityinfo.client.dispatch.remote.Remote;
import org.activityinfo.client.offline.sync.ServerStateChangeEvent;
import org.activityinfo.login.shared.AuthenticatedUser;
import org.activityinfo.shared.command.Command;
import org.activityinfo.shared.command.MutatingCommand;
import org.activityinfo.shared.command.result.CommandResult;
import org.activityinfo.shared.util.Collector;

import org.activityinfo.client.Log;
import com.bedatadriven.rebar.async.ChainedCallback;
import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * Dispatches commands to local handlers
 */
public class LocalDispatcher extends AbstractDispatcher {
    private final AuthenticatedUser auth;
    private final HandlerRegistry registry;
    private final SqlDatabase database;
    private final CommandQueue commandQueue;
	private final Dispatcher remoteDispatcher;
	private final EventBus eventBus;
	
    @Inject
    public LocalDispatcher(EventBus eventBus, AuthenticatedUser auth, SqlDatabase database,
    		HandlerRegistry registry, @Remote Dispatcher remoteDispatcher, CommandQueue commandQueue) {
        Log.trace("LocalDispatcher constructor starting...");
        this.eventBus = eventBus;
    	this.auth = auth;
        this.registry = registry;
        this.database = database;
        this.commandQueue = commandQueue;
        this.remoteDispatcher = remoteDispatcher;
    }
    
    @Override
	public <R extends CommandResult> void execute(Command<R> command,
			final AsyncCallback<R> callback) {
    	
    	if(registry.hasHandler(command)) {
    		executeOffline(command, callback);
    	} else {
    		executeRemotely(command, callback);
    	}
	}

	/**
     * Begins a new transaction, initializes a new ExecutionContext, and executes the root command.
     * @param command
     * @param callback
     */
    private <R extends CommandResult> void executeOffline(final Command<R> command, final AsyncCallback<R> callback) {
    	Log.debug("Executing command " + command + " OFFLINE.");
    	try {
	    	final Collector<R> commandResult = Collector.newCollector();
	    	database.transaction(new SqlTransactionCallback() {
				
				@Override
				public void begin(SqlTransaction tx) {
					OfflineExecutionContext context = new OfflineExecutionContext(auth, tx, registry, commandQueue);
					context.execute(command, commandResult);
				}
	
				@Override
				public void onError(SqlException e) {
					Log.error("OFFLINE EXECUTION FAILED: " + command, e);
					callback.onFailure(e);
				}
	
				@Override
				public void onSuccess() {
					callback.onSuccess(commandResult.getResult());
				}
			});
    	} catch(Exception e) {
    		callback.onFailure(e);
    	}
    }

    private <R extends CommandResult> void executeRemotely(final Command<R> command, final AsyncCallback<R> callback) {
    	Log.debug("No handler for " + command + ", executing remotely.");

    	remoteDispatcher.execute(command, new ChainedCallback<R>(callback) {

			@Override
			public void onSuccess(R result) {
		    	if(command instanceof MutatingCommand) {
		    		// This will lead to an inconstent state where the user's
		    		// local copy of the database is out of sync 
		    		
		    		// this will disappear as we implement more functionality offline
		    		// but in the meantime we need to trigger synchronization and 
		    		// let the user they should expect some screwy stuff.
                	if(command instanceof MutatingCommand) {
    		    		eventBus.fireEvent(new ServerStateChangeEvent());
                	}		    	
		    	} 
		    	callback.onSuccess(result);
			}
    	});
    }
}
