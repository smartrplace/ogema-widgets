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
package de.iwes.timeseries.eval.api.extended.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.extended.HierarchyMultiEvalDataProvider;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;

/**The hierarchy data provider only allows access in its {@link TerminalOption#getOptions(List)}
 * with a single selection for each hierarchy element
 * @param <T> source object
 */
public abstract class HierarchyMultiEvalDataProviderGeneric<T extends HierarchySelectionItemGeneric> extends MultiEvalDataProviderGeneric
		implements HierarchyMultiEvalDataProvider<T> {
	private HierarchyTerminalOptionGeneric<T> terminalOption;
	
	public HierarchyMultiEvalDataProviderGeneric(String[] linkingOptionNames) {
		super(null);
		selectionOptions = new LinkingOption[linkingOptionNames.length];
		for(int i=0; i<linkingOptionNames.length; i++) {
			
			LinkingOption[] dependencies;
			if(i==0) dependencies = null;
			else dependencies = new LinkingOption[]	{selectionOptions[i-1]};
			if(i==linkingOptionNames.length-1)
				selectionOptions[i] = terminalOption = new HierarchyTerminalOptionGeneric<T>(linkingOptionNames[i],
						linkingOptionNames[i], dependencies, this);
			else selectionOptions[i] = new HierarchyLinkingOptionGeneric<T>(linkingOptionNames[i],
				linkingOptionNames[i], dependencies, this);
		}
	}
		
	/**
	 * Get the list of items for a dependency level
	 * @param level level of dependency tree for which options shall be provided
	 * @param superItem SelectionItem of the level above. For level 0 this usually is null.
	 * @return list of items available for the level and the superItem requested. The elements must be
	 * 		of type T although the super-interface signature requres a list of SelectionItem
	 */
	protected abstract List<SelectionItem> getOptions(int level, T superItem);
	/** Set gateways to be offered by this data provider instance. This is relevant if e.g. a
	 * MultiEvalation shall not evaluate all gateways in the data set
	 * 
	 * @param gwSelectionItemsToOffer must be a subset of the original result of
	 * {@link #getOptions(int, T)} with the highest level used
	 */
	public abstract void setGatewaysOffered(List<SelectionItem> gwSelectionItemsToOffer);

	@SuppressWarnings("unchecked")
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies, int level) {
		if(level == 0) return getOptions(level, null);
		Collection<SelectionItem> col = dependencies.get(level-1);
		if(col.size() != 1) throw new IllegalArgumentException("HierarchyMultiEvalDataProvider only allows single values for each dependency level in getOptions!");
		Object[] arr = col.toArray();
		T superTerminalItem = (T) arr[0];
		return getOptions(level, superTerminalItem);
	}
	
	@Override
	public EvaluationInput getData(List<SelectionItem> items) {
		List<TimeSeriesData> tsList = new ArrayList<>();
		for(SelectionItem item: items) {
			tsList.add(terminalOption.getElement(item));
		}
		return new EvaluationInputImpl(tsList);
	}

	/*@Override
	public R getResource(SelectionItem item) {
		@SuppressWarnings("unchecked")
		T selItem = (T)item;
		return selItem.getResource();
	}*/
}
