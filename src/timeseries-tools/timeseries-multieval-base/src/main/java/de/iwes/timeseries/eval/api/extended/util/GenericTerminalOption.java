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
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;

public abstract class GenericTerminalOption extends TerminalOption<TimeSeriesData> {
	private final String id;
	private final String label;
	private final LinkingOption[] dependencies;
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public String label(OgemaLocale locale) {
		return label;
	}

	@Override
	public LinkingOption[] dependencies() {
		return dependencies;
	}

	public GenericTerminalOption(String id, String label, LinkingOption[] dependencies) {
		super();
		this.id = id;
		this.label = label;
		this.dependencies = dependencies;
	}

	@Override
	public abstract List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies);
}
