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

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationItemSelector;
import de.iwes.widgets.html.selectiontree.LinkingOptionType;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class AbstractMultiEvaluationInputGeneric implements MultiEvaluationInputGeneric {
	protected final DataProviderType type;
	protected final List<DataProvider<?>> dataProvider;
	
	@Override
	public DataProviderType type() {
		return type;
	}

	@Override
	public List<DataProvider<?>> dataProvider() {
		return dataProvider;
	}

	public AbstractMultiEvaluationInputGeneric(DataProviderType type, List<DataProvider<?>> dataProvider) {
		this.type = type;
		this.dataProvider = dataProvider;
	}

	/** !! This constructor is only to be used by the JSON Deserialization, not for other purposes !!*/
	public AbstractMultiEvaluationInputGeneric() {
		this.type = null;
		this.dataProvider = null;
	}

	@Override
	public MultiEvaluationItemSelector itemSelector() {
		return new MultiEvaluationItemSelector() {
			
			@Override
			public boolean useDataProviderItem(LinkingOptionType linkingOptionType, SelectionItem item) {
				return true;
			}
		};
	}

	public Object getInputDefinition() {
		return null;
	}
}
