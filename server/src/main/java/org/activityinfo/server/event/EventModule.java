package org.activityinfo.server.event;

import org.activityinfo.server.event.sitechange.SiteChangeListener;
import org.activityinfo.server.event.sitechange.SiteChangeServlet;
import org.activityinfo.server.event.sitehistory.SiteHistoryListener;

import com.google.inject.servlet.ServletModule;

public class EventModule extends ServletModule {

	@Override
	protected void configureServlets() {
		// eventbus
		bind(ServerEventBus.class).asEagerSingleton();

		// listeners
		bind(SiteChangeListener.class).asEagerSingleton();
		bind(SiteHistoryListener.class).asEagerSingleton();
		
		// define endpoints for async callbacks
		serve(SiteChangeServlet.ENDPOINT).with(SiteChangeServlet.class);
	}
}
