package de.iwes.timeseries.eval.api;

import org.ogema.core.model.simple.SingleValueResource;

public interface RequiredInputData extends LabelledItem {

	public int cardinalityMin();
	public int cardinalityMax();
	
	/**
	 * Define what kind of input data is required. E.g.
	 * <ul>
	 *  <li>SingleValueResource.class for arbitrary input data
	 *  <li>TemperatureResource.class for temperature values
	 *  <li>BooleanResource.class for boolean values, etc.
	 * </ul>
	 * @return
	 */
	public Class<? extends SingleValueResource> requestedInputType();
	
}
