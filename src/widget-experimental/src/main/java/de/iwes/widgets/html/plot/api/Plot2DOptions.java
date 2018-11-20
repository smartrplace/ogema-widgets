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
package de.iwes.widgets.html.plot.api;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public abstract class Plot2DOptions<C extends Plot2DConfiguration, D extends Plot2DDataSet> extends WidgetData {
	
	protected final Map<String,D> dataSets = new LinkedHashMap<String, D>();
	protected C configuration;
	
	public Plot2DOptions(Plot2D<C,D,?> plot) {
		super(plot);
//		configuration = createNewConfiguration();
	}
	
	/**
	 * Trigger a client-side change in the plot type; may not be supported by all implementations.
	 * @param type
	 * @return
	 */
	public final static TriggeredAction adaptPlotType(PlotType type) {
		return new TriggeredAction("setPlotType", new Object[]{type.getId()});
	}
	
	/**
	 * Trigger a client-side change in the plot height; may not be supported by all implementations.
	 * @param heightPixels
	 * @return
	 */
	public final static TriggeredAction adaptHeight(int heightPixels) {
		if (heightPixels < 0)
			throw new IllegalArgumentException("Negative height not allowed, got " + heightPixels);
		return new TriggeredAction("setHeight", new Object[]{heightPixels});
	}
	
	/**
	 * Instead of overriding this, implement {@link #getPlotData(Map, OgemaHttpRequest)}
	 */
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		HttpServletRequest rq = req.getReq();
		String x0 = rq.getParameter("xmin");
		String x1 = rq.getParameter("xmax");
		String y0 = rq.getParameter("ymin");
		String y1 = rq.getParameter("ymax");
		float xmin = Float.MIN_VALUE;
		float xmax = Float.MAX_VALUE; 
		float ymin = Float.MIN_VALUE;
		float ymax = Float.MAX_VALUE; 
		boolean xlimited = false;
		boolean ylimited = false;
		if (x0 != null && x1 != null) {
			try {
				xmin = Float.parseFloat(x0);
				xlimited = true;
			} catch (NumberFormatException e) {
				Long dummy = convertToTimestamp(x0);
				if (dummy != null) {
					xmin = dummy.longValue();
					xlimited  =true;
				}
					
			}
			try {
				xmax = Float.parseFloat(x1);
				xlimited = true;
			} catch (NumberFormatException e) {
				Long dummy = convertToTimestamp(x1);
				if (dummy != null) {
					xmax = dummy.longValue();
					xlimited  =true;
				}
			}
		}
		if (y0 != null && y1 != null) {
			try {
				ymin = Float.parseFloat(y0);
				ylimited = true;
			} catch (NumberFormatException e) {
				Long dummy = convertToTimestamp(y0);
				if (dummy != null) {
					ymin = dummy.longValue();
					ylimited  =true;
				}
					
			}
			try {
				ymax = Float.parseFloat(y1);
				ylimited = true;
			} catch (NumberFormatException e) {
				Long dummy = convertToTimestamp(y1);
				if (dummy != null) {
					ymax = dummy.longValue();
					ylimited  =true;
				}
			}
		}
		Map<String,D> data;
		if (xlimited && ylimited) 
			data = getReducedPlots(xmin, xmax, ymin, ymax);
		else if (xlimited)
			data = getReducedPlots(xmin, xmax);
		else
			data = dataSets;
		JSONObject result = getPlotData(data,req);		
		result.put("interactionsEnabled",configuration.isInteractionsEnabled());
		final String yAxis = configuration.getYUnit();
		if (yAxis != null)
			result.put("yAxis", yAxis);
		final String xAxis = configuration.getXUnit();
		if (xAxis != null)
			result.put("xAxis", xAxis);
		return result;
	}
	
	protected static Long convertToTimestamp(String timeString) {
		timeString = timeString.replaceAll("\\s", "").toLowerCase();
		if (!timeString.startsWith("now")) 
			return null;
		long now = System.currentTimeMillis();
		if (timeString.length() == 3)
			return now;
		char operator = timeString.charAt(3);
		boolean op;
		switch (operator) {
		case '+':
			op = true;
			break;
		case '-':
			op = false;
			break;
		default: 
			return null;
		}
		float l = extractLength(timeString.substring(4));
		if (l == Float.NaN)
			return null;
		else if (op)
			return now + (long) l;
		else
			return now - (long) l;
		
	}
	
	/**
	 *  expects string in format "2m", or "3.1d", or the like ("4" without unit is interpreted as millisenconds)
	 *  @return
	 *  	duration in ms, or NaN if format is invalid
	 */
	private static float extractLength(String timeString) {
		if (timeString == null || timeString.isEmpty())
			return Float.NaN;
		StringBuilder sb= new StringBuilder();
		boolean firstPointReached = false;
		int idx = 0;
		char ch = timeString.charAt(0);
		while (Character.isDigit(ch) || (!firstPointReached && (firstPointReached = (ch == '.')))) {
			sb.append(ch);
			idx++;
			ch = timeString.charAt(idx);
		}
		BigDecimal bd;
		try {
			bd = new BigDecimal(sb.toString());
		} catch (NumberFormatException e) {
			return Float.NaN;
		}
		String unit = timeString.substring(idx);
		long factor;
		switch (unit) {
		case "ms":  // millis
		case "": 
			factor = 1;
			break;
		case "s":
			factor = 1000;
			break;
		case "m": // minutes
		case "min":
		case "minutes":
			factor = 60 * 1000;
			break;
		case "h":
		case "hour":
		case "hours":
			factor = 60 * 60 * 1000;
			break;
		case "d":
		case "day":
		case "days":
			factor = 24 * 60 * 60 * 1000;
			break;
		case "w":
		case "week":
		case "weeks":
			factor = 7 * 24 * 60 * 60 * 1000;
			break;
		case "mon":
		case "month":
		case "months":
			factor = 30 * 7 * 24 * 60 * 60 * 1000;
			break;
		case "a":
		case "year":
		case "years":
		case "y":
			factor = 12 * 30 * 7 * 24 * 60 * 60 * 1000;
			break;
		default: 
			return Float.NaN;
		}
		return bd.floatValue() * factor;
	}
	
	public abstract JSONObject getPlotData(Map<String,D> data, OgemaHttpRequest req);
	
	public final void addRow(D row) {
		dataSets.put(row.getId(), row);
	}
	
	public final void setRows(Collection<D> data) {
		dataSets.clear();
		for (D dt: data) {
			addRow(dt);
		}
	}
	
	public final Map<String,D> getRows() {
		return new LinkedHashMap<String, D>(dataSets);
	}
	
	public C getConfiguration() {
		return configuration;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,D> getReducedPlots(float xmin, float xmax, float ymin, float ymax) {
		Map<String,D> reducedMap = new LinkedHashMap<String, D>();
		for (Map.Entry<String, D> entry: dataSets.entrySet()) {
			reducedMap.put(entry.getKey(), (D) entry.getValue().getValues(xmin, xmax, ymin, ymax));
		}
		return reducedMap;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,D> getReducedPlots(float xmin, float xmax) {
		Map<String,D> reducedMap = new LinkedHashMap<String, D>();
		for (Map.Entry<String, D> entry: dataSets.entrySet()) {
			reducedMap.put(entry.getKey(), (D) entry.getValue().getValues(xmin, xmax));
		}
		return reducedMap;
	}

}
