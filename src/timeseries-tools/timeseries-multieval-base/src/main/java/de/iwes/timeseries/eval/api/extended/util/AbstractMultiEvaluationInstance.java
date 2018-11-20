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

import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.Status.EvaluationStatus;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationItemSelector;
import de.iwes.timeseries.eval.api.extended.MultiResult;
import de.iwes.timeseries.eval.api.helper.AlignedIntervalUtil;
import de.iwes.timeseries.eval.api.helper.EvalHelperExtended;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationUtils;
import de.iwes.timeseries.eval.base.provider.utils.StatusImpl;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;

/**Abstract implementation for unforked dependency declarations: all input must have the same dependency tree
 * except for the last (terminal) option or must have a partial tree (except for the terminal option) 
 * of the largest tree without terminal option.<br>
 * For this reason {@link MultiEvaluationInput#itemsSelected()} only needs to be non-null for the
 * first maximum tree input, the method is not called for the other inputs.
 */
public abstract class AbstractMultiEvaluationInstance<T extends MultiResult, S extends SelectionItem> implements MultiEvaluationInstance<T> {
    protected final static AtomicLong idcounter = new AtomicLong(0); // TODO initialize from existing stored eval resources
    protected final String id;
    
	protected final List<MultiEvaluationInputGeneric> input;
	protected AbstractSuperMultiResult<T> superResult;
	private volatile Status status = StatusImpl.RUNNING;
	private final CountDownLatch evalLatch = new CountDownLatch(1);
	//private final long time;
	//protected final int size;
	private final Set<MultiEvaluationListener<T>> listeners = Collections.synchronizedSet(new HashSet<MultiEvaluationListener<T>>());
	protected final TemporalUnit resultStepSize;
	protected final Collection<ConfigurationInstance> configurations;
	
	protected final AbstractMultiEvaluationProvider<T> provider;
	private final MultiEvaluationInputGeneric governingInput;
	private final MultiEvaluationItemSelector governingItemsSelected;
	protected final LinkingOption[] linkingOptions;
	//protected final Collection<ConfigurationInstance> superConfigurations;
	
    public AbstractMultiEvaluationInstance(List<MultiEvaluationInputGeneric> input, 
			Collection<ConfigurationInstance> configurations, TemporalUnit resultStepSize,
			String evalId, AbstractMultiEvaluationProvider<T> provider) {
		this.id = evalId;
		this.input = input;
		this.provider = provider;
		this.resultStepSize = resultStepSize;
		this.configurations = configurations;
        startEnd = EvaluationUtils.getStartAndEndTime(configurations);
        governingInput = input.get(provider.maxTreeIndex);
		governingItemsSelected = input.get(provider.maxTreeIndex).itemSelector();
		//This should be the same for all providers
		linkingOptions = getLinkingOptions(governingInput); //governingInput.dataProvider().get(0).selectionOptions();
	}

	private final long[] startEnd;
	
	public abstract T initNewResult(long start, long end, Collection<ConfigurationInstance> configurations);
	public abstract AbstractSuperMultiResult<T> initSuperResult(List<MultiEvaluationInputGeneric> inputData, long startTime, Collection<ConfigurationInstance> configurations);
	protected abstract LinkingOption[] getLinkingOptions(MultiEvaluationInputGeneric governingInput);
	/** Evaluate time series. The method performs checking whether all required input data is available, so
	 * it is called with all combinations provided by the DataProvider that fit the required input definition
	 * 
	 * @param keys SelectionItems for the hierarchy levels that were used to determine the timeSeries
	 * @param timeSeries index of array: input requested; index of list: timeseries found for the index,
	 * 		e.g. several temperature sensor time series for a gateway-room found
	 * @return StatusImpl.RUNNING to continue
	 * 		* StatusImpl.RESTART_REQUESTED to restart the evaluation loop entirely
	 *      * StatusImpl.SKIP_EVALLEVEL to stop evaluation of the dependency level
	 *      * otherwise evaluation is stopped.
	 */
	public abstract void evaluateDataSet(List<S> keys, List<TimeSeriesData>[] timeSeries, T result);
	/** Notify evaluation on items available for a new dependency level and allow to perform operations
	 *  required for the new level. Note that the status may be set directly via the member variable.
	 * 
	 * @param levelOptionsTerminal
	 * @param level
	 * @return a sublist of levelItems that actually shall be evaluated. If null the level is skipped.
	 *  To evaluate all items just return levelItems.
	 */
	public abstract List<S> startInputLevel(List<S> levelOptionsTerminal,
			List<S> dependecyTreeSelection, int level, T result);
	public abstract void finishInputLevel(int level, T result);
	
	/**Provide resource based on dependencyLevel and information provided by DataProvider of
	 * the maxTree-Inputelement
	 * @param item
	 * @param dependencyLevel
	 * @return
	 */
	/*protected R getResource(SelectionItem item) {
		return input.get(provider.maxTreeIndex).dataProvider().getResource(item);
	}*/
	/**Provide resource for terminal option.
	 * @param item SelectionItem fitting the TerminalOption of the respective DataProvider*/
	/*protected R getResourceForTerminalOption(SelectionItem item, int inputIdx) {
		return input.get(inputIdx).dataProvider().getResource(item);
	}*/
	
