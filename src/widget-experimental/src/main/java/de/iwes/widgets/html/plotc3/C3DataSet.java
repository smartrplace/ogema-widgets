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
