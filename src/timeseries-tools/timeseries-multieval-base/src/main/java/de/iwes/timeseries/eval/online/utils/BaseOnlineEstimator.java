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
public class BaseOnlineEstimator extends OnlineEstimator {
	public enum AverageMode {
		NONE,
		AVERAGE_ONLY,
		STD_DEVIATION
	}
	private final boolean calculateMinMax;
	private final AverageMode averageMode;
	
	public long count = 0;
	private double sumAv = 0;
	private double sumSquare = 0;
	private float min = Float.NaN;
	private float max = Float.NaN;
	
	/** Construct Base Estimator object that can be fed with values via {@link #addValue(float, long)}
	 * 
	 * @param calculateMinMax if true minimum and maximum values are searched
	 * @param averageMode if NONE no average value is summed up, otherwise average is always calculated.
	 */
	public BaseOnlineEstimator(boolean calculateMinMax, AverageMode averageMode) {
		this.calculateMinMax = calculateMinMax;
		this.averageMode = averageMode;
	}

	public float getMinimum() {
		if(!calculateMinMax) return Float.NaN;
		return min;
	}
	public float getMaximum() {
		if(!calculateMinMax) return Float.NaN;
		return max;
	}
	public float getAverage() {
		if(averageMode == AverageMode.NONE) return Float.NaN;
		return (float) (sumAv / count);
	}
	public double getIntegralOverMilliseconds() {
		if(averageMode == AverageMode.NONE) return Float.NaN;
		return sumAv;
	}
	public double getSqareIntegralOverMilliseconds() {
		if(averageMode != AverageMode.STD_DEVIATION) return Float.NaN;
		return sumSquare;
	}
	public float getStdDeviation() {
		if(averageMode != AverageMode.STD_DEVIATION) return Float.NaN;
		return (float) Math.sqrt((sumSquare - (sumAv*sumAv / count))/(count - 1));
	}
	
	/**Report new value.
	 * @param value
	 * @param duration If the evaluation shall weight all values equally (duration of the value
	 * not considered, just hand over 1 every time, otherwise the duration of the value
	 */
	public void addValue(float value, long duration) {
		count += duration;
		if(calculateMinMax) {
			if((value < min)||Float.isNaN(min)) min = value;
			if((value > max)||Float.isNaN(max)) max = value;
		}
		if(averageMode != AverageMode.NONE) {
			double f = value*duration;
			sumAv += f;
			if(averageMode == AverageMode.STD_DEVIATION) {
				sumSquare += value*f;
			}
		}
	}
	
}
