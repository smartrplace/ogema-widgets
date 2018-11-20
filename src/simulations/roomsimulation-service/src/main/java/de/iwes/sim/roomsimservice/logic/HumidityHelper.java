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
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.sim.roomsimservice.logic;

public class HumidityHelper {
	public static float getAbsoluteHumidity(float temperature, float humidity) {
		double mw = 18.016;
		double R = 8314.3;
        float ap = 7.5f;
        float bp = 237.3f;
        float T = temperature - 273.15f;
        double DD = humidity * 6.1078 * Math.pow(10, (ap*T)/(bp+T));
        return (float) (1e5 * mw/R * DD/temperature);
	}
	
	public static float getDewPointTemperature(float temperature, float humidity) {
        //according to http://www.wetterochs.de/wetter/feuchte.html
        float ap = 7.5f;
        float bp = 237.3f;
        float T = temperature - 273.15f;
        double DD = humidity * 6.1078 * Math.pow(10, (ap*T)/(bp+T));
        double v = Math.log10(DD/6.1078);
        return (float) (bp*v/(ap-v)) + 273.15f;
	}
	
	public static float getRelativeHumidity(float temperature, float absoluteHumidity) {
		double mw = 18.016;
		double R = 8314.3;
        float T = temperature - 273.15f;
        
        double DD = absoluteHumidity * temperature / (1e5* mw/R);
        //double DD = absoluteHumidity / (1e5 * mw/R) + temperature;
        //double humidity = DD / (6.1078 * Math.pow(10, (ap*T)/(bp+T)));
         double humidity = DD / SDD(T);
        return (float)humidity;		
	}
	
	private static double SDD(double T) {
        float ap = 7.5f;
        float bp = 237.3f;
		return 6.1078 * Math.pow(10, ((ap*T)/(bp+T)));
	}
}
