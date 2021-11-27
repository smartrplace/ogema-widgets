package org.ogema.model.chartexportconfig;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.prototypes.Data;

public interface ChartExportConfig extends Data {
	/** If true a separate line is added with labels of time series in addition to locations*/
	BooleanResource addLabels();
	
	/** If false the following values are not relevant*/
	BooleanResource offerFixedStepExport();

	/** If export in common fixed time step is used, this is the time step used. Currently we always align to minutes based on the start
	 * of the interval requested (not the real data).*/
	FloatResource fixedTimeStepSeconds();
	
	/** TODO: Currently this is fixed true if less than 3 values are in the range. If more valued are in the range than averaging is performed
	 * without interpolation. 
	 * 
	 * If false then fixed time step uses the average or interpolation to get a value for a certain time step.
	 * If true then the closest real measurement value is used.
	 */
	//BooleanResource fixedTimestepByClosestValue();
	
	/** Maximum distance of last value in source is found before NaN or empty value is written. Set to 
	 * zero or Long.Max if this shall not occur.*/
	FloatResource maxValidValueIntervalSeconds();

	/** If false then intervals then data for timestamps that are too far away from the closest real data point
	 * are omitted, (nothing between separators). In the case the field naNValue is not relevant.*/
	BooleanResource writeNaN();

	/** If active then the value provided here is used as NaN value in the CSV export. Otherwise NaN is printed.
	 * TODO: Offer option to write nothing between commas.*/
	FloatResource naNValue();
	
}
