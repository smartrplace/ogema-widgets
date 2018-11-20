/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	 * Register as an OSGi service with a unique value for this property
	 */
	public static final String PROVIDER_ID = "provider-id";
	
	public static final String PROVIDER_LABEL= "provider-label";
	
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
