package org.activityinfo.server.endpoint.refine;

import javax.inject.Singleton;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class RefineModule extends ServletModule {

	@Override
	protected void configureServlets() {
		bind(ReconciliationService.class);
		bind(RefineIndexTask.class);
		bind(JacksonJsonProvider.class).in(Singleton.class);
		filter("/reconcile*").through(GuiceContainer.class);
		filter("/tasks/refine/index").through(GuiceContainer.class);
	}
}
