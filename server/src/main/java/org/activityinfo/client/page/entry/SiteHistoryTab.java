package org.activityinfo.client.page.entry;

import org.activityinfo.client.dispatch.Dispatcher;
import org.activityinfo.client.i18n.I18N;
import org.activityinfo.shared.command.GetSchema;
import org.activityinfo.shared.command.GetSiteHistory;
import org.activityinfo.shared.command.GetSiteHistory.GetSiteHistoryResult;
import org.activityinfo.shared.dto.SchemaDTO;
import org.activityinfo.shared.dto.SiteDTO;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SiteHistoryTab extends TabItem {

	private final Html content;
	private final Dispatcher dispatcher;
	
	public SiteHistoryTab(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
		this.setScrollMode(Scroll.AUTO);
		
		setText(I18N.CONSTANTS.history());
		
		content = new Html();
		content.setStyleName("details");
		add(content);
	}
	
	public void setSite(final SiteDTO site) {
		dispatcher.execute(new GetSiteHistory(site.getId()), new AsyncCallback<GetSiteHistoryResult>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			@Override
			public void onSuccess(final GetSiteHistoryResult historyResult) {
				dispatcher.execute(new GetSchema(), new AsyncCallback<SchemaDTO>() {
					@Override
					public void onFailure(Throwable caught) {
					}
					@Override
					public void onSuccess(SchemaDTO schema) {
						render(schema, site, historyResult);
					}
				});
			}
		});
	}

	private void render(SchemaDTO schema, SiteDTO site, GetSiteHistoryResult historyResult) {
		content.setHtml(new SiteHistoryRenderer().render(schema, site, historyResult));
	}
}
