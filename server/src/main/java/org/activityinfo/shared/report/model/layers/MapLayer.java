package org.activityinfo.shared.report.model.layers;

import java.io.Serializable;
import java.util.List;

import org.activityinfo.shared.command.Filter;

/**
 * A layer representing one or more indicators on a MapElement
 */
public interface MapLayer extends Serializable {

	boolean isVisible();
	void setVisible(boolean isVisible);

	/**
	 * Gets the list of indicators to map. The value at 
	 * each site used for scaling is equal to the sum
	 * of the values of the indicators in this list, or
	 * 1.0 if no indicators are specified.
	 */
	List<Integer> getIndicatorIds();
	void addIndicatorId(int Id);
	
	boolean supportsMultipleIndicators();
	boolean hasMultipleIndicators();
	
	String getName();
	void setName(String name);
	
	
	/*
	 * Function to determine non-typesafe name of the class for gxt template usage
	 */
	String getTypeName();
	
	
	Filter getFilter();
	
	void setFilter(Filter filter);
}