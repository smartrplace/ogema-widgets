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
package de.iwes.util.collectionother;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ValueResource;
import org.ogema.core.resourcemanager.ResourceValueListener;

import de.iwes.util.format.StringFormatHelper;

/** Instances of this class listen to a certain resource and providing the information when the last value
 * changed callback occured. This information can be accessed as time (long) value or as
 * String.
 * @author dnestle
 * @deprecated use {@link ValueResource#getLastUpdateTime()} instead
 */
@Deprecated
public class LastWriteTimeProvider implements ResourceValueListener<Resource> {
	private ApplicationManager appMan;
	private long lastCallbackTime = -1;

	/**
	 * 
	 * @param res resource to listen to; this does not include sub-resources
	 * @param appMan
	 */
	public LastWriteTimeProvider(Resource res, ApplicationManager appMan) {
		this.appMan = appMan;
		res.addValueListener(this, true);
	}
	
	@Override
	public void resourceChanged(Resource arg0) {
		lastCallbackTime = appMan.getFrameworkTime(); 		
	}

	/** Get time of last write operation on the resource*/
	public long getTimeValue() {
		return lastCallbackTime;
	}
	
	public long getDurationSinceTimeValue() {
		long deltaT = (appMan.getFrameworkTime() - lastCallbackTime);
		return deltaT;
	}
	
	/** Get the duration since the last write operation on the resource in a human readable form.
	 * @return depending on the duration it will be given as seconds, minutes or hours. If no
	 * write operation was detected since the initialization of the object it will return "no data",
	 * if the duration is greater than 99h it will return ">99h".
	 */
	public String getFormattedValue() {
		if(lastCallbackTime < 0) {
			return "no data";
		}
		long deltaT = (appMan.getFrameworkTime() - lastCallbackTime);
		return StringFormatHelper.getFormattedValue(deltaT);
	}
}
