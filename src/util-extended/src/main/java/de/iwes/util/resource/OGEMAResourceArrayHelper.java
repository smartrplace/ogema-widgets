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

import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.array.TimeArrayResource;
import org.ogema.tools.resource.util.ValueResourceUtils;

/**
 * Static class that providing helper methods to add elements to a basic array resource
 * 
 * @deprecated use {@link ValueResourceUtils#appendValue(org.ogema.core.model.array.ArrayResource, Object)
 * instead
 */
public class OGEMAResourceArrayHelper {

	/**Add value to TimeArrayResource. Note that the helper has to re-write the entire array resource
	 * so this should not be performed too frequently.
	 * @param res
	 * @param value to be appended to the array
	 * @return new length of res
 * @deprecated use {@link ValueResourceUtils#appendValue(org.ogema.core.model.array.ArrayResource, Object)
 * instead
	 */
	public static int addLongValue(TimeArrayResource res, long value) {
		long[] arrOld = res.getValues();
		long[] arrNew = new long[arrOld.length+1];
		for(int i=0; i<arrOld.length; i++) {
			arrNew[i] = arrOld[i];
		}
		arrNew[arrOld.length] = value;
		res.setValues(arrNew);
		return arrNew.length;
	}

	/**Add value to StringArrayResource. Note that the helper has to re-write the entire array resource
	 * so this should not be performed too frequently.
	 * @param res
	 * @param value to be appended to the array
	 * @return new length of res
 * @deprecated use {@link ValueResourceUtils#appendValue(org.ogema.core.model.array.ArrayResource, Object)
 * instead
	 */
	public static int addStringValue(StringArrayResource res, String value) {
		String[] arrOld = res.getValues();
		String[] arrNew = new String[arrOld.length+1];
		for(int i=0; i<arrOld.length; i++) {
			arrNew[i] = arrOld[i];
		}
		arrNew[arrOld.length] = value;
		res.setValues(arrNew);
		return arrNew.length;
	}
}
