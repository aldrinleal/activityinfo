package org.activityinfo.client.offline.capability;

import org.activityinfo.client.i18n.I18N;
import org.activityinfo.client.offline.ui.BasePromptDialog;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;

public class FFPermissionsDialog extends BasePromptDialog {
	
	public FFPermissionsDialog() {
		super(composeMessage());
		setWidth(450);
		setHeight(250);
		
		
		getButtonBar().add(new Button(I18N.CONSTANTS.help(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				com.google.gwt.user.client.Window.open("http://about.activityinfo.org/firefox-offline-mode", "_blank", null);
			}
		}));

		getButtonBar().add(new Button(I18N.CONSTANTS.reloadPage(), new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				com.google.gwt.user.client.Window.Location.reload();
			}
		}));
		
		
		getButtonBar().add(new Button(I18N.CONSTANTS.cancel(), new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		}));
		
	}

	private static String composeMessage() {
		return "<p>" + I18N.CONSTANTS.ffOfflinePerm1() + "</p>" +
				"<p>" + I18N.CONSTANTS.ffOfflinePerm2() + "</p>";
	}
	
}
