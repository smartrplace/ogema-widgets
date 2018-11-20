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

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public abstract class HierarchySelectionItemGeneric implements SelectionItem {
	protected final int level;
	protected final String id;

	public HierarchySelectionItemGeneric(int level, String id) {
		this.level = level;
		this.id = id;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public String label(OgemaLocale locale) {
		return id();
	}
	
	//public abstract T getResource();
	
	/**only relevant for SelectionItems of TerminalOption, otherwise just return null*/
	public abstract TimeSeriesData getTimeSeriesData();
}
