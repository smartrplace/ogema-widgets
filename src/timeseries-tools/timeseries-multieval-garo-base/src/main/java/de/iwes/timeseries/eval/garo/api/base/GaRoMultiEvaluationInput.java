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
package de.iwes.timeseries.eval.garo.api.base;

import java.util.Arrays;
import java.util.List;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationItemSelector;
import de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;
import de.iwes.widgets.html.selectiontree.LinkingOptionType;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class GaRoMultiEvaluationInput extends AbstractMultiEvaluationInputGeneric {
	protected final GaRoDataTypeI terminalDataType;
	private final List<String> topLevelIdsToEvaluate;
	private final String topLevelOptionId;
	//private final LinkingOptionType terminalOptionType;
	
	public GaRoMultiEvaluationInput(DataProviderType type, DataProvider<?> dataProvider,
			GaRoDataTypeI terminalDataType, List<String> topLevelIdsToEvaluate, String topLevelOptionId) {
		this(type, Arrays.asList(new DataProvider[] {dataProvider}), terminalDataType, topLevelIdsToEvaluate, topLevelOptionId);
	}
	public GaRoMultiEvaluationInput(DataProviderType type, List<DataProvider<?>> dataProviders,
			GaRoDataTypeI terminalDataType, List<String> topLevelIdsToEvaluate, String topLevelOptionId) {
		super(type, dataProviders);
		this.terminalDataType = terminalDataType;
		this.topLevelIdsToEvaluate = topLevelIdsToEvaluate;
		this.topLevelOptionId = topLevelOptionId;
		//this.terminalOptionType = type.selectionOptions()[type.selectionOptions().length-1];
	}
	
	@Override
	public MultiEvaluationItemSelector itemSelector() {
		return new MultiEvaluationItemSelector() {
			@Override
			public boolean useDataProviderItem(LinkingOptionType linkingOptionType, SelectionItem item) {
				if(linkingOptionType.id().equals(topLevelOptionId)) {
					if(topLevelIdsToEvaluate == null) return true;
					GaRoSelectionItem gsi = (GaRoSelectionItem)item;
					if(gsi.gwSelectionItem == null) return topLevelIdsToEvaluate.contains(gsi.id());
					else if(topLevelIdsToEvaluate.contains(gsi.gwSelectionItem.id())) return true;
					return false;
				}
				if(linkingOptionType.id().equals(GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID)) return true;
				if(linkingOptionType.id().equals(GaRoMultiEvalDataProvider.ROOM_LINKINGOPTION_ID)) return true;
				if(terminalDataType == GaRoDataType.Any) return true;
				GaRoSelectionItem gsi = (GaRoSelectionItem)item;
				return useDataProviderItemTerminal(gsi);
				//return GaRoEvalHelper.getDataType(gsi.id()).label(null).equals(terminalDataType.label(null));
				//return GaRoEvalHelper.getDataType(gsi.id()) == terminalDataType;
			}
		};
	}
	
	protected boolean useDataProviderItemTerminal(GaRoSelectionItem item) {
		return GaRoEvalHelper.getDataType(item.id()).label(null).equals(terminalDataType.label(null));		
	}
	
	@Override
	public GaRoDataTypeI getInputDefinition() {
		return terminalDataType;
	}
}
