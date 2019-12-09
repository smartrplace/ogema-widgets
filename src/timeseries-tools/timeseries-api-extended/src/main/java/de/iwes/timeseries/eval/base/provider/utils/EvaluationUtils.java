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
package de.iwes.timeseries.eval.base.provider.utils;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesIterator;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesIteratorBuilder;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationInstance.ResultListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.DateConfiguration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.GenericStringConfiguration;
import de.iwes.timeseries.eval.api.configuration.StartEndConfiguration;
import de.iwes.widgets.resource.timeseries.OnlineIterator;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;

public class EvaluationUtils {
	
	/**
	 * Utility method to interrupt a {@link MultiTimeSeriesIterator} that contains 
	 * {@link OnlineIterator}s.
	 * @param iterator
	 * @return
	 */
	public static boolean interruptOnlineMultiIterator(final MultiTimeSeriesIterator iterator) {
		if (iterator == null)
			return false;
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {

			@SuppressWarnings("unchecked")
			@Override
			public Boolean run() {
				final Field field = findField(iterator.getClass(), "iterators");
				if (field == null)
					return false;
				field.setAccessible(true);
				final List<Iterator<SampledValue>> iterators;
				try {
					 iterators = (List<Iterator<SampledValue>>) field.get(iterator);
				} catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NullPointerException e) {
					return false;
				}
				boolean found = false;
				for (Iterator<SampledValue> it: iterators) {
					if (it instanceof OnlineIterator) {
						((OnlineIterator) it).stop();
						found = true;
					}
				}
				return found;
			}
		});
		
	}
	
	/** Get start or end time for maximum interval defined on the input time series selected
	 * 
	 * @param input selected input time series
	 * @param startOrEnd if true the start time shall be returned, otherwise the end time is calculated
	 * @return
	 */
	public static long getDefaultStartEndTimeForInput(Collection<ReadOnlyTimeSeries> input, boolean startOrEnd) {
		long t = startOrEnd ? Long.MAX_VALUE : Long.MIN_VALUE;
		for (ReadOnlyTimeSeries ts  : input) {
			final SampledValue extremal = startOrEnd ? ts.getNextValue(Long.MIN_VALUE) : ts.getPreviousValue(Long.MAX_VALUE);
			if (extremal == null)
				continue;
			final long tlocal = extremal.getTimestamp();
			if ((startOrEnd && tlocal < t) || (!startOrEnd && tlocal > t))
				t = tlocal;
		}
		if ((startOrEnd && t == Long.MAX_VALUE) || (!startOrEnd && t == Long.MIN_VALUE))
			t = System.currentTimeMillis();
		return t;
	}

	private static Field findField(Class<?> clazz, String name) {
		if (clazz == null)
			return null;
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			return findField(clazz.getSuperclass(), name);
		} catch (SecurityException e) {
			return null;
		}
	}
	
	public static MultiTimeSeriesIterator getMultiTimeSeriesIterator(List<EvaluationInput> input,
			List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations,
			EvaluationInstance instance, Long timestep) {
		final List<Iterator<SampledValue>> its = new ArrayList<>();
		final long[] startEnd = getStartAndEndTime(configurations, input, instance.isOnlineEvaluation());
		if (startEnd == null) 
			throw new IllegalArgumentException("Could not determine start/end time from evaluation input");
		long startTime = startEnd[0];
		long endTime = startEnd[1];
		final List<InterpolationMode> modes = new ArrayList<>();
		for (EvaluationInput ei : input) {
			for (TimeSeriesData tsdBase : ei.getInputData()) {
				if(!(tsdBase instanceof TimeSeriesDataOffline)) 
					throw new IllegalStateException("MultiTimeSeriesIterator only works on TimeSeriesData input!");
				TimeSeriesDataOffline tsd = (TimeSeriesDataOffline) tsdBase;
				final ReadOnlyTimeSeries timeSeries = tsd.getTimeSeries();
				final Iterator<SampledValue> it = 
						timeSeries instanceof OnlineTimeSeries ? ((OnlineTimeSeries) timeSeries).onlineIterator(true) : 
						timeSeries.iterator(startTime, endTime);
				its.add(it);
				InterpolationMode mode = tsd.interpolationMode();
				if (mode == null)
					mode = timeSeries.getInterpolationMode();
				modes.add(mode);
			}
		}
		// FIXME?
		if (instance instanceof EvaluationBaseImpl) {
			for (ReadOnlyTimeSeries ts : ((EvaluationBaseImpl) instance).addedInput()) {
				its.add(ts.iterator(startTime, endTime));
				modes.add(ts.getInterpolationMode());
			}
		} else { // no problem, only relevant for EvaluationBaseImpl
//			System.out.println("Warning: Not instance of EvaluationBaseImpl, we might miss relvant step here!!");
		}
		final  MultiTimeSeriesIteratorBuilder builder = MultiTimeSeriesIteratorBuilder.newBuilder(its)
				.setIndividualInterpolationModes(modes);
		if (timestep != null && timestep > 0)
			builder.setStepSize(System.currentTimeMillis(), timestep);
		return builder.build();		
	}

	/**
	 * @param configs
	 * @return
	 * 		null, if no sensible start and end time can be determined, an array {start,end} otherwise
	 */
	public static long[] getStartAndEndTime(final Collection<ConfigurationInstance> configs) {
		Long start = null;
		Long end = null;
		if (configs != null) {
			for (ConfigurationInstance c : configs) {
				if (c instanceof DateConfiguration) {
					if (c.getConfigurationType().equals(StartEndConfiguration.START_CONFIGURATION)) 
						start = ((DateConfiguration) c).getValue();
					else if (c.getConfigurationType().equals(StartEndConfiguration.END_CONFIGURATION))
						end  = ((DateConfiguration) c).getValue();
					if (start != null && end != null) {
						return new long[]{start,end};
					}
				}
			}
		}
		if (start != null && end != null) {
			if (start > end)
				return null;
			return new long[]{start,end};
		}
		return null;
	}
	/**
	 * @param configs
	 * @param input may be null if configs contains start / end time
	 * @param online
	 * @return
	 * 		null, if no sensible start and end time can be determined, an array {start,end} otherwise
	 */
	public static long[] getStartAndEndTime(final Collection<ConfigurationInstance> configs, 
			final List<EvaluationInput> input, final boolean online) {
		Long start = null;
		Long end = null;
		if (configs != null) {
			for (ConfigurationInstance c : configs) {
				if (c instanceof DateConfiguration) {
					if (c.getConfigurationType().equals(StartEndConfiguration.START_CONFIGURATION)) 
						start = ((DateConfiguration) c).getValue();
					else if (c.getConfigurationType().equals(StartEndConfiguration.END_CONFIGURATION))
						end  = ((DateConfiguration) c).getValue();
					if (start != null && end != null) {
						return new long[]{start,end};
					}
				}
			}
		}
		final AtomicLong startNew = start == null ? new AtomicLong(Long.MAX_VALUE) : null;
		final AtomicLong endNew = end == null ? new AtomicLong(Long.MIN_VALUE) : null;
//		long startNew = Long.MAX_VALUE;
//		long endNew = Long.MIN_VALUE;
		for (EvaluationInput ei : input) {
			ReadOnlyTimeSeries timeSeries;
			for (TimeSeriesData tsdBase : ei.getInputData()) {
				if(!(tsdBase instanceof TimeSeriesDataOffline)) throw new IllegalStateException("getStartAndEndTime only works on TimeSeriesData input!");
				TimeSeriesDataOffline tsd = (TimeSeriesDataOffline) tsdBase;
				timeSeries = tsd.getTimeSeries();
				SampledValue sv = timeSeries.getNextValue(Long.MIN_VALUE);
				if (startNew != null && sv != null && sv.getTimestamp() < startNew.get())
					startNew.set(sv.getTimestamp());
				sv = timeSeries.getPreviousValue(Long.MAX_VALUE);
				if (endNew == null || sv == null)
					continue;
				if (sv.getTimestamp() > endNew.get())
					endNew.set(sv.getTimestamp());
			}
		}
		if (start == null)
			start = startNew.get();
		if (online)
			end = Long.MAX_VALUE;
		else if (end == null)
			end = endNew.get();
		if (end != Long.MAX_VALUE)
			end++;
		if (start > end)
			return null;
		return new long[]{start,end};
	}
	
	public static String getStringConfigurationValue(String id, Collection<ConfigurationInstance> configs) {
		if (configs != null) {
			for (ConfigurationInstance c : configs) {
				if (c instanceof GenericStringConfiguration) {
					if (c.getConfigurationType().id().equals(id)) 
						return ((GenericStringConfiguration)c).getValue();
				}
			}
		}
		return null;
	}
	
	public static EvaluationInstance performEvaluationBlocking(EvaluationProvider evalProvider,
			List<EvaluationInput> allItems, List<ResultType> requestedResults, 
			Collection<ConfigurationInstance> configurations) {
		return performEvaluationBlocking(evalProvider, allItems, requestedResults, configurations, null);
	}
	
	public static EvaluationInstance performEvaluationBlocking(EvaluationProvider evalProvider,
			List<EvaluationInput> allItems, List<ResultType> requestedResults, 
			Collection<ConfigurationInstance> configurations, ResultListener intermediateListener) {
		EvaluationInstance instance = evalProvider.newEvaluation(allItems, requestedResults, configurations);
		if(intermediateListener != null) instance.addIntermediateResultListener(intermediateListener);
		
		if(!allItems.isEmpty()) {
			MultiTimeSeriesIterator iterator = 
					EvaluationUtils.getMultiTimeSeriesIterator(allItems, requestedResults, configurations, instance, evalProvider.requestedUpdateInterval());
			while (!instance.isDone() && iterator.hasNext()) {
				try {
					instance.step(iterator.next());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					break; // finished already
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}
		instance.finish();
		return instance;
	}

}
