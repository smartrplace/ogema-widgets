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
package de.iwes.widgets.reswidget.scheduleplot.container;

import java.security.PrivilegedActionException;
import java.util.Objects;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.html.bricks.PageSnippetData;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DConfiguration;
import de.iwes.widgets.reswidget.scheduleplot.api.ScheduleData;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;

class TimeSeriesPlotData extends PageSnippetData {

	// never null
	private Class<? extends TimeSeriesPlot> targetType;
	private volatile TimeSeriesPlot<?, ?, ?> target;

	TimeSeriesPlotData(TimeSeriesPlotGeneric widget) {
		super(widget);
		this.targetType = widget.defaultType;
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		if (!widget.isGlobalWidget())
			widget.triggerAction(target, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST, req);
		return super.retrieveGETData(req);
	}

	protected void setPlotWidget(final Class<? extends TimeSeriesPlot> type, OgemaHttpRequest req) {
		Objects.requireNonNull(type);
		final TimeSeriesPlot<?, ?, ?> bak = this.target;
		if (target != null && type.isAssignableFrom(target.getClass()))
			return;
		try {
			this.target = ((TimeSeriesPlotGeneric) widget).getNew(type, req);
			this.targetType = type;
			removeSubWidgets();
			append(target);
			if (bak != null) {
				if (widget.isGlobalWidget())
					widget.removeTriggerAction(bak, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
				else
					widget.removeTriggerAction(bak, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST, req);
				ScheduleData.copy(bak.getScheduleData(req), this.target.getScheduleData(req));
				Plot2DConfiguration.copyValues(bak.getConfiguration(req), this.target.getConfiguration(req));
			}
		} catch (PrivilegedActionException e) {
			throw new IllegalArgumentException("Could not create widget of type "+ type);
		}
		try {
			if (bak != null)
				bak.destroyWidget();
		} catch (Exception e) {
			LoggerFactory.getLogger(TimeSeriesPlotData.class).warn("Failed to destroy old plot widget",e);
		}
	}

	protected void setPlotWidget(final TimeSeriesPlot<?, ?, ?> plot, OgemaHttpRequest req) {
		Objects.requireNonNull(plot);
		if (plot.equals(target))
			return;
		final TimeSeriesPlot<?, ?, ?> bak;
		synchronized (this) {
			if (plot.equals(target))
				return;
			bak = target;
			if (bak != null) {
				ScheduleData.copy(bak.getScheduleData(req), plot.getScheduleData(req));
				Plot2DConfiguration.copyValues(bak.getConfiguration(req), plot.getConfiguration(req));
				if (widget.isGlobalWidget())
					widget.removeTriggerAction(bak, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
				else
					widget.removeTriggerAction(bak, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST, req);
			}
			target = plot;
			targetType = plot.getClass();
			removeSubWidgets();
			append(target);
		}
		if (bak != null) {
			try {
				bak.destroyWidget();
			} catch (Exception e) {
				e.printStackTrace(); // FIXME
			}
		}
	}

	protected TimeSeriesPlot<?, ?, ?> getPlotWidget(OgemaHttpRequest req) {
		return getTarget(req);
	}

	protected ScheduleData<?> getScheduleData(OgemaHttpRequest req) {
		return getTarget(req).getScheduleData(req);
	}

	TimeSeriesPlot<?, ?, ?> getTarget(OgemaHttpRequest req) {
		TimeSeriesPlot<?, ?, ?> target = this.target;
		if (target == null) {
			synchronized (this) {
				target = this.target;
				if (target == null) {
					try {
						target = ((TimeSeriesPlotGeneric) widget).getNew(targetType, req);
						this.target = target;
						append(target);
					} catch (PrivilegedActionException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return target;
	}

}
