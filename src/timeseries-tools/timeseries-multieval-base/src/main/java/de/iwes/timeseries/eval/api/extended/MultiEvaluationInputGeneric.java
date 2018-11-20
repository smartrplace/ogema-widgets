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
package de.iwes.timeseries.eval.api.extended;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationInputGeneric;
import de.iwes.widgets.html.selectiontree.LinkingOption;

@JsonDeserialize(as = AbstractMultiEvaluationInputGeneric.class)
public interface MultiEvaluationInputGeneric {
	/**Type for which the input is given*/
	DataProviderType type();
	/**A data provider that must provide the LinkingOption signature defined by type*
	 * TODO: The LinkingOption hierarchy must be provided by the order of elements in 
	 * dataProvider().selectionOptions. This is not entirely clear from the current specification
	 */
	List<DataProvider<?>> dataProvider();
	/** For each LinkingOption in the signature one or more of the possible
	 * SelectionItems may be given here. For each usable combination an evaluation shall be performed (accetable
	 * combinations are defined by the data provider).
	 * Usually only for one (or no) LinkingOption more than one value shall be provided.
	 * If no value is provided for a LinkingOption than all available configurations shall be used.
	 */
	MultiEvaluationItemSelector itemSelector();
	//Map<LinkingOptionType, List<SelectionItem>> itemsSelected();
}
