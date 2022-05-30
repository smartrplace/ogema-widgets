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
package de.iwes.util.resource;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.model.array.BooleanArrayResource;
import org.ogema.core.model.array.FloatArrayResource;
import org.ogema.core.model.array.IntegerArrayResource;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.recordeddata.RecordedData;

public class ValueResourceHelper {
	/** create resource if it does not yet exist. If the resource is newly created write
	 * value into it, otherwise do nothing. Note that the activation status is not changed,
	 * so the resource usually has to be activated later on if it was created.
	 * @return true if resource was created and value was written
	 */
	public static boolean setIfNew(FloatResource fres, float value) {
		if(!fres.exists()) {
			fres.create();
			fres.setValue(value);
			return true;
		}
		return false;
	}
	/** create resource if it does not yet exist. If the resource is newly created write
	 * value into it, otherwise do nothing. Note that the activation status is not changed,
	 * so the resource usually has to be activated later on if it was created.
	 * @return true if resource was created and value was written
	 */
	public static boolean setIfNewOrZero(FloatResource fres, float value) {
		if(!fres.exists() || (fres.getValue()==0.0f)) {
			fres.create();
			fres.setValue(value);
			return true;
		}
		return false;
	}
	/** create resource if it does not yet exist. If the resource is newly created write
	 * value into it, otherwise do nothing. Note that the activation status is not changed,
	 * so the resource usually has to be activated later on if it was created.
	 * @param value temperature to set for new resource in celsius
	 * @return true if resource was created and value was written
	 */
	public static boolean setIfNewCelsius(TemperatureResource fres, float value) {
		if(!fres.exists()) {
			fres.create();
			fres.setCelsius(value);
			return true;
		}
		return false;
	}
	/** create resource if it does not yet exist. If the resource is newly created write
	 * value into it, otherwise do nothing. Note that the activation status is not changed,
	 * so the resource usually has to be activated later on if it was created.
	 * @return true if resource was created and value was written
	 */
	public static boolean setIfNew(TimeResource fres, long value) {
		if(!fres.exists()) {
			fres.create();
			fres.setValue(value);
			return true;
		}
		return false;
	}
	/** create resource if it does not yet exist. If the resource is newly created write
	 * value into it, otherwise do nothing. Note that the activation status is not changed,
	 * so the resource usually has to be activated later on if it was created.
	 * @return true if resource was created and value was written
	 */
	public static boolean setIfNew(IntegerResource fres, int value) {
		if(!fres.exists()) {
			fres.create();
			fres.setValue(value);
			return true;
		}
		return false;
	}
	/** create resource if it does not yet exist. If the resource is newly created write
	 * value into it, otherwise do nothing. Note that the activation status is not changed,
	 * so the resource usually has to be activated later on if it was created.
	 * @return true if resource was created and value was written
	 */
	public static boolean setIfNew(BooleanResource fres, boolean value) {
		if(!fres.exists()) {
			fres.create();
			fres.setValue(value);
			return true;
		}
		return false;
	}
	/** create resource if it does not yet exist. If the resource is newly created write
	 * value into it, otherwise do nothing. Note that the activation status is not changed,
	 * so the resource usually has to be activated later on if it was created.
	 * @return true if resource was created and value was written
	 */
	public static boolean setIfNew(StringResource fres, String value) {
		if(!fres.exists()) {
			fres.create();
			fres.setValue(value);
			return true;
		}
		return false;
	}
	public static boolean setIfNew(FloatArrayResource fres, float[] values) {
		if(!fres.exists()) {
			fres.create();
			fres.setValues(values);
			return true;
		}
		return false;
	}
	/** If resource does not exist yet, create it as reference, otherwise do nothing. So if
	 * the resource exists as reference or as direct sub resource it remains
	 * unchanged.
	 * @param fres resource to create as reference if not yet existing
	 * @param source resource to set reference to
	 * @return true if resource was created and value was written
	 */
	public static <T extends Resource> boolean referenceIfNew(T fres, T source) {
		if(!fres.exists()) {
			fres.setAsReference(source);
			return true;
		}
		return false;
	}
	/** write into resoure and create it. If the resource does not exist prior to callling
	 * the method it it first created, then written, then activated.
	 * @return true if resource was created and value was written
	 */
	public static boolean setCreate(FloatResource fres, float value) {
		if(!fres.exists()) {
			fres.create();
			fres.setValue(value);
			fres.activate(false);
			return true;
		}
		fres.setValue(value);
		return false;
	}
	/** write into resoure and create it. If the resource does not exist prior to callling
	 * the method it it first created, then written, then activated.
	 * @return true if resource was created and value was written
	 */
	public static boolean setCreate(IntegerResource fres, int value) {
		if(!fres.exists()) {
			fres.create();
			fres.setValue(value);
			fres.activate(false);
			return true;
		}
		fres.setValue(value);
		return false;
	}
	public static boolean setCreateIfChanged(IntegerResource fres, int value) {
		if(!fres.exists())
			return setCreate(fres, value);
		if(fres.getValue() == value)
			return false;
		return setCreate(fres, value);
	}
	/** Write only if the value has changed
	 * 
	 * @param fres
	 * @param value
	 * @return true if value was writtten, false if no change was detected
	 */
	public static boolean setIfChange(IntegerResource fres, int value) {
		if(fres.getValue() == value)
			return false;
		fres.setValue(value);
		return true;
	}
	