	static class InputData {
		public List<SelectionItem> options;
	}
	@Override
	public Status execute() {
		if(stopExecution) return finish();
		if(provider.executeSuperLevelOnly()) {
			this.superResult = initSuperResult(input, startEnd[0], configurations);
			this.superResult.intervalResults = new ArrayList<>();
			superResult.endTime = startEnd[1];
			provider.performSuperEval(superResult);
		} else if(resultStepSize == null) {
			this.superResult = initSuperResult(input, startEnd[0], configurations);
			this.superResult.intervalResults = new ArrayList<>();
			superResult.endTime = startEnd[1];
			executeInterval(startEnd[0], startEnd[1]);
		} else {
			//TODO
			/*AlignedInterval ai;
			ZonedDateTime zdt;
			Instant i = Instant.ofEpochMilli(0);
			ZonedDateTime zdt = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
			zdt.truncatedTo(ChronoUnit.DAYS);
			zdt.with(T);
			zdt.plus(ai.count(), ai.unit());*/
			
			long start = AlignedIntervalUtil.geIntervalStart(startEnd[0], resultStepSize);
			this.superResult = initSuperResult(input, start, configurations);
			while((start < startEnd[1]) && (status == StatusImpl.RUNNING)) {
				long end = AlignedIntervalUtil.getNextIntervalStart(start, resultStepSize);
				executeInterval(start, end);
				start = end;
			}
			superResult.endTime = start;
		}
		return finish();
	}
	
