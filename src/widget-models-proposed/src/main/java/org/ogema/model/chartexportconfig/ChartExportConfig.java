package org.ogema.model.chartexportconfig;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

public interface ChartExportConfig extends Data {
	/** If true values are written with comma as decimal separator. Note that we always use semicolon as
	 * CSV separator.*/
	BooleanResource exportGermanExcelCSV();
	
	/** Format to apply to time stamps like yyyy-MM-dd'T'HH:mm:ss. If empty standard
	 * OGEMA time stamps in milliseconds since epoch UTC are exported*/
	StringResource timeStampFormat();

	/** If false and conversion information is available the values are transformed like in the chart*/
	BooleanResource exportOriginalOGEMAValues();
	
	/** If false the following values are not relevant*/
	BooleanResource performFixedStepExport();

	/** If export in common fixed time step is used, this is the time step used. Currently we always align to minutes based on the start
	 * of the interval requested (not the real data).*/
	FloatResource fixedTimeStepSeconds();
	
	/** If true a separate line is added with labels of time series in addition to locations*/
	BooleanResource addLabels();
	
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

	/** Value provided here is used as NaN value in the CSV export. The field can be empty then nothing will be
	 * written between separators.*/
	StringResource naNValue();
	
}
