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
package de.iwes.tools.system.supervision.model;

import org.ogema.core.model.simple.TimeResource;

public class SupervisionUtils {
	
	public final static long MIN_SUPERVISION_ITV = 60 * 1000;
	public final static long DEFAULT_RAM_SUPERVISION_ITV = 60*60*1000;
	public final static long DEFAULT_DISK_SUPERVISION_ITV = 24*60*60*1000;
	public final static long DEFAULT_RESOURCE_SUPERVISION_ITV = 60*60*1000;
	
	public static long getInterval(final TimeResource itvRes, long defaultItv) {
		long proposed = 0;
		if (itvRes.isActive())
			proposed = itvRes.getValue();
		if (proposed < MIN_SUPERVISION_ITV) {
			proposed = defaultItv;
			itvRes.<TimeResource> create().setValue(defaultItv);
			itvRes.activate(false);
		}
		return proposed;
	}

}
