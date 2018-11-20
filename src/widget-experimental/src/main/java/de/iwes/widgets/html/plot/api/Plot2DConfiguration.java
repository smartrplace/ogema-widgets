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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Objects;

/**
 * This is cloneable using the default implementation, since
 * it only contains primitive fields. In case a derived class
 * contains complex objects, it should override the {@link #clone()} method.
 */
public class Plot2DConfiguration implements Cloneable {

	public enum AxisType {

		DEFAULT,
		TIME;

	}

	private AxisType axisType = AxisType.DEFAULT;
	/**
	 * default values
	 */
	private PlotType type = PlotType.LINE;
	private boolean asStackedVersion = false;
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
	 * @param ymaxFilter
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

	public AxisType getXAxisType0() {
		return axisType;
	}

	public Plot2DConfiguration setXAxisType0(AxisType type) {
		this.axisType = Objects.requireNonNull(type);
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

	/**
	 * @return
	 * @deprecated use {@link #getPlotType()} instead
	 */
	@Deprecated
	public boolean asStackedVersion() {
		return asStackedVersion;
	}

	/**
	 * @param asStackedVersion
	 * @deprecated use {@link #setPlotType(PlotType) setPlotType(PlotType.LINE_STACKED)} instead
	 */
	@Deprecated
	public void setAsStackedVersion(boolean asStackedVersion) {
		this.asStackedVersion = asStackedVersion;
	}


	public static void copyValues(final Plot2DConfiguration source, final Plot2DConfiguration target) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				Arrays.asList(Plot2DConfiguration.class.getDeclaredFields())
					.forEach(field -> {
						field.setAccessible(true);
						try {
							field.set(target, field.get(source));
						} catch (IllegalArgumentException | IllegalAccessException e) {
							throw new RuntimeException("Unexpected exception", e);
						}
					});

				return null;
			}
		});
	}


}
