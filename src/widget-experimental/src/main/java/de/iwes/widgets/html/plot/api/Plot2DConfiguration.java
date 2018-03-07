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
 * This is cloneable using the default implementation, since
 * it only contains primitive fields. In case a derived class
 * contains complex objects, it should override the {@link #clone()} method. 
 */
public class Plot2DConfiguration implements Cloneable {
	
	/**
	 * default values
	 */
	private PlotType type = PlotType.LINE_WITH_POINTS;
	// XXX 
	private boolean typeSetExplicitly = false;
	private boolean interactionsEnabled = true;
	private boolean smoothLines = false;
	private float pointSize = 2.5F;
	private float lineWidth = 2;
	private boolean zoomEnabled = false;
	private boolean clickable = false;
	private boolean hoverable = false;
	private boolean showXGrid = false;
	private boolean showYGrid = true;
	// TODO these are only implemented for flot... (?)
	// Filter for the view only, points which violate the limit are still transferred
	private float ymin = Float.NaN;
	private float ymax = Float.NaN;
	private float xmin = Float.NaN;
	private float xmax = Float.NaN;
	// Points violating these limits are not transferred to the client
	private float yminFilter = Float.NaN;
	private float ymaxFilter = Float.NaN;
	private boolean doScale = false; // if there are multiple plots at different scales, perform a rescaling?
	private String yUnit;
	private String xUnit;

	@Override
	public Plot2DConfiguration clone() {
		try {
			return (Plot2DConfiguration) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Strange exception",e);
		}
	}
	
	// TODO colors & fonts

	/*** Setters ***/
	
	/**
	 * 
	 * @param type
	 * @return
	 * 		this
	 */
	public Plot2DConfiguration setPlotType(PlotType type) {
		return setPlotType(type, false);
	}
	
	// XXX ugly
	/**
	 * Hacky method... don't use it.
	 * @param type
	 * @param ignore
	 * @return
	 */
	public Plot2DConfiguration setPlotType(PlotType type, boolean ignore) {
		this.type = type;
		if (!ignore)
			this.typeSetExplicitly = type != null;
		return this;
	}
	
	/**
	 * Check whether default plot type is used, or the type has been set.
	 * @return
	 */
	public boolean isPlotTypeSetExplicitly() {
		return typeSetExplicitly;
	}
	
	//
	
	/**
	 * Default: true
	 * @param enable
	 * 		if false, the plot will be made static, i.e. not react to 
	 * 		mouse hover, etc. This can improve performance in case of large
	 * 		data sets
	 * @return
	 * 		this
	 */
	public Plot2DConfiguration enableInteractions(boolean enable) {
		this.interactionsEnabled = enable;
		if (!enable) {
			this.clickable = false;
			this.hoverable = false;
			this.zoomEnabled = false;
		}
		return this;
	}
	
	/**
	 * 
	 * @param smooth
	 * @return
	 * 		this
	 */
	public Plot2DConfiguration smoothLine(boolean smooth) {
		this.smoothLines = smooth;
		return this;
	}
	
	/**
	 * 
	 * @param nrPixels
	 * @return
	 * 		this
	 */
	public Plot2DConfiguration setPointSize(float nrPixels) {
		this.pointSize = nrPixels;
		return this;
	}
	
	/**
	 * @param nrPixels
	 * @return
	 * 		this
	 */
	public Plot2DConfiguration setLineWidth(float nrPixels) {
		this.lineWidth = nrPixels;
		return this;
	}
	
	/**
	 * Default: false
	 * @param enable
	 * @return
	 * 		this
	 */
	public Plot2DConfiguration enableZoom(boolean enable) {
		this.zoomEnabled = enable;
		return this;
	}

	/**
	 * @param showXGrid
	 * @return
	 * 		this
	 * @throws UnsupportedOperationException
	 * 		some implementations may not support setting x- and y-grid independently.
	 * 		In this case setShowXGrid shall throw an UnsupportedOperationException
	 */
	public Plot2DConfiguration setShowXGrid(boolean showXGrid) {
		this.showXGrid = showXGrid;
		return this;
	}

	/**
	 * 
	 * @param showYGrid
	 * @return
	 *  	this
	 */
	public Plot2DConfiguration setShowYGrid(boolean showYGrid) {
		this.showYGrid = showYGrid;
		return this;
	}
	
	/**
	 * Default: false
	 * @param hoverable
	 * @return
	 * 		this
	 */
	public Plot2DConfiguration setHoverable(boolean hoverable) {
		this.hoverable = hoverable;
		return this;
	}
	
	/**
	 * Default: false
	 * @param clickable
	 * @return
	 * 		this
	 */
	public Plot2DConfiguration setClickable(boolean clickable) {
		this.clickable = clickable;
		return this;
	}
	
	/**
	 * Restrict the view to a minimum value.
	 * @param ymin
	 * @return
	 * 		this
	 */
	public Plot2DConfiguration setYmin(float ymin) {
		this.ymin = ymin;
		return this;
	}
	
	/**
	 * Restrict the view to a maximum value.
	 * @param ymax
	 * @return
	 * 		this
	 */
	public Plot2DConfiguration setYmax(float ymax) {
		this.ymax = ymax;
		return this;
	}
	
	/**
	 * 
	 * @param xmin
	 * @return
	 *		this 
	 */
	public Plot2DConfiguration setXmin(float xmin) {
		this.xmin = xmin;
		return this;
	}
	
	/**
	 * 
	 * @param xmax
	 * @return
	 * 		this
	 */
	public Plot2DConfiguration setXmax(float xmax) {
		this.xmax = xmax;
		return this;
	}

	/**
	 * Filter out all values smaller then the argument
	 * @param yminFilter
	 */
	public void setYminFilter(float yminFilter) {
		this.yminFilter = yminFilter;
	}

	/**
	 * Filter out all values greater then the argument
	 * @param yminFilter
	 */
	public void setYmaxFilter(float ymaxFilter) {
		this.ymaxFilter = ymaxFilter;
	}

	/**
	 * Adapt scales in case of multiple different plot scales?
	 * Default: false (true for plot as part of schedule viewer widget)
	 * @param scale
	 * @return
	 */
	public Plot2DConfiguration doScale(boolean scale) {
		this.doScale = scale;
		return this;
	}
	
	public Plot2DConfiguration setYUnit(String unit) {
		this.yUnit = unit;
		return this;
	}
	
	public Plot2DConfiguration setXUnit(String unit) {
		this.xUnit = unit;
		return this;
	}
	
	public float getYmin() {
		return ymin;
	}

	public float getYmax() {
		return ymax;
	}

	public float getXmin() {
		return xmin;
	}

	public float getXmax() {
		return xmax;
	}
	
	public float getYminFilter() {
		return yminFilter;
	}
	
	public float getYmaxFilter() {
		return ymaxFilter;
	}
	
	public PlotType getPlotType() {
		return type;
	}
	
	public boolean isInteractionsEnabled() {
		return interactionsEnabled;
	}
	
	public boolean isSmoothLine() {
		return smoothLines;
	}
	
	public float getPointSize() {
		return pointSize;
	}
	
	public float getLineWidth() {
		return lineWidth;
	}
	
	public boolean isZoomEnabled() {
		return zoomEnabled;
	}
	
	public boolean isShowXGrid() {
		return showXGrid;
	}
	
	public boolean isShowYGrid() {
		return showYGrid;
	}
	
	public boolean isHoverable() {
		return hoverable;
	}
	
	public boolean isClickable() {
		return clickable;
	}
	
	public boolean isScale() {
		return doScale;
	}
	
	public String getYUnit() {
		return yUnit;
	}
	
	public String getXUnit() {
		return xUnit;
	}
	
}
