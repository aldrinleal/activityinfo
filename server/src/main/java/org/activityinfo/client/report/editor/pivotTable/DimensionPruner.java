package org.activityinfo.client.report.editor.pivotTable;

import java.util.Set;
import java.util.logging.Logger;

import org.activityinfo.client.EventBus;
import org.activityinfo.client.dispatch.Dispatcher;
import org.activityinfo.client.page.report.HasReportElement;
import org.activityinfo.client.page.report.ReportChangeHandler;
import org.activityinfo.client.page.report.ReportEventHelper;
import org.activityinfo.shared.command.GetSchema;
import org.activityinfo.shared.dto.ActivityDTO;
import org.activityinfo.shared.dto.IndicatorDTO;
import org.activityinfo.shared.dto.SchemaDTO;
import org.activityinfo.shared.dto.UserDatabaseDTO;
import org.activityinfo.shared.report.model.AttributeGroupDimension;
import org.activityinfo.shared.report.model.Dimension;
import org.activityinfo.shared.report.model.DimensionType;
import org.activityinfo.shared.report.model.PivotTableReportElement;

import com.google.common.collect.Sets;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * Removes inapplicable dimensions from the model after a user change.
 * 
 * <p>For example, if an attribute dimension related to activity X is
 * selected, but all indicators from Activity are removed, then we
 * need to remove the dimension.
 */
public class DimensionPruner implements HasReportElement<PivotTableReportElement>{

	private static Logger LOGGER = Logger.getLogger(DimensionPruner.class.getName());
	
	private final ReportEventHelper events;
	private PivotTableReportElement model;
	private Dispatcher dispatcher;

	@Inject
	public DimensionPruner(EventBus eventBus, Dispatcher dispatcher) {
		super();
		this.dispatcher = dispatcher;
		this.events = new ReportEventHelper(eventBus, this);
		this.events.listen(new ReportChangeHandler() {
			
			@Override
			public void onChanged() {
				DimensionPruner.this.onChanged();
			}
		});
	}

	protected void onChanged() {
		dispatcher.execute(new GetSchema(), new AsyncCallback<SchemaDTO>() {

			@Override
			public void onFailure(Throwable caught) {
			
			}

			@Override
			public void onSuccess(SchemaDTO result) {
				pruneModel(result);
			}
		});
	}

	private void pruneModel(SchemaDTO schema) {
		Set<ActivityDTO> activityIds = getSelectedActivities(schema);
		Set<AttributeGroupDimension> dimensions = getSelectedAttributes(schema);
		boolean dirty = false;
		for(AttributeGroupDimension dim : dimensions) {
			if(!isApplicable(schema, activityIds, dim)) {
				LOGGER.fine("Removing attribute group " + dim.getAttributeGroupId());
				model.getRowDimensions().remove(dim);
				model.getColumnDimensions().remove(dim);
				dirty = true;
			}
		}
		if(dirty) {
			events.fireChange();
		}
	}
	
	private boolean isApplicable(SchemaDTO schema,
			Set<ActivityDTO> activities, AttributeGroupDimension dim) {
	
		for(ActivityDTO activity : activities) {
			if(activity.getAttributeGroupById(dim.getAttributeGroupId()) != null) {
				return true;
			}
		}
		return false;
	}

	private Set<AttributeGroupDimension> getSelectedAttributes(SchemaDTO schema) {
		Set<AttributeGroupDimension> dimensions = Sets.newHashSet();
		for(Dimension dim : model.allDimensions()) {
			if(dim instanceof AttributeGroupDimension) {
				dimensions.add((AttributeGroupDimension) dim);
			}
		}
		return dimensions;
	}

	private Set<ActivityDTO> getSelectedActivities(SchemaDTO schema) {
		Set<ActivityDTO> activities = Sets.newHashSet();
		Set<Integer> indicatorIds = Sets.newHashSet(
				model.getFilter().getRestrictions(DimensionType.Indicator));
		for(UserDatabaseDTO db : schema.getDatabases()) {
			for(ActivityDTO activity : db.getActivities()) {
				for(IndicatorDTO indicator : activity.getIndicators()) {
					if(indicatorIds.contains(indicator.getId())) {
						activities.add(activity);
					}
				}
			}
		}
		return activities;
	}

	@Override
	public void bind(PivotTableReportElement model) {
		this.model = model;
	}

	@Override
	public PivotTableReportElement getModel() {
		return model;
	}

	@Override
	public void disconnect() {
		events.disconnect();
	}
}
