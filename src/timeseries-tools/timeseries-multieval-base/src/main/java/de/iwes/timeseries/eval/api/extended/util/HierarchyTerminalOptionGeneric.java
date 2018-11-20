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

import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class HierarchyTerminalOptionGeneric<T extends HierarchySelectionItemGeneric> extends GenericTerminalOption {
	private final HierarchyMultiEvalDataProviderGeneric<T> provider;
	
	public HierarchyTerminalOptionGeneric(String id, String label, LinkingOption[] dependencies,
			HierarchyMultiEvalDataProviderGeneric<T> provider) {
		super(id, label, dependencies);
		this.provider = provider;
	}

	@Override
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies) {
		//if(dependencies.size() != provider.selectionOptions().length-1)
		//	System.out.println("Warning( to be removed) : TerminalOption.getOptions requires dependencies exactly for all non-terminal options.");
			//Note: When a DataProvider has a shorter dependency tree than the governing  DataPovider then
			//the number of dependencies given here might be longer than necessary
			//throw new IllegalArgumentException("TerminalOption.getOptions requires dependencies exactly for all non-terminal options.");
		return provider.getOptions(dependencies, provider.selectionOptions().length-1);
	}

	@Override
	public TimeSeriesData getElement(SelectionItem item) {
		@SuppressWarnings("unchecked")
		T selItem = (T)item;
		return selItem.getTimeSeriesData();
	}
}
