/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sync;

import java.util.Date;

import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.DirectDispatcher;
import org.sigmah.client.offline.command.LocalDispatcher;
import org.sigmah.client.offline.install.InstallSteps;
import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.Ping;
import org.sigmah.shared.command.result.VoidResult;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class SynchronizerImpl implements Synchronizer {
    private static final int SYNC_INTERVAL = 30000;

    private final Provider<InstallSteps> installSteps;
    private final LocalDispatcher localDispatcher;
    private final Dispatcher remoteDispatcher;
    private final DownSynchronizer synchronizer;
    private final UpdateSynchronizer updateSynchronizer;


    @Inject
    public SynchronizerImpl(Provider<InstallSteps> installSteps,
                       LocalDispatcher localDispatcher,
                       DirectDispatcher remoteDispatcher,
                       DownSynchronizer synchronizer,
                       UpdateSynchronizer updateSynchronizer) {

        this.installSteps = installSteps;
        this.localDispatcher = localDispatcher;
        this.remoteDispatcher = remoteDispatcher;
        this.synchronizer = synchronizer;
        this.updateSynchronizer = updateSynchronizer;
    }

    @Override
    public void install(final AsyncCallback<Void> callback) {
    	
        installSteps.get().run(new AsyncCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(Object o) {
                goOffline(callback);
            }
        });
    }

    @Override
    public void goOffline(AsyncCallback<Void> callback) {
        callback.onSuccess(null);
    }

    @Override
    public void getLastSyncTime(AsyncCallback<java.util.Date> callback) {
        synchronizer.getLastUpdateTime(callback);
    }

    @Override
    public void validateOfflineInstalled(final AsyncCallback<Void> callback) {
    	synchronizer.getLastUpdateTime(new AsyncCallback<Date>() {
			
			@Override
			public void onSuccess(Date result) {
				if(result == null) {
					callback.onFailure(new RuntimeException("Never synchronized"));
				} else {
					callback.onSuccess(null);
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				
			}
		});
    }

    @Override
    public void synchronize(final AsyncCallback<Void> callback) {
    	// send updates to server, then on success request 
    	// updates from server
    	updateSynchronizer.sync(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void arg0) {
		    	synchronizer.start(callback); 
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
    }

    @Override
    public void goOnline(final AsyncCallback<Void> callback) {
    
    	updateSynchronizer.sync(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
		    	verifyConnectivity(callback);
				
			}
		});    	
    }

	private void verifyConnectivity(final AsyncCallback<Void> callback) {
		// confirm that we can connect to the server
    	remoteDispatcher.execute(new Ping(), null, new AsyncCallback<VoidResult>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				
			}

			@Override
			public void onSuccess(VoidResult result) {
				callback.onSuccess(null);
			}
		});
	}

	@Override
	public boolean canHandle(Command command) {
		return localDispatcher.canExecute(command);
	}

	@Override
	public void execute(Command command, AsyncMonitor monitor,
			AsyncCallback callback) {
		
		localDispatcher.execute(command, monitor, callback);
		
	}
}