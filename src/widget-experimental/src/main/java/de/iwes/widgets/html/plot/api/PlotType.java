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

/**
 * think of an enum that can be extended by individual plot widgets, if they offer additional
 * plot types.
 */
public class PlotType {

	public static final PlotType LINE = new PlotType("line",
			System.getProperty("de.iwes.widgets.html.plot.api.line","Line"));
	public static final PlotType LINE_WITH_POINTS = new PlotType("linePoints",
			System.getProperty("de.iwes.widgets.html.plot.api.linePoints","Line with points"));
	public static final PlotType BAR = new PlotType("bar",
			System.getProperty("de.iwes.widgets.html.plot.api.bar","Bar"));
	public static final PlotType POINTS = new PlotType("points",
			System.getProperty("de.iwes.widgets.html.plot.api.points", "Points"));
	public static final PlotType STEPS = new PlotType("steps",
			System.getProperty("de.iwes.widgets.html.plot.api.steps","Steps"));
	public static final PlotType LINE_STACKED = new PlotType("lineStacked",
			System.getProperty("de.iwes.widgets.html.plot.api.lineStacked","Stacked lines"));
	public static final PlotType BAR_STACKED = new PlotType("barStacked",
			System.getProperty("de.iwes.widgets.html.plot.api.barStacked","Stacked bars"));

	private final String id;
	private final String description;

	public PlotType(String id) {
		this(id, id);
	}

	public PlotType(String id, String description) {
		this.id = id;
		this.description = description;
	}

	/**
	 * Returns a unique identifier
	 * @return
	 */
	public final String getId() {
		return id;
	}

	/**
	 * Returns a human readable description
	 */
	@Override
	public String toString() {
		return description;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PlotType))
			return false;
		PlotType tp2 = (PlotType) obj;
		return id.equals(tp2.getId());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
