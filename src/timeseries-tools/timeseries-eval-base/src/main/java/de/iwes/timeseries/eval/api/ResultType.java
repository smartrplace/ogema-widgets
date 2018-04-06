package de.iwes.timeseries.eval.api;

public interface ResultType extends LabelledItem {
	
	public enum ResultStructure {
		
		/**
		 * One evaluation result per time series
		 */
		PER_INPUT,
		
		/**
		 * A single result for a combination of time series 
		 */
		COMBINED
		
	}
	
	public enum ValueType {
		
		NUMERIC,
		TIME_SERIES
		
	}
	
	/*public enum Cardinality {
		
		SINGLE_VALUE,
		ARRAY
		
	}*/
	
	ResultStructure resultStructure();
	ValueType valueType();
	//Cardinality cardinality();
	/**If true the result is a single value, otherwise it is an array*/
	Boolean isSingleValueOrArray();

}
