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

package de.iwes.widgets.html.plotc3;

import org.json.JSONArray;

import de.iwes.widgets.html.plot.api.Plot2DDataSet;

public class C3DataSet implements Plot2DDataSet {

	private final String xLabel;
	private final String yLabel;  // = id
	protected JSONArray xArr;  // simply array of values // first value = id
	protected JSONArray yArr;

	/****** Constructors ********/
	
	/**
	 * @param xs
	 * 	  First entry: label (String); then: data points 
	 * @param ys
	 * 	  First entry: label (String); then: data points 
	 * @param type
	 */
	public C3DataSet(JSONArray xs, JSONArray ys) {
		this.xLabel = xs.getString(0);  
		this.yLabel = ys.getString(0);	
		this.xArr = xs;
		this.yArr = ys;
	}

	public String getXLabel() {
		return xLabel;
	}
	
	public String getYLabel() {
		return yLabel;
	}
	
	protected JSONArray[] getData() {
		JSONArray[] arr = {xArr,yArr};
		return arr;
	}
	
	public void reset() {
		xArr = new JSONArray();
		yArr = new JSONArray();
		xArr.put(xLabel);
		yArr.put(yLabel);
	}

	@Override
	public String getId() {
		return yLabel;
	}

	@Override
	public Plot2DDataSet getValues(float xmin, float xmax) throws UnsupportedOperationException { // TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public Plot2DDataSet getValues(float xmin, float xmax, int maxNrPoints)	throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Plot2DDataSet getValues(float xmin, float xmax, float ymin,float ymax) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
}
