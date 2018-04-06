package de.iwes.timeseries.eval.api.configuration;

import java.util.Collection;

import de.iwes.timeseries.eval.api.LabelledItem;
import de.iwes.timeseries.eval.api.ResultType;

/**
 * A configuration type that can be supported by an evaluation provider. The configuration values
 * for specific evaluations are provided by {@link ConfigurationInstance}s. 
 * 
 * @param <C>
 */
public interface Configuration<C extends ConfigurationInstance> extends LabelledItem {

	Class<C> configurationType();
	
	/**
	 * Check if the passed instance is allowed. Otherwise an exception
	 * shall be thrown.
	 * @param instance
	 * @throws IllegalArgumentException
	 */
	void filter(C instance) throws IllegalArgumentException;
	
	/**
	 * 
	 * @return
	 * 		null to indicate that this applies to all result types
	 */
	Collection<ResultType> getApplicableResultTypes();
	
	/**
	 * @return
	 * 		null to indicate that a default value does not exist
	 */
	C defaultValues();
	
	boolean isOptional();
	
}
