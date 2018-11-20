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
package de.iwes.sim.tools;

public class SimUtils {

	// XXX very generic... move to application code or remove
	public static float roundToDec(float inval, int decimals) {
		switch(decimals) {
		case 1:
			return Math.round(inval*10)/10;
		case 2:
			return Math.round(inval*100)/100;
		case -1:
			return Math.round(inval/10)*10;
		default:
			return Math.round(inval);
		}
	}

    public static float infiniteProbabilityAggregation(float probabilitySingleStep) {
		return 1.0f+1.0f/((1.0f/probabilitySingleStep)-1);
	}
    
    public static boolean performRandomTest(float probabilityPerMilli, long timeStep) {
    	double rval = Math.random();
    	if(rval < (probabilityPerMilli*timeStep)) {
    		return true;
    	} else return false;
    }
    public static boolean performRandomTest(float probability) {
    	double rval = Math.random();
    	if(rval < probability) {
    		return true;
    	} else return false;
    }
}
