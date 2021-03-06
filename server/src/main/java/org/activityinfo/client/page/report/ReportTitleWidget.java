package org.activityinfo.client.page.report;

import org.activityinfo.client.i18n.I18N;

import com.extjs.gxt.ui.client.Style.AutoSizeMode;
import com.extjs.gxt.ui.client.event.EditorEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Editor;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ReportTitleWidget extends Widget implements HasText {

	private static ReportTitleWidgetUiBinder uiBinder = GWT
			.create(ReportTitleWidgetUiBinder.class);

	interface ReportTitleWidgetUiBinder extends
			UiBinder<Element, ReportTitleWidget> {
	}
	
	interface MyStyle extends CssResource {
		String reportTitle();
		String reportTitleEditor();
		String changeTitleText();
	}

	@UiField
	SpanElement titleSpan;
	
	@UiField
	MyStyle style;

	private Editor titleEditor;
	
	private String text;

	public ReportTitleWidget() {
		setElement(uiBinder.createAndBindUi(this));

		TextField<String> titleField = new TextField<String>();
		titleField.addInputStyleName(style.reportTitleEditor());
		
		titleEditor = new Editor(titleField);
		titleEditor.setAutoSizeMode(AutoSizeMode.HEIGHT);
		titleEditor.setConstrain(true);
		titleEditor.setAlignment("cl");
		titleEditor.setWidth(400);
		titleEditor.setHeight(16);
		titleEditor.setCompleteOnEnter(true);
		
		sinkEvents(Event.ONCLICK);
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
		titleSpan.setInnerText(text == null ? I18N.CONSTANTS.untitledReport() : text);
	}

	@Override
	public void onBrowserEvent(Event event) {
		Element clicked = event.getEventTarget().cast();
		if(clicked.getClassName().equals(style.reportTitle()) ||
		   clicked.getClassName().equals(style.changeTitleText())) {
			titleEditor.startEdit(  (com.google.gwt.user.client.Element) titleSpan.cast(), text);
		}
	}

	public void addEditCompleteListener(Listener<EditorEvent> listener) {
		titleEditor.addListener(Events.Complete, listener);
	}
}
