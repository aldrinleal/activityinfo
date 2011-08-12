/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command.handler;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Before;
import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.i18n.UIMessages;
import org.sigmah.client.mock.MockEventBus;
import org.sigmah.client.offline.command.CommandQueue;
import org.sigmah.client.offline.command.LocalDispatcher;
import org.sigmah.client.offline.sync.Synchronizer;
import org.sigmah.client.offline.sync.UpdateSynchronizer;
import org.sigmah.server.endpoint.gwtrpc.CommandServlet;
import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.SyncRegionUpdate;
import org.sigmah.shared.domain.User;

import com.allen_sauer.gwt.log.client.Log;
import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.server.jdbc.JdbcDatabase;
import com.bedatadriven.rebar.sql.server.jdbc.JdbcDatabaseFactory;
import com.bedatadriven.rebar.sync.client.BulkUpdaterAsync;
import com.bedatadriven.rebar.sync.mock.MockBulkUpdater;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public abstract class LocalHandlerTestCase {
    @Inject
    private CommandServlet servlet;
    @Inject
    protected EntityManagerFactory serverEntityManagerFactory;

    /**
     * this is scoped to Tests as the analog of being
     * scoped to a request.
     */
    @Inject
    protected EntityManager serverEm;
    
    protected User user;

    protected Dispatcher remoteDispatcher;

    protected Authentication localAuth;
    protected LocalDispatcher localDispatcher;
    protected JdbcDatabase localDatabase;
    
    protected CommandQueue commandQueue;
    
    private BulkUpdaterAsync updater;

    private UIConstants uiConstants;
    private UIMessages uiMessages;
	protected Connection localConnection;
	
	private String databaseName = "target/localdbtest" + new java.util.Date().getTime();

    @Before
    public void setUp() throws SQLException, ClassNotFoundException {

        setUser(1); // default is db owner

        remoteDispatcher = new RemoteDispatcherStub();

        localDatabase = new JdbcDatabase(databaseName);
                        
        uiConstants = createNiceMock(UIConstants.class);
        uiMessages = createNiceMock(UIMessages.class);
        replay(uiConstants, uiMessages);

        Log.setCurrentLogLevel(Log.LOG_LEVEL_DEBUG);
    }

    protected void setUser(int userId) {
        user = new User();
        user.setId(userId);
        localAuth = new Authentication(user.getId(), "X", user.getEmail());
    }

    protected void synchronize() {
//    	new UpdateSynchronizer(commandQueue, remoteDispatcher)
//    		.sync(new AsyncCallback<Void>() {
//			
//			@Override
//			public void onSuccess(Void result) {				
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {				
//			}
//		});
    	
    	Synchronizer syncr = new Synchronizer(new MockEventBus(), remoteDispatcher, localDatabase, 
                uiConstants, uiMessages);
        syncr.start(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				throw new AssertionError(caught);
			}

			@Override
			public void onSuccess(Void result) {
				
			}
		});
        localDatabase.processEventQueue();
        
    }

    protected void newRequest() {
    	serverEm.clear();
    }

    private class RemoteDispatcherStub implements Dispatcher {
        @Override
        public <T extends CommandResult> void execute(Command<T> command, AsyncMonitor monitor, AsyncCallback<T> callback) {
            List<CommandResult> results = servlet.handleCommands(user, Collections.<Command>singletonList(command));
            CommandResult result = results.get(0);

            if(result instanceof SyncRegionUpdate) {
                System.out.println(((SyncRegionUpdate) result).getSql());
            }

            if(result instanceof Exception) {
                throw new Error((Throwable) result);
            } else {
                callback.onSuccess((T) result);
            }
        }
    }

}
