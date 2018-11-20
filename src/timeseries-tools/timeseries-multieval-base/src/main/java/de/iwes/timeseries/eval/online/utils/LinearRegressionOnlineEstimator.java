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
package de.iwes.timeseries.eval.online.utils;

/**Online estimator for average, std-deviation, min, max
 * TODO: Use integration taking into account InterpolationMode, now STEPS is assumed*/
public class LinearRegressionOnlineEstimator extends BaseOnlineEstimator {
	private double sumCov = 0;
	private double sumAvY = 0;
	//private double sumSquareY = 0;
	
	/** Construct Base Estimator object that can be fed with values via {@link #addValue(float)}
	 * 
	 * @param calculateMinMax if true minimum and maximum values are searched
	 * @param averageMode if NONE no average value is summed up, otherwise average is always calculated.
	 */
	public LinearRegressionOnlineEstimator() {
		super(false, AverageMode.STD_DEVIATION);
	}

	public float getAverageX() {
		return super.getAverage();
	}
	public float getAverageY() {
		return (float) (sumAvY / count);
	}
	public double getIntegralOverMillisecondsX() {
		return super.getIntegralOverMilliseconds();
	}
	public double getIntegralOverMillisecondsY() {
		return sumAvY;
	}
	public float getStdDeviationX() {
		return super.getStdDeviation();
	}
	//public float getStdDeviationY() {
	//	return (float) Math.sqrt((sumSquareY - (sumAvY*sumAvY / count))/(count - 1));
	//}
	public class RegressionResult {
		public float gradient;
		public float offset;
	}
	public RegressionResult getRegressionParams() {
		RegressionResult result = new RegressionResult();
		double sumAvX = super.getIntegralOverMilliseconds();
		double sumSquareX = super.getSqareIntegralOverMilliseconds();
		result.gradient = (float) ((count*sumCov - sumAvX*sumAvY)/(count*sumSquareX - sumAvX*sumAvX));
		result.offset = getAverageY() - result.gradient * getAverageX();
		return result;
	}
	
	/**Report new value.
	 * @param value
	 * @param duration If the evaluation shall weight all values equally (duration of the value
	 * not considered, just hand over 1 every time, otherwise the duration of the value
	 */
	public void addValue(float valueX, float valueY, long duration) {
		count += duration;
		super.addValue(valueX, duration);
		
		double f = valueY*duration;
		sumAvY += f;
	}
	@Override
	public void addValue(float value, long duration) {
		throw new IllegalStateException("For linear regression always X and Y have to be submitted!");
	}
}