	/** write into resoure and create it. If the resource does not exist prior to callling
	 * the method it it first created, then written, then activated.
	 * @return true if resource was created and value was written
	 */
	public static boolean setCreate(TimeResource fres, long value) {
		if(!fres.exists()) {
			fres.create();
			fres.setValue(value);
			fres.activate(false);
			return true;
		}
		fres.setValue(value);
		return false;
	}
	/** write into resoure and create it. If the resource does not exist prior to callling
	 * the method it it first created, then written, then activated.
	 * @return true if resource was created and value was written
	 */
	public static boolean setCreate(BooleanResource fres, boolean value) {
		if(!fres.exists()) {
			fres.create();
			fres.setValue(value);
			fres.activate(false);
			return true;
		}
		fres.setValue(value);
		return false;
	}
	/** write into resoure and create it. If the resource does not exist prior to callling
	 * the method it it first created, then written, then activated.
	 * @return true if resource was created and value was written
	 */
	public static boolean setCreate(StringResource fres, String value) {
		if(!fres.exists()) {
			fres.create();
			fres.setValue(value);
			fres.activate(false);
			return true;
		}
		fres.setValue(value);
		return false;
	}
	
	/** write into resoure and create it. If the resource does not exist prior to callling
	 * the method it it first created, then written, then activated.
	 * @return true if resource was created and value was written
	 */
	public static boolean setCreate(StringArrayResource fres, String[] values) {
		if(!fres.exists()) {
			fres.create();
			fres.setValues(values);
			fres.activate(false);
			return true;
		}
		fres.setValues(values);
		return false;
	}
	public static boolean setCreate(IntegerArrayResource fres, int[] values) {
		if(!fres.exists()) {
			fres.create();
			fres.setValues(values);
			fres.activate(false);
			return true;
		}
		fres.setValues(values);
		return false;
	}
	public static boolean setCreate(FloatArrayResource fres, float[] values) {
		if(!fres.exists()) {
			fres.create();
			fres.setValues(values);
			fres.activate(false);
			return true;
		}
		fres.setValues(values);
		return false;
	}
	public static boolean setCreate(BooleanArrayResource fres, boolean[] values) {
		if(!fres.exists()) {
			fres.create();
			fres.setValues(values);
			fres.activate(false);
			return true;
		}
		fres.setValues(values);
		return false;
	}
	
