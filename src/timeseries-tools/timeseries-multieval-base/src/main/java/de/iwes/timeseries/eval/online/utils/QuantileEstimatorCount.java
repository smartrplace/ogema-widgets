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

import java.util.ArrayList;
import java.util.List;

/**Online quantile estimator. Note: Up to now considers each value, not the time span used.*/
public class QuantileEstimatorCount {
	/**number of input elements that shall be stored before estimation is used. If this is very large
	 * another implementation may be necessary*/
	private final int maxDataToHold;
	/**0.5 calculates median, 0.75 upper quartile*/
	private final float quantileRequired;
	
	//private float quantileNum;
	private int quantileIdx = -1;
	private int lowerValuesCollected;
	//private int upperValuesCollected;
	private int exactValuesCollected;
	
	public int count = 0;
	private class InputData {
		public final float value;
		public int count = 1;
		public InputData(float value) {
			this.value = value;
		}
	}
	protected final List<InputData> collected;
	
	/** Construct Quantile Estimator object that can be fed with values via {@link #addValue(float)}
	 * 
	 * @param maxDataToHold see {@link QuantileEstimator}
	 * @param quantileRequired the quantile value to be determined. For quantileRequired = 0.5 the median
	 * 		is calculated.
	 */
	public QuantileEstimatorCount(int maxDataToHold, float quantileRequired) {
		this.maxDataToHold = maxDataToHold;
		this.quantileRequired = quantileRequired;
		collected = new ArrayList<>(maxDataToHold);
	}

	public float getQuantileValue() {
		if(collected.isEmpty()) return Float.NaN;
		float quantileNum = count*quantileRequired;
		quantileIdx = getQuantileIdx(quantileNum);
		if((quantileNum == (float)quantileIdx) || quantileIdx == (collected.size()-1))
			return collected.get(quantileIdx).value;
		else return (collected.get(quantileIdx).value + collected.get(quantileIdx+1).value)/2;
	}
	
	public void addValue(float value) {
		count++;
		if(collected.isEmpty()) {
			collected.add(new InputData(value));
			return;
		}
		int idx = getIndexOfClosestValue(value);
		InputData id = collected.get(idx);
		if(id.value == value) {
			id.count++;
			return;
		}
		if((collected.size() > maxDataToHold) && (idx == 0 || idx == collected.size()-1)) {
			id.count++;
		} else if(value < id.value) {
			collected.add(idx, new InputData(value));
		} else {
			collected.add(idx+1, new InputData(value));					
		}
		if(collected.size() > maxDataToHold) {
			//TODO: For closs-border quantiles / badly distributed data this needs to be adapted
			
			//This is the exact number of values that are expected below the quantile value
			float quantileNum = count*quantileRequired;
			if(quantileIdx < 0) {
				//initialize
				quantileIdx = getQuantileIdx(quantileNum);
				if(quantileIdx < 0) throw new IllegalStateException("Quantile calculation failed!");
			} else {
				if(idx < quantileIdx) {
					lowerValuesCollected++;
				} else if(idx > quantileIdx) {
					//upperValuesCollected++;
				} else {
					exactValuesCollected++;
				}
				if(lowerValuesCollected+exactValuesCollected < quantileNum) {
					quantileIdx++;
					lowerValuesCollected += exactValuesCollected;
					exactValuesCollected = collected.get(quantileIdx).count;
					//upperValuesCollected -= exactValuesCollected;
				} else if(lowerValuesCollected > quantileNum) {
					quantileIdx--;
					//upperValuesCollected += exactValuesCollected;
					exactValuesCollected = collected.get(quantileIdx).count;
					lowerValuesCollected -= exactValuesCollected;
				}
			}
			
			//clean up
			if(quantileIdx > collected.size() / 2) {
				collected.get(1).count += collected.get(0).count;
				collected.remove(0);
			} else {
				collected.get(collected.size()-2).count += collected.get(collected.size()-1).count;
				collected.remove(collected.size()-1);				
			}
		}
	}
	
	private int getQuantileIdx(float quantileNum) {
		int cntInQ = 0;
		lowerValuesCollected = 0;
		for(int i=0; i<collected.size(); i++) {
			cntInQ += collected.get(i).count;
			if(cntInQ >= quantileNum) {
				quantileIdx = i;
				//upperValuesCollected = count - cntInQ;
				exactValuesCollected =  collected.get(i).count;
				return i;
			}
			lowerValuesCollected += collected.get(i).count;
		}
		return collected.size()-1;
	}
	
	private int getIndexOfClosestValue(float value) {

	        if(value < collected.get(0).value) {
	            return 0;
	        }
	        if(value > collected.get(collected.size()-1).value) {
	            return collected.size()-1;
	        }

	        int lo = 0;
	        int hi = collected.size() - 1;

	        while (lo <= hi) {
	            int mid = (hi + lo) / 2;

	            if (value < collected.get(mid).value) {
	                hi = mid - 1;
	            } else if (value > collected.get(mid).value) {
	                lo = mid + 1;
	            } else {
	                return mid;
	            }
	        }
	        // lo == hi + 1
	        return (collected.get(lo).value - value) < (value - collected.get(hi).value) ? lo : hi;
	    }
	
	public int size() {
		return collected.size();
	}
}
