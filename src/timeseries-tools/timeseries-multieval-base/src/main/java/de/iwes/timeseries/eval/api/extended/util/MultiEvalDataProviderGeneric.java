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

import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;

public abstract class MultiEvalDataProviderGeneric implements DataProvider<TimeSeriesData> {
	public MultiEvalDataProviderGeneric(LinkingOption[] selectionOptions) {
		this.selectionOptions = selectionOptions;
	}

	protected LinkingOption[] selectionOptions;
	
	@Override
	public LinkingOption[] selectionOptions() {
		return selectionOptions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TerminalOption<? extends ReadOnlyTimeSeries> getTerminalOption() {
		return (TerminalOption<? extends ReadOnlyTimeSeries>) selectionOptions[selectionOptions.length-1];
	}

	@Override
	public abstract EvaluationInput getData(List<SelectionItem> items);

	@Override
	public String id() {
		return "defaultInputForMultiEvaluations";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Default Input for MultiEvaluations";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "This is default input for MultiEvaluations";
	}

	//@Override
	//public abstract R getResource(SelectionItem item);

}
