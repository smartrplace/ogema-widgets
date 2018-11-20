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
package de.iwes.timeseries.eval.api.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.OnlineEvaluation;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.Status.EvaluationStatus;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.StatusImpl;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;

@Deprecated
public abstract class EvaluationSelfSearchingBaseImpl implements OnlineEvaluation, Callable<Status> {
	
	protected List<TimeSeriesData> input = null;
	protected InterpolationMode[] modes = null;
	private volatile Map<ResultType, EvaluationResult> results = null;
	private volatile Status status = StatusImpl.RUNNING;
	private final CountDownLatch evalLatch = new CountDownLatch(1);
//	private final MultiTimeSeriesIterator iterator;
//	private final Future<Status> future;
//	private final EvalTask task;
	protected final List<ResultType> requestedResults;
	private final long time;
	protected int size = 0;
	private final boolean online;
	private final Set<EvaluationListener> listeners = Collections.synchronizedSet(new HashSet<EvaluationListener>());
	
	public EvaluationSelfSearchingBaseImpl(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time) {
		this.time = time;
		this.requestedResults = Collections.unmodifiableList(requestedResults);
		listeners.add(listener);
		final List<TimeSeriesData> list = new ArrayList<>();
		int cnt = 0;
		boolean online = false;
		if(input != null) {
			for (EvaluationInput ei : input)
			list.addAll(ei.getInputData());
			this.size = list.size();
			this.input = Collections.unmodifiableList(list);
			this.modes = new InterpolationMode[size];
			for (TimeSeriesData tsdBase : this.input) {
				// TODO start and end time
				if(!(tsdBase instanceof TimeSeriesDataOffline)) continue;
				TimeSeriesDataOffline tsd = (TimeSeriesDataOffline) tsdBase;
				final ReadOnlyTimeSeries timeSeries = tsd.getTimeSeries();
				if (timeSeries instanceof OnlineTimeSeries)
					online = true;
				final InterpolationMode mode = tsd.interpolationMode() != null ? tsd.interpolationMode() : tsd.getTimeSeries().getInterpolationMode() != null ? 
						tsd.getTimeSeries().getInterpolationMode() : InterpolationMode.NONE;
				modes[cnt++] = mode;
			}
		} else {
			this.input = Collections.emptyList();
		}
		this.online = online;
	}
	
	/**
	 * Override if required
	 * @return
	 */
	public List<ReadOnlyTimeSeries> addedInput() {
		return Collections.emptyList();
	}
	
	@Override
	public long getStartTime() {
		return time;
	}
	
	@Override
	public boolean isOnlineEvaluation() {
		return online;
	}
	
	@Override
	public void addListener(EvaluationListener listener) {
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
			for (EvaluationListener listener: listeners) {
				new Thread(new ListenerRunnable(this, status, listener), 
						"EvaluationListenerThread").start();
			}
			listeners.clear();
		}
	}

	private static class ListenerRunnable implements Runnable {
		
		private final OnlineEvaluation eval;
		private final Status status;
		private final EvaluationListener listener;

		public ListenerRunnable(OnlineEvaluation eval, Status status, EvaluationListener listener) {
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
	public List<ResultType> getResultTypes() {
		return requestedResults;
	}
	
	@Override
	public List<TimeSeriesData> getInputData() {
		return input;
	}
	
	@Override
	public boolean isDone() {
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
    @Deprecated
	public Status finish(long timeout, TimeUnit unit) throws TimeoutException {
		return finish(); 
	}
	
	@Override
	public boolean isCancelled() {
		return status == StatusImpl.CANCELLED;
	}
	
	@Override
	public Map<ResultType, EvaluationResult> getResults() throws IllegalStateException {
		if (!isDone())
			throw new IllegalStateException("Evaluation not done yet");
		if (status.getStatus() == EvaluationStatus.FAILED)
			throw new IllegalStateException("Evaluation failed", status.getCause());
		return results;
	}

	@Override
	public Map<ResultType, EvaluationResult> getIntermediateResults() {
		if (isDone()) {
			if (status.getStatus() == EvaluationStatus.FAILED)
				throw new IllegalStateException("Evaluation failed", status.getCause());
			return results;
		}
		if (!isOnlineEvaluation())
			throw new UnsupportedOperationException();
		return getCurrentResults();
	}
	
	/**
	 * Return an unmodifiable map.
	 * @return
	 */
	protected abstract Map<ResultType, EvaluationResult> getCurrentResults();
	
	public void step(SampledValueDataPoint dataPoint) throws IllegalStateException, Exception {
		throw new IllegalStateException("Self-Searching Evaluations do not allow to call step method");
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
				results = getCurrentResults();
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
