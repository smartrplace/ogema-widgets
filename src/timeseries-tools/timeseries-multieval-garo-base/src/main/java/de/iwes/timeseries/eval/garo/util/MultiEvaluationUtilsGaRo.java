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
package de.iwes.timeseries.eval.garo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationItemSelector;
import de.iwes.timeseries.eval.api.extended.MultiResult;
import de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationInstance;
import de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationProvider;
import de.iwes.timeseries.eval.api.extended.util.MultiEvaluationUtils;
import de.iwes.timeseries.eval.api.extended.util.MultiEvaluationUtils.TimeSeriesInputForSingleRequiredInputIdx;
import de.iwes.timeseries.eval.api.extended.util.TimeSeriesDataExtendedImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.eval.garo.api.base.EvaluationInputImplGaRo;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeParam;
import de.iwes.util.resource.ResourceHelper.DeviceInfo;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class MultiEvaluationUtilsGaRo {
	/** Get time series that will be evaluated for a certain input configuration.
	 * Note that {@link AbstractMultiEvaluationInstance#startInputLevel(List, List, int, MultiResult)}
	 * will not be evaluated here to reduce the time series actually used.
	 * Also {@link #finishInputLevel(int, MultiResult)} is not called here and no status information
	 * is processed. Of course, also {@link #evaluateDataSet(List, List[], MultiResult)} is not
	 * called here. Note that this method also does not take into account a roomType-limitation of
	 * the EvaluationProvider.
	 * @param dp DataProvider to use
	 * @param provider EvaluationProvider to use
	 * @return the result is not structured here in contrast would is needed for real evaluation.
	 * 		All input time series are just collected in a list. We do not check for the same time
	 * 		series being in the list here. We get new TimeSeriesData object usually, so using
	 * 		a Set would probably not help
	 */
	public static <SI extends SelectionItem> List<TimeSeriesData> getFittingTSforEval(DataProvider<?> dp,
			AbstractMultiEvaluationProvider<?> provider,
			List<MultiEvaluationInputGeneric> input) {
        
		MultiEvaluationInputGeneric governingInput2 = input.get(provider.maxTreeIndex);
        MultiEvaluationItemSelector governingItemsSelected2 = input.get(provider.maxTreeIndex).itemSelector();
        LinkingOption[] linkingOptions2 = governingInput2.dataProvider().get(0).selectionOptions(); //getLinkingOptions(governingInput2);
		return executeAllOfLevelStatic(provider, 0, new ArrayList<SI>(), new ArrayList<Collection<SelectionItem>>(),
				linkingOptions2, governingItemsSelected2, input, true);		
	}
	@SuppressWarnings("unchecked")
	public static <SI extends SelectionItem> List<TimeSeriesData> executeAllOfLevelStatic(
			AbstractMultiEvaluationProvider<?> provider,
			int level,
			ArrayList<SI> upperDependencies2,
			List<Collection<SelectionItem>> upperDependencies,
			LinkingOption[] linkingOptions,
			MultiEvaluationItemSelector governingItemsSelected,
			List<MultiEvaluationInputGeneric> input, boolean addContextData) {

		List<SelectionItem> levelOptionsAll = linkingOptions[level].getOptions(upperDependencies);
		List<SI> levelOptions = new ArrayList<>();
		for(SelectionItem lvlAll: levelOptionsAll) {
			if(governingItemsSelected.useDataProviderItem(linkingOptions[level], lvlAll))
				levelOptions.add((SI) lvlAll);
		}

		if(level >= (provider.maxTreeSize-1)) {
			//we should execute
			TimeSeriesInputForSingleRequiredInputIdx[] currentData = MultiEvaluationUtils.getDataForInput(input, upperDependencies);
			List<TimeSeriesData> result= new ArrayList<>();
			if(addContextData) {
				int idx = 0;
				for(TimeSeriesInputForSingleRequiredInputIdx cd: currentData) {
					MultiEvaluationInputGeneric multiGen = input.get(idx);
					final Object inputDef;
					if(multiGen instanceof AbstractMultiEvaluationInputGeneric) {
						inputDef = ((AbstractMultiEvaluationInputGeneric)multiGen).getInputDefinition();
					} else inputDef = null;
					List<DeviceInfo> devList = null;
					if(inputDef instanceof GaRoDataTypeParam) {
						devList = new ArrayList<>();
						for(EvaluationInput dp: cd.eiList) {
							if(dp instanceof EvaluationInputImplGaRo) {
								devList.addAll(((EvaluationInputImplGaRo)dp).getDeviceInfo());
							}
						}
					}
					int tsIdx = 0;
					for(TimeSeriesData ts: cd.tsList) {
						if(ts instanceof TimeSeriesDataImpl && (!(ts instanceof TimeSeriesDataExtendedImpl))) {
							List<String> ids = new ArrayList<>();
							for(SI dep: upperDependencies2) {
								ids.add(dep.id());
							}
							//if(inputDef instanceof GaRoDataTypeParam) {
							//	GaRoDataTypeParam inputDefG = (GaRoDataTypeParam)inputDef;
							//	inputDefG.deviceInfo = Arrays.asList(new DeviceInfo[] {devList.get(tsIdx)});
							//}
							TimeSeriesDataExtendedImpl tsExt = new TimeSeriesDataExtendedImpl((TimeSeriesDataImpl)ts, ids, inputDef);
							if(devList != null) {
								DeviceInfo di = devList.get(tsIdx);
								tsExt.addProperty("deviceName", di.getDeviceName());
								tsExt.addProperty("deviceResourceLocation", di.getDeviceResourceLocation());
							}
							result.add(tsExt);
						} else
							result.add(ts);
						tsIdx++;
					}
					idx++;
				}
			} else
				for(TimeSeriesInputForSingleRequiredInputIdx cd: currentData) result.addAll(cd.tsList);
			return result;

		} else {
			List<TimeSeriesData> result = new ArrayList<>();
			for(SelectionItem item: levelOptions) {
				ArrayList<SelectionItem> newDependency = new ArrayList<>();
				newDependency.add(item);
				upperDependencies.add(newDependency);
				upperDependencies2.add((SI) item);
				/*if(addContextData) {
					List<TimeSeriesData> base = executeAllOfLevelStatic(provider, level+1, upperDependencies2, upperDependencies,
							linkingOptions, governingItemsSelected, input, addContextData);
					for(TimeSeriesData ts: base) {
						if(ts instanceof TimeSeriesDataImpl && (!(ts instanceof TimeSeriesDataExtendedImpl))) {
							List<String> ids = new ArrayList<>();
							for(SI dep: upperDependencies2) {
								ids.add(dep.id());
							}
							MultiEvaluationInputGeneric multiGen = input.get(0);
							DataProviderType types = multiGen.type();
							MultiEvaluationItemSelector type = multiGen.itemSelector();
							result.add(new TimeSeriesDataExtendedImpl((TimeSeriesDataImpl)ts, ids, type));
						} else
							result.add(ts);
					}
				} else*/
				result.addAll(executeAllOfLevelStatic(provider, level+1, upperDependencies2, upperDependencies,
					linkingOptions, governingItemsSelected, input, addContextData));
				//if(status != StatusImpl.RUNNING) return;
				upperDependencies.remove(upperDependencies.size()-1);
				upperDependencies2.remove(upperDependencies2.size()-1);
			}
			return result;
		}
	}
 }