	public static boolean setIfChange(IntegerArrayResource fres, int[] values) {
		int[] curVals = fres.getValues();
		if(intArrayEquals(curVals, values)) //curVals.equals(values))
			return false;
		fres.setValues(values);
		return true;
	}
	public static int[] list2arrInt(List<Integer> floatList, int defaultVal) {
		int[] arr = new int[floatList.size()];
		int i= 0;
		for (Integer f : floatList) {
		    arr[i++] = (f != null ? f : defaultVal);
		}
		return arr;
	}
	public static List<Integer> arr2listInt(int[] arr) {
		List<Integer> result = new ArrayList<>();
		for (int f : arr) {
		    result.add(f);
		}
		return result;
	}
	public static boolean setIfChange(IntegerArrayResource fres, List<Integer> floatList, int defaultVal) {
		int[] arr = list2arrInt(floatList, defaultVal);
		return setIfChange(fres, arr);
	}
	public static boolean setIfChange(FloatArrayResource fres, float[] values) {
		float[] curVals = fres.getValues();
		if(floatArrayEquals(curVals, values)) //if(curVals.equals(values))
			return false;
		fres.setValues(values);
		return true;
	}
	public static boolean floatArrayEquals(float[] arr1, float[] arr2) {
		if(arr1.length != arr2.length)
			return false;
		for(int idx=0; idx<arr1.length; idx++ ) {
			if(Math.abs(arr1[idx]-arr2[idx]) > 0.000001f)
				return false;
		}
		return true;
	}
	public static boolean intArrayEquals(int[] arr1, int[] arr2) {
		if(arr1.length != arr2.length)
			return false;
		for(int idx=0; idx<arr1.length; idx++ ) {
			if(arr1[idx] != arr2[idx])
				return false;
		}
		return true;
	}
	public static float[] list2arrFloat(List<Float> floatList) {
		float[] arr = new float[floatList.size()];
		int i= 0;
		for (Float f : floatList) {
		    arr[i++] = (f != null ? f : Float.NaN);
		}
		return arr;
	}
	public static List<Float> arr2listFloat(float[] arr) {
		List<Float> result = new ArrayList<>();
		for (float f : arr) {
		    result.add(f);
		}
		return result;
	}
	public static boolean setIfChange(FloatArrayResource fres, List<Float> floatList) {
		float[] arr = list2arrFloat(floatList);
		return setIfChange(fres, arr);
	}

	public static boolean isAlmostEqual(float temp1, float temp2) {
		if (Math.abs(temp1-temp2) < 0.001f) return true;
		else return false;
	}

	/** Get Recorded data from SingleValueResource
	 * 
	 * @param valueResource
	 * @return null if type of resource does not support getHistoricalData
	 */
	public static RecordedData getRecordedData(SingleValueResource valueResource) {
		if(valueResource instanceof FloatResource)
			return ((FloatResource)valueResource).getHistoricalData();
		if(valueResource instanceof IntegerResource)
			return ((IntegerResource)valueResource).getHistoricalData();
		if(valueResource instanceof TimeResource)
			return ((TimeResource)valueResource).getHistoricalData();
		if(valueResource instanceof BooleanResource)
			return ((BooleanResource)valueResource).getHistoricalData();
		return null;
	}
	
	public static Float getFloatProperty(String propertyName) {
		return getFloatPropertyInternal(propertyName, null);
	}
	public static float getFloatProperty(String propertyName, float defaultVal) {
		return getFloatPropertyInternal(propertyName, defaultVal);
	}
	private static Float getFloatPropertyInternal(String propertyName, Float defaultVal) {
		String prop = System.getProperty(propertyName);
		if(prop == null)
			return defaultVal;
		try {
			return Float.parseFloat(prop);
		} catch(NumberFormatException e) {
			return defaultVal;
		}
	}
	public static double getFloatAsDouble(float value) {
		//Float result = new Float(value);
		//return new FloatingDecimal(result.floatValue()).doubleValue();
		return Double.valueOf(Float.valueOf(value).toString()).doubleValue();
	}
}
