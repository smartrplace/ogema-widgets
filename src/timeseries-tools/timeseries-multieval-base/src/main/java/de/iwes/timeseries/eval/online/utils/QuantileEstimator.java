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
import java.util.Collections;
import java.util.List;

/**Online quantile estimator.*/
public class QuantileEstimator {
	/**number of input elements that shall be stored before estimation is used. If this is very large
	 * another implementation may be necessary*/
	private final int maxDataToHold;
	/**0.5 calculates median, 0.75 upper quartile*/
	private final double quantileRequired;
	
	//private float quantileNum;
	private int quantileIdx = -1;
	private long lowerValuesCollected;
	//private long upperValuesCollected;
	private long exactValuesCollected;
	
	public long count = 0;
	private class InputData {
		public final float value;
		public long count;
		public InputData(float value, long durationOfInitialValue) {
			this.value = value;
			this.count = durationOfInitialValue;
		}
	}
	protected final List<InputData> collected;
	
	/** Construct Quantile Estimator object that can be fed with values via {@link #addValue(float, long)}
	 * 
	 * @param maxDataToHold maximum number of data points to store before data reduction takes place. The
	 * 		number of elements usually should be at least 10/short_interval, where short interval is the
	 * 		the shorter part you get when you cut the data range normed to 1.0 by the quantile, so the short_interval
	 * 		is 0.1 for a quantileRequired of 0.1 as well as of 0.9. whereas it is 0.5 for a quantileRequired of 0.5.
	 * @param quantileRequired the quantile value to be determined. For quantileRequired = 0.5 the median
	 * 		is calculated.
	 */
	public QuantileEstimator(int maxDataToHold, float quantileRequired) {
		this.maxDataToHold = maxDataToHold;
		this.quantileRequired = quantileRequired;
		collected = new ArrayList<>(maxDataToHold);
	}

	public float getQuantileValue() {
		if(collected.isEmpty()) return Float.NaN;
		quantileIdx = getQuantileIdx();
		//check if integer is close to theoretical value, than just use this, otherwise use average
		//if(((Math.abs(Math.round(quantileNum) - quantileNum)) < 0.1) || quantileIdx == (collected.size()-1))
		InputData myValue = collected.get(quantileIdx);
		return myValue.value;
		//TODO: If the unrounded quantileNum from getQuantileIdx ends on .5 and the sum of all counts up to
		//the previous value before myValue is exactly the unrounded quantileNum-0.5 then we should use the
		//average. This required some more implementation and will rarely occur when using time-based weigting,
		//but would be a problem when unweighted medians shall be calculated
		//else return (myValue.value + collected.get(quantileIdx-1).value)/2;
	}
	
	/**Report new value.
	 * @param value
	 * @param duration If the evaluation shall weight all values equally (duration of the value
	 * not considered, just hand over 1 every time, otherwise the duration of the value
	 */
	public void addValue(float value, long duration) {
		count += duration;
		if(collected.isEmpty()) {
			collected.add(new InputData(value, duration));
			return;
		}
		int idx = getIndexOfClosestValue(value);
		InputData id = collected.get(idx);
		if(id.value == value) {
			id.count += duration;
			return;
		}
		if((collected.size() > maxDataToHold) && (idx == 0 || idx == collected.size()-1)) {
			id.count += duration;
		} else if(value < id.value) {
			collected.add(idx, new InputData(value, duration));
		} else {
			collected.add(idx+1, new InputData(value, duration));					
		}
		if(collected.size() > maxDataToHold) {
			//TODO: For closs-border quantiles / badly distributed data this needs to be adapted
			
			//This is the exact number of values that are expected below the quantile value
			long quantileNum = Math.round(count*quantileRequired);
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
	
	private int getQuantileIdx() {
		long quantileNum = Math.round(count*quantileRequired);
		return getQuantileIdx(quantileNum);
	}
	
	private int getQuantileIdx(long quantileNum) {
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
	
	public List<InputData> getCollectedValues() {
		return collected;
	}
	
	/** Get values collected below or above quantile
	 * 
	 * @param useLower id true values below quantile are returned, otherwise above
	 * @param excludeQuantileIdx if true the data element used to calculate the quantile is included in the list,
	 * 		otherwise not
	 * @return
	 */
	public List<InputData> getCollectedValuesForQuantile(boolean useLower, boolean excludeQuantileIdx) {
		if(collected.isEmpty()) return Collections.emptyList();
		int quantileIdx = getQuantileIdx();
		if(useLower) 
			return collected.subList(0, quantileIdx+1-(excludeQuantileIdx?1:0));
		else
			return collected.subList(quantileIdx+(excludeQuantileIdx?1:0), collected.size());
	}
	
	public float getQuantileMean(boolean useLower, boolean excludeQuantileIdx) {
		if(collected.isEmpty()) return Float.NaN;
		List<InputData> data = getCollectedValuesForQuantile(useLower, excludeQuantileIdx);
		double sum = 0;
		long cnt = 0;
		for(InputData id: data) {
			sum += id.value * id.count;
			cnt += id.count;
		}
		return (float) (sum / cnt);
	}
}
