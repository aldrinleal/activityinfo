package org.activityinfo.client.page.report.template;

import org.activityinfo.client.dispatch.Dispatcher;
import org.activityinfo.client.i18n.I18N;
import org.activityinfo.shared.report.model.DateDimension;
import org.activityinfo.shared.report.model.DateUnit;
import org.activityinfo.shared.report.model.Dimension;
import org.activityinfo.shared.report.model.DimensionType;
import org.activityinfo.shared.report.model.PivotTableReportElement;
import org.activityinfo.shared.report.model.Report;
import org.activityinfo.shared.report.model.ReportElement;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class PivotTableTemplate extends ReportElementTemplate {

	public PivotTableTemplate(Dispatcher dispatcher) {
		super(dispatcher);
		
		setName(I18N.CONSTANTS.pivotTables());
		setDescription(I18N.CONSTANTS.pivotTableDescription());
		setImagePath("pivot.png");
	}

	@Override
	public void createElement(AsyncCallback<ReportElement> callback) {
		PivotTableReportElement table = new PivotTableReportElement();
		table.addColDimension(new DateDimension(DateUnit.YEAR));
		table.addColDimension(new DateDimension(DateUnit.MONTH));
		table.addRowDimension(new Dimension(DimensionType.Partner));
		
		callback.onSuccess(table);
	}
	
	

}
