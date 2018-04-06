package de.iwes.timeseries.eval.api.extended;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationInputGeneric;

@JsonDeserialize(as = AbstractMultiEvaluationInputGeneric.class)
public interface MultiEvaluationInputGeneric<R> {
	/**Type for which the input is given*/
	DataProviderType type();
	/**A data provider that must provide the LinkingOption signature defined by type*
	 * TODO: The LinkingOption hierarchy must be provided by the order of elements in 
	 * dataProvider().selectionOptions. This is not entirely clear from the current specification
	 */
	DataProviderResInfoGeneric<R, ?> dataProvider();
	/** For each LinkingOption in the signature one or more of the possible
	 * SelectionItems may be given here. For each usable combination an evaluation shall be performed (accetable
	 * combinations are defined by the data provider).
	 * Usually only for one (or no) LinkingOption more than one value shall be provided.
	 * If no value is provided for a LinkingOption than all available configurations shall be used.
	 */
	MultiEvaluationItemSelector itemSelector();
	//Map<LinkingOptionType, List<SelectionItem>> itemsSelected();
}
