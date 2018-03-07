/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package de.iwes.widgets.html.plot.api;

/**
 * think of an enum that can be extended by individual plot widgets, if they offer additional
 * plot types.
 */
public class PlotType {

	public static final PlotType LINE = new PlotType("line","Line");
	public static final PlotType LINE_WITH_POINTS = new PlotType("linePoints", "Line with points");
	public static final PlotType BAR = new PlotType("bar", "Bar");
	public static final PlotType POINTS = new PlotType("points", "Points");
	public static final PlotType STEPS = new PlotType("steps", "Steps");
	
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
