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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.DateConfiguration;
import de.iwes.timeseries.eval.api.configuration.StartEndConfiguration;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationItemSelector;
import de.iwes.timeseries.eval.api.extended.MultiResult;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationUtils;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class MultiEvaluationUtils {
	/** Export result of MultiEvaluationInstance to json
	 * 
	 * @param fileName
	 * @param multiResult all public fields are serialized into a JSON (recursive)
	 */
	public static void exportToJSONFile(String fileName, Object multiResult) {
		File file = new File(fileName);
		file.getParentFile().mkdirs();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			mapper.writeValue(file, multiResult);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	/**Import data from CSV into a given MultiResult class. Data in the file that does not fit
	 * into the result object is omitted. Elements that are not found in the file shall be null
	 * or default if null is not allowed.
	 * @param file
	 * @param structure
	 * @return object with result
	 */
	/*public static <M extends MultiResult> M importFromJSON(FileInputStream file, Class<M> structure) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(file, structure);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}*/
	
	public static <M> M importFromJSON(String fileName, Class<M> structure) {
		File file = new File(fileName);

		JacksonNonBlockingObjectMapperFactory factory = new JacksonNonBlockingObjectMapperFactory();
		factory.setJsonDeserializers(Arrays.asList(new StdDeserializer[]{
		    // StdDeserializer, here, comes from Jackson (org.codehaus.jackson.map.deser.StdDeserializer)
		    new NumberDeserializers.ShortDeserializer(Short.class, null),
		    new NumberDeserializers.IntegerDeserializer(Integer.class, null),
		    new NumberDeserializers.CharacterDeserializer(Character.class, null),
		    new NumberDeserializers.LongDeserializer(Long.class, null),
		    new NumberDeserializers.FloatDeserializer(Float.class, null),
		    new NumberDeserializers.DoubleDeserializer(Double.class, null),
		    new NumberDeserializers.NumberDeserializer(),
		    new NumberDeserializers.BigDecimalDeserializer(),
		    new NumberDeserializers.BigIntegerDeserializer()
		    //new StdDeserializer.CalendarDeserializer()
		}));
		ObjectMapper mapper = factory.createObjectMapper();
		//ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
		mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			M result = mapper.readValue(file, structure);
			if(result instanceof AbstractSuperMultiResult) {
				AbstractSuperMultiResult<?> superRes = (AbstractSuperMultiResult<?>)result;
				long fileTime = file.lastModified();
				for(MultiResult ir: superRes.intervalResults) {
					if(ir instanceof AbstractMultiResult)
						((AbstractMultiResult)ir).timeOfCalculation = fileTime;
				}
			}
			return result;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	/** Export to CSV structure flattening everything into a matrix structure. For single values and arrays
	 * up to 255 values the path elements of the respective JSON representation are given in the initial
	 * six columns of a row, afterwards the values. If more than six path elements are required, the
	 * sixth column shall contain the entire remaining path with slashes as separators.
	 * Time series are all given in a column last with common time stamps.
	 * Note: Import from such CSV is not foreseen currently.
	 * @param file
	 * @param multiResult
	 * @return number of top-level elements serialized
	 */
	public static int exportToCSVFile(FileOutputStream file, MultiResult multiResult) {
		//TODO
		throw new UnsupportedOperationException();
	}
	
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}

    /**
     * Add the mandatory start/end time information to the list of
     * configurations or create list with this information if not yet existing
     *
     * @param tsList list of input time series from which start/end shall be
     * determined
     * @param configurations may be null if no other configurations shall be set
     * for the evaluation
     * @return list of configurations with start and end time
     */
    public static Collection<ConfigurationInstance> addStartEndTime(final List<ReadOnlyTimeSeries> tsList, Collection<ConfigurationInstance> configurations) {
        if (configurations == null) {
            configurations = new ArrayList<>();
        }
        long startTime = EvaluationUtils.getDefaultStartEndTimeForInput(tsList, true);
        ConfigurationInstance config = new DateConfiguration(startTime, StartEndConfiguration.START_CONFIGURATION);
        configurations.add(config);
        long endTime = EvaluationUtils.getDefaultStartEndTimeForInput(tsList, false);
        config = new DateConfiguration(endTime, StartEndConfiguration.END_CONFIGURATION);
        configurations.add(config);
        return configurations;
    }
    
    public static class JacksonNonBlockingObjectMapperFactory {

        /**
         * Deserializer that won't block if value parsing doesn't match with target type
         * @param <T> Handled type
         */
        private static class NonBlockingDeserializer<T> extends JsonDeserializer<T> {
            private StdDeserializer<T> delegate;

            public NonBlockingDeserializer(StdDeserializer<T> _delegate){
                this.delegate = _delegate;
            }

            @Override
            public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                try {
                    return delegate.deserialize(jp, ctxt);
                }catch (JsonMappingException e){
                    // If a JSONMappingException occurs, simply returning null instead of blocking things
                    return null;
                }
            }
        }

        @SuppressWarnings("rawtypes")
		private List<StdDeserializer> jsonDeserializers = new ArrayList<StdDeserializer>();

        @SuppressWarnings({ "unchecked", "rawtypes"})
		public ObjectMapper createObjectMapper(){
            ObjectMapper objectMapper = new ObjectMapper();

            SimpleModule customJacksonModule = new SimpleModule("customJacksonModule", new Version(0, 0, 0, null, null, null));
            //SimpleModule customJacksonModule = new SimpleModule("customJacksonModule", new Version(1, 0, 0, null));
            for(StdDeserializer jsonDeserializer : jsonDeserializers){
                // Wrapping given deserializers with NonBlockingDeserializer
                customJacksonModule.addDeserializer(jsonDeserializer.handledType(), new NonBlockingDeserializer(jsonDeserializer));
            }

            objectMapper.registerModule(customJacksonModule);
            return objectMapper;
        }

        @SuppressWarnings("rawtypes")
		public JacksonNonBlockingObjectMapperFactory setJsonDeserializers(List<StdDeserializer> _jsonDeserializers){
            this.jsonDeserializers = _jsonDeserializers;
            return this;
        }
    }
    
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
			List<TimeSeriesData>[] currentData = getDataForInput(input, upperDependencies);
			List<TimeSeriesData> result= new ArrayList<>();
			if(addContextData) {
				int idx = 0;
				for(List<TimeSeriesData> cd: currentData) {
					MultiEvaluationInputGeneric multiGen = input.get(idx);
					final Object inputDef;
					if(multiGen instanceof AbstractMultiEvaluationInputGeneric) {
						inputDef = ((AbstractMultiEvaluationInputGeneric)multiGen).getInputDefinition();
					} else inputDef = null;
					for(TimeSeriesData ts: cd) {
						if(ts instanceof TimeSeriesDataImpl && (!(ts instanceof TimeSeriesDataExtendedImpl))) {
							List<String> ids = new ArrayList<>();
							for(SI dep: upperDependencies2) {
								ids.add(dep.id());
							}
							result.add(new TimeSeriesDataExtendedImpl((TimeSeriesDataImpl)ts, ids, inputDef));
						} else
							result.add(ts);
					}
					idx++;
				}
			} else
				for(List<TimeSeriesData> cd: currentData) result.addAll(cd);
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

	
	@SuppressWarnings("unchecked")
	public static <SI extends SelectionItem> List<TimeSeriesData>[] getDataForInput(
			List<MultiEvaluationInputGeneric> input,
			List<Collection<SelectionItem>> upperDependencies) {
		List<TimeSeriesData>[] currentData = new List[input.size()];
		for(int tsIdx = 0; tsIdx < input.size(); tsIdx++) {
			//first we call start level also here
			MultiEvaluationInputGeneric in = input.get(tsIdx);
			List<SelectionItem> levelOptionsTerminalAll = new ArrayList<>();
			for(DataProvider<?> dp: in.dataProvider()) {
				levelOptionsTerminalAll.addAll(dp.getTerminalOption().getOptions(upperDependencies));
			}
			//List<SelectionItem> levelOptionsTerminalAll = in.dataProvider().getTerminalOption().getOptions(upperDependencies);
			List<SI> levelOptionsTerminal = new ArrayList<>();
			for(SelectionItem lvlAll: levelOptionsTerminalAll) {
				for(DataProvider<?> dp: in.dataProvider()) {
					if(in.itemSelector().useDataProviderItem(dp.getTerminalOption(), lvlAll))
					//if(in.itemSelector().useDataProviderItem(in.dataProvider().getTerminalOption(), lvlAll))
						levelOptionsTerminal.add((SI) lvlAll);
				}
			}
			//try {
			//	levelOptionsTerminal = startInputLevel(levelOptionsTerminal, upperDependencies2, level+1, result);
			//} catch(Exception e) {
			//	e.printStackTrace();
			//	throw e;
			//}

			// The following is dead code
			//if(levelOptionsTerminal == null) return null;
			
			//now we actually get the data for an evaluation
			//EvaluationInputImpl evalInput = null;
			List<TimeSeriesData> tsList = new ArrayList<>();;
			for(DataProvider<?> dp: in.dataProvider()) {
				EvaluationInput evalInputLoc = dp.getData((List<SelectionItem>) levelOptionsTerminal);
				tsList.addAll(evalInputLoc.getInputData());
			}
			//EvaluationInput evalInput = in.dataProvider().getData((List<SelectionItem>) levelOptionsTerminal);
			//List<TimeSeriesData> tsList = evalInput.getInputData();
			currentData[tsIdx] = tsList; //new ArrayList<>();
		} //for
		return currentData;
	}

 }