	private void executeInterval(long start, long end) {
		if(stopExecution) return;
		try {
		T result = initNewResult(start, end, EvalHelperExtended.addStartEndTime(start, end, null));
		//result.startTime = start;
		//result.endTime = end;
		//result.configurations = EvalHelperExtended.addStartEndTime(start, end, null);
		status = StatusImpl.RESTART_REQUESTED;
		while(status == StatusImpl.RESTART_REQUESTED) {
			status = StatusImpl.RUNNING;
			executeAllOfLevel(0, new ArrayList<S>(), new ArrayList<Collection<SelectionItem>>(), result);
		}
		superResult.intervalResults.add(result);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	boolean stopExecution = false;
	@Override
	public void stopExecution() {
		stopExecution = true;
	}
	
	/** Execute all gateways, rooms or timeseries
	 * 
	 * @param level
	 * @param upperDependencies2 must contain the same entries as upperDependencies, but
	 * 		not in form of Collections with a single entry each, but just a normal list
	 * @param upperDependencies each inner Collection typically contains exactly one
	 * 		entry
	 * @param result
	 */
	@SuppressWarnings("unchecked")
	private void executeAllOfLevel(int level,
			ArrayList<S> upperDependencies2,
			List<Collection<SelectionItem>> upperDependencies, T result) {
		if(stopExecution) return;
		
		List<SelectionItem> levelOptionsAll = linkingOptions[level].getOptions(upperDependencies);
		List<S> levelOptions = new ArrayList<>();
		for(SelectionItem lvlAll: levelOptionsAll) {
			if(governingItemsSelected.useDataProviderItem(linkingOptions[level], lvlAll))
				levelOptions.add((S) lvlAll);
		}
		//governingItemsSelected.get(linkingOptions[level]);
		//if(levelOptions == null) {
		//	levelOptions = linkingOptions[level].getOptions(upperDependencies);
		//}
		try {
			//This method is overriden e.g. by GaRoMultiEvaluationInstance to perform checking of
			// gateway-Ids, roomTypes etc. Note that gateway-ids are also coded into MultiEvaluationInputGeneric
			// so they should be sorted out before-hand
			levelOptions = startInputLevel(levelOptions, upperDependencies2, level, result);
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		if(status == StatusImpl.SKIP_EVALLEVEL) {
			status = StatusImpl.RUNNING;
			return;
		};
		if(status != StatusImpl.RUNNING || levelOptions == null) return;
		if(level >= (provider.maxTreeSize-1)) {
			//we should execute
			if(status != StatusImpl.RUNNING) return;
			List<TimeSeriesData>[] currentData = MultiEvaluationUtils.getDataForInput(input, upperDependencies);
			if(currentData == null) return;
			/*List<TimeSeriesData>[] currentData = new List[input.size()];
			for(int tsIdx = 0; tsIdx < input.size(); tsIdx++) {
				//first we call start level also here
				MultiEvaluationInputGeneric in = input.get(tsIdx);
				List<SelectionItem> levelOptionsTerminalAll = new ArrayList<>();
				for(DataProvider<?> dp: in.dataProvider()) {
					levelOptionsTerminalAll.addAll(dp.getTerminalOption().getOptions(upperDependencies));
				}
				//List<SelectionItem> levelOptionsTerminalAll = in.dataProvider().getTerminalOption().getOptions(upperDependencies);
				List<S> levelOptionsTerminal = new ArrayList<>();
				for(SelectionItem lvlAll: levelOptionsTerminalAll) {
					for(DataProvider<?> dp: in.dataProvider()) {
						if(in.itemSelector().useDataProviderItem(dp.getTerminalOption(), lvlAll))
						//if(in.itemSelector().useDataProviderItem(in.dataProvider().getTerminalOption(), lvlAll))
							levelOptionsTerminal.add((S) lvlAll);
					}
				}
				//try {
				//	levelOptionsTerminal = startInputLevel(levelOptionsTerminal, upperDependencies2, level+1, result);
				//} catch(Exception e) {
				//	e.printStackTrace();
				//	throw e;
				//}
				if(status != StatusImpl.RUNNING || levelOptionsTerminal == null) return;
				
				//now we actually perform the evaluation
				//EvaluationInputImpl evalInput = null;
				List<TimeSeriesData> tsList = new ArrayList<>();;
				for(DataProvider<?> dp: in.dataProvider()) {
					EvaluationInput evalInputLoc = dp.getData((List<SelectionItem>) levelOptionsTerminal);
					tsList.addAll(evalInputLoc.getInputData());
				}
				//EvaluationInput evalInput = in.dataProvider().getData((List<SelectionItem>) levelOptionsTerminal);
				//List<TimeSeriesData> tsList = evalInput.getInputData();
				currentData[tsIdx] = tsList; //new ArrayList<>();
			} //for*/

			try {
				evaluateDataSet(upperDependencies2, currentData, result);
			} catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
			if(status == StatusImpl.SKIP_EVALLEVEL) {
				//eval level finished anyways
				status = StatusImpl.RUNNING;
			};
			if(status != StatusImpl.RUNNING) return;
		} else {
			for(SelectionItem item: levelOptions) {
				ArrayList<SelectionItem> newDependency = new ArrayList<>();
				newDependency.add(item);
				upperDependencies.add(newDependency);
				upperDependencies2.add((S) item);
				executeAllOfLevel(level+1, upperDependencies2, upperDependencies, result);
				if(status != StatusImpl.RUNNING) return;
				upperDependencies.remove(upperDependencies.size()-1);
				upperDependencies2.remove(upperDependencies2.size()-1);
			}
		}
		try {
			finishInputLevel(level, result);
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public List<MultiEvaluationInputGeneric> getInputData() {
		return input;
	}

	@Override
	public boolean isOnlineEvaluation() {
		return false;
	}

	@Override
	public void addListener(MultiEvaluationListener<T> listener) {
		if (isDone()) {
			new Thread(new ListenerRunnable(this, status, listener), 
					"EvaluationListenerThread").start();
			return;
		} 
		listeners.add(listener);
		if (isDone()) { // to avoid a possible race condition
			if (listeners.remove(listener)) {
				addListener(listener);
			}
		}
	}
	
	private void informListeners() {
		synchronized (listeners) {
			for (MultiEvaluationListener<T> listener: listeners) {
				new Thread(new ListenerRunnable(this, status, listener), 
						"EvaluationListenerThread").start();
			}
			listeners.clear();
		}
	}

	private static class ListenerRunnable implements Runnable {
		
		private final MultiEvaluationInstance<?> eval;
		private final Status status;
		private final MultiEvaluationListener<?> listener;

		public ListenerRunnable(MultiEvaluationInstance<?> eval, Status status, MultiEvaluationListener<?> listener) {
			this.eval = eval;
			this.status = status;
			this.listener = listener;
		}

		@Override
		public void run() {
			listener.evaluationDone(eval,status);
		}
		
	}
	
	@Override
	public boolean isDone() {
		if(stopExecution) return true;
		return evalLatch.getCount() <= 0;
	}

	@Override
	public Status finish() {
		if (isDone())
			return status;
		return finishInternal();
	}
	
	@Override
	public Status get() throws InterruptedException, ExecutionException {
		evalLatch.await();
		return status;
	}
	
	@Override
	public Status get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		evalLatch.await(timeout, unit);
		return status;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (!isDone())
			finishInternal(StatusImpl.CANCELLED);
		return isCancelled(); // there might still be some race condition
	}
	
	@Override
	public boolean isCancelled() {
		return status == StatusImpl.CANCELLED;
	}
	
	@Override
	public AbstractSuperMultiResult<T> getResult() throws IllegalStateException {
		if (!isDone())
			throw new IllegalStateException("Evaluation not done yet");
		if (status.getStatus() == EvaluationStatus.FAILED)
			throw new IllegalStateException("Evaluation failed", status.getCause());
		return superResult;
	}

	public Status finishInternal() {
		return finishInternal(StatusImpl.DONE);
	}
	
	protected synchronized Status finishInternal(final Status status) {
		if (status.getStatus() == EvaluationStatus.RUNNING)
			throw new IllegalArgumentException();
		if (this.status == StatusImpl.RUNNING) {
			this.status = status;
			try {
				//getCurrentResults();
			} catch (Exception e) {
				if (status.getStatus() != EvaluationStatus.FAILED) {
					this.status = new StatusImpl(EvaluationStatus.FAILED, e);
				} // else ignore
			} finally {
				evalLatch.countDown();
				informListeners();
			}
		}
		return status;
	}
}
