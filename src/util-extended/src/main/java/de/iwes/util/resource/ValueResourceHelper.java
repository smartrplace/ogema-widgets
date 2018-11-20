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

import org.ogema.core.model.Resource;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.model.units.TemperatureResource;

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
}
