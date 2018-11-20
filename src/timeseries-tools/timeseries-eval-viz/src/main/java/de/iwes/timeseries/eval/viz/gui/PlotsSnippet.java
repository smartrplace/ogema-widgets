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
package de.iwes.timeseries.eval.viz.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.ArrayResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.TimeSeriesResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.viz.gui.ResultsPage.ResultInit;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.PlotType;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultTimeSeriesDisplayTemplate;
import de.iwes.widgets.reswidget.scheduleviewer.ScheduleViewerBasic;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationBuilder;
import de.iwes.widgets.template.DisplayTemplate;

class PlotsSnippet extends PageSnippet {

	private static final long serialVersionUID = 1L;
	private final ScheduleViewerBasic<SchedulePresentationData> scheduleViewer;
	private final ResultInit evaluationInit;

	public PlotsSnippet(WidgetPage<?> page, String id, ApplicationManager am, ResultInit evaluationInit) {
		super(page, id, true);
		this.evaluationInit = evaluationInit;
		final ScheduleViewerConfiguration config = ScheduleViewerConfigurationBuilder.newBuilder()   // TODO
				.setShowIndividualConfigBtn(true)
				.build();
		final DisplayTemplate<SchedulePresentationData> template = new DefaultTimeSeriesDisplayTemplate<>(getNameService());
		this.scheduleViewer = new ScheduleViewerBasic<SchedulePresentationData>(page, id + "_viewer", am, config, template);
		scheduleViewer.getSchedulePlot().getDefaultConfiguration().doScale(false);
		
		this.append(scheduleViewer, null);
		this.triggerAction(scheduleViewer, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST); // ok?
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		final List<SchedulePresentationData> presentationData = new ArrayList<>();
		final Map<ReadOnlyTimeSeries,TimeSeriesData> input = evaluationInit.getInputData(req);
		if (input == null) {
			final NullPointerException npe = new NullPointerException("The requested evaluation data could not be found");
			npe.setStackTrace(new StackTraceElement[0]); // error message will be printed to the error page -> avoid spam
			throw npe;
		}
		for (Map.Entry<ReadOnlyTimeSeries, TimeSeriesData> entry : input.entrySet()) {
			DefaultSchedulePresentationData presentation = 
					new DefaultSchedulePresentationData(entry.getKey(), Float.class, "Input: " + entry.getValue().label(req.getLocale()), entry.getValue().interpolationMode()); // FIXME type
			presentationData.add(presentation);
		}
		InterpolationMode modeResult = null;
		for (SingleEvaluationResult ser : evaluationInit.getSingleResults(req)) {
			if (ser instanceof TimeSeriesResult) {
				DefaultSchedulePresentationData presentation = new DefaultSchedulePresentationData(((TimeSeriesResult) ser).getValue(), 
						Float.class, "Result: " + ser.getResultType().label(req.getLocale())); // FIXME type?
				presentationData.add(presentation);
				if (modeResult == null)
					modeResult = ((TimeSeriesResult) ser).getValue().getInterpolationMode();
			} else if (ser instanceof ArrayResult) {
				final List<SingleEvaluationResult> singleResults = ((ArrayResult) ser).getValues();
				if (singleResults == null || singleResults.isEmpty() || !(singleResults.get(0) instanceof TimeSeriesResult))
					continue;
				for (SingleEvaluationResult singleResult : singleResults) {
					if (!(singleResult instanceof TimeSeriesResult))
						continue;
					((TimeSeriesResult) singleResult).getValue().toString();
					DefaultSchedulePresentationData presentation = new DefaultSchedulePresentationData(((TimeSeriesResult) singleResult).getValue(), 
							Float.class, "Result: " + singleResult.getResultType().label(req.getLocale()) + ": "
							+ ((TimeSeriesResult) singleResult).getValue().toString());
					presentationData.add(presentation);
					if (modeResult == null)
						modeResult = ((TimeSeriesResult) singleResult).getValue().getInterpolationMode();
				}
			}
		}
		scheduleViewer.setSchedules(presentationData, req);
		if (modeResult != null) 
			scheduleViewer.getDefaultPlotConfiguration().setPlotType(getPlotType(modeResult));
	}
	
	private static PlotType getPlotType(final InterpolationMode mode) {
		return mode == InterpolationMode.STEPS || mode == InterpolationMode.NEAREST ? PlotType.STEPS :
			mode == InterpolationMode.LINEAR ? PlotType.LINE :
			null;
				
	}

}
