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


public class HumdityCalculator {
	private float absoluteVapor;
	private float outSideVapor;

	public HumdityCalculator(float humidityStart, float temperatureStart) {
		absoluteVapor = HumidityHelper.getAbsoluteHumidity(temperatureStart, humidityStart);
		outSideVapor = HumidityHelper.getAbsoluteHumidity(22f+273.15f, 0.4f);
//		System.out.println("Init HumCalc:"+absoluteVapor+" outSide:"+outSideVapor);
	}
	
	public float getNewValue(long deltaT, float vaporEmission, float temperature) {
		float halfTime = 5*60*1000;
		float roomsize = 40;
		
		//Vapor emission entry
		absoluteVapor += vaporEmission/roomsize*deltaT*0.001f;
		//Diffusion
		absoluteVapor += (outSideVapor-absoluteVapor)*0.69075*deltaT/halfTime;
//		System.out.println("Abs Vapor:"+absoluteVapor+" outSide:"+outSideVapor+" factor:"+(0.69075*deltaT/halfTime)+" vaporEmission"+vaporEmission/roomsize*deltaT*0.001f+" T:"+temperature);
		if(absoluteVapor < 0) absoluteVapor = 0;
		
		float hum = HumidityHelper.getRelativeHumidity(temperature, absoluteVapor);
		//condense at 100%
		if(hum > 1.0f) {
			absoluteVapor = HumidityHelper.getAbsoluteHumidity(temperature, 1.0f);
			hum = 1.0f;
		}
		return hum;
	}
	
	public void addVapor(float vaporPR) {
		absoluteVapor += vaporPR;
	}
	public float getAbsoluteVapor() {
		return absoluteVapor;
	}
}
