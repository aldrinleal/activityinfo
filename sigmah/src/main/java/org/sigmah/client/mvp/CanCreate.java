package org.sigmah.client.mvp;

import org.sigmah.shared.dto.DTO;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public interface CanCreate<M extends DTO> {
	
	public interface CreateHandler extends EventHandler {
		void onCreate(CreateEvent createEvent);
	}
	public interface CancelCreateHandler extends EventHandler {
		void onCancelCreate(CancelCreateEvent createEvent);
	}
	public interface StartCreateHandler extends EventHandler {
		void onStartCreate(StartCreateEvent createEvent);
	}
	// The user signals the presenter it wants to add a new item
	public HandlerRegistration addCreateHandler(CreateHandler handler);

	// The user signals the presenter it wants to add a new item
	public HandlerRegistration addCancelCreateHandler(CancelCreateHandler handler);

	// The user signals the presenter it wants to add a new item
	public HandlerRegistration addStartCreateHandler(StartCreateHandler handler);
	
	// An item is created by the presenter, this method adds the item to the view 
	public void create(M item);
	
	public void setCreateEnabled(boolean createEnabled);

	// The Presenter has a seperate view for creating/updating domain object
	public class CreateEvent extends GwtEvent<CreateHandler> {
		public static Type TYPE = new Type<CreateHandler>(); 
		
		@Override
		public Type<CreateHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(CreateHandler handler) {
			handler.onCreate(this);
		}
	}

	// The Presenter has a seperate view for creating/updating domain object
	public class CancelCreateEvent extends GwtEvent<CancelCreateHandler> {
		public static Type TYPE = new Type<CancelCreateHandler>(); 
		
		@Override
		public Type<CancelCreateHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(CancelCreateHandler handler) {
			handler.onCancelCreate(this);
		}
	}

	// The Presenter has a seperate view for creating/updating domain object
	public class StartCreateEvent extends GwtEvent<StartCreateHandler> {
		public static Type TYPE = new Type<StartCreateHandler>(); 
		
		@Override
		public Type<StartCreateHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(StartCreateHandler handler) {
			handler.onStartCreate(this);
		}
	}
}