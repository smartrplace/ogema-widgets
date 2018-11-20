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

import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.extended.util.HierarchySelectionItemGeneric;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;
import de.iwes.widgets.template.LabelledItem;

/**The hierarchy data provider only allows access in its {@link TerminalOption#getOptions(List)}
 * with a single selection for each hierarchy element
 * @param <T> source object
 */
public interface HierarchyMultiEvalDataProvider<T extends HierarchySelectionItemGeneric> extends LabelledItem {
	/**
	 * Get the list of items for a dependency level
	 * @param level level of dependency tree for which options shall be provided
	 * @param superItem SelectionItem of the level above. For level 0 this usually is null.
	 * @return list of items available for the level and the superItem requested. The elements must be
	 * 		of type T although the super-interface signature requres a list of SelectionItem
	 */
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies, int level);
	/** Set gateways to be offered by this data provider instance. This is relevant if e.g. a
	 * MultiEvalation shall not evaluate all gateways in the data set
	 * 
	 * @param gwSelectionItemsToOffer must be a subset of the original result of
	 * {@link #getOptions(int, T)} with the highest level used
	 */
	public void setGatewaysOffered(List<SelectionItem> gwSelectionItemsToOffer);

	public EvaluationInput getData(List<SelectionItem> items);

	//public R getResource(SelectionItem item);
}
