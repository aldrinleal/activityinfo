package org.activityinfo.shared.command;

import java.util.List;
import java.util.Set;

import org.activityinfo.shared.command.PivotSites.PivotResult;
import org.activityinfo.shared.command.result.Bucket;
import org.activityinfo.shared.command.result.CommandResult;
import org.activityinfo.shared.report.model.Dimension;
import org.activityinfo.shared.report.model.DimensionType;

import com.google.common.collect.Sets;

public class PivotSites implements Command<PivotResult> {

	public enum ValueType {
		INDICATOR,
		TOTAL_SITES
	}
	
	private Set<Dimension> dimensions;
	private Filter filter;
	private ValueType valueType = ValueType.INDICATOR;
	
	public PivotSites() {
	}
	
	public PivotSites(Set<Dimension> dimensions, Filter filter) {
		super();
		this.dimensions = dimensions;
		this.filter = filter;
	}

	public Set<Dimension> getDimensions() {
		return dimensions;
	}
	
	public Set<DimensionType> getDimensionTypes() {
		Set<DimensionType> set = Sets.newHashSet();
		for(Dimension dim : getDimensions()) {
			set.add(dim.getType());
		}
		return set;
	}

	public void setDimensions(Set<Dimension> dimensions) {
		this.dimensions = dimensions;
	}
	
	public void setDimensions(Dimension... dimensions) {
		this.dimensions = Sets.newHashSet(dimensions);
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	@Override
	public String toString() {
		return "PivotSites [dimensions=" + dimensions + ", filter=" + filter
				+ ", valueType=" + valueType + "]";
	}

	public boolean isPivotedBy(DimensionType dimType) {
		for(Dimension dim : dimensions) {
			if(dim.getType() == dimType) {
				return true;
			}
		}
		return false;
	}


	public static class PivotResult implements CommandResult {
		private List<Bucket> buckets;

		public PivotResult() {
		}
		
		public PivotResult(List<Bucket> buckets) {
			this.buckets = buckets;
		}
		
		public List<Bucket> getBuckets() {
			return buckets;
		}
		
		public void setBuckets(List<Bucket> buckets) {
			this.buckets = buckets;
		}
	}
	
}
