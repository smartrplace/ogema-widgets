package de.iwes.timeseries.eval.api;

import de.iwes.widgets.html.selectiontree.LinkingOptionType;

public interface DataProviderType {
	/**
	 * The selection hierarchy for the data provider. A typical example is the selection of 
	 * data points by first specifying a room in a building, then a device within the room,
	 * and finally the specific data point(s) of the device. The last element must be the
	 *  terminal option.
	 */
	// fed into SelectionTree widget
	LinkingOptionType[] selectionOptions();
}
