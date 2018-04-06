package de.iwes.timeseries.eval.api;

import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;

/**
 * Provides input data for online and offline evaluation
 * Register instances of this as a service.
 *
 * @param <T>
 */
public interface DataProvider<T> extends LabelledItem, DataProviderType {
	
	/**
	 * The selection hierarchy for the data provider. A typical example is the selection of 
	 * data points by first specifying a room in a building, then a device within the room,
	 * and finally the specific data point(s) of the device.   
	 * @return
	 */
	// fed into SelectionTree widget
	@Override
	LinkingOption[] selectionOptions();
	
	/**
	 * @return
	 * 		the last entry of {@link #selectionOptions()} must be of type
	 * 		{@link TerminalOption}, and is returned here.
	 */
	TerminalOption<? extends ReadOnlyTimeSeries> getTerminalOption();
	
	/**
	 * Converts the selected options into time series. 
	 * @param items
	 * 		must be a list of items provided by the terminal LinkingOption of selectionOptions
	 * @throws IllegalArgumentException 
	 * 		if the selection items are of wrong type
	 * @throws NullPointerException 
	 * 		if any of the input arguments is null
	 * @return
	 */
	EvaluationInput getData(List<SelectionItem> items);
	
}
