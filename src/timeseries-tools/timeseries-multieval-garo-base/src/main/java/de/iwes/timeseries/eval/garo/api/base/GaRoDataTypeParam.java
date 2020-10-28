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
package de.iwes.timeseries.eval.garo.api.base;

import java.util.List;

import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.util.resource.ResourceHelper.DeviceInfo;

/** Extended type that can provide additional parameter information for evaluations. The additional
 * information is no type information, but parameter information for specific evaluation calls
 *
 */
public class GaRoDataTypeParam extends GaRoDataType {
	protected final GaRoDataType baseType;
	protected final boolean isRequired;
	/** The framework provides information on the timeseries used here, e.g.
	 * the label and description (which is usually the same as the label). From this information is
	 * should be possible to find out which inputs belong to the same device, but this is not
	 * implemented yet. It might make sense to add information in the future on the room type,
	 * which currenty cannot be accessed by a GaRo evaluation directly (although the room types used
	 * can be limited).
	 */
	public List<TimeSeriesDataImpl> inputInfo;
	
	/*public static class DeviceInformation {
		String deviceId;
		Class<? extends Resource> resourceType;
	}*/
	public List<DeviceInfo> deviceInfo;
	
	public GaRoDataTypeParam(GaRoDataType baseType, boolean isRequired) {
		super(baseType.label(null), baseType.representingResourceType(), baseType.getLevel());
		this.baseType = baseType;
		this.isRequired = isRequired;
	}
	
	public boolean isRequired() {
		return isRequired;
	}
	
	@Override
	public String id() {
		return baseType.id();
	}
}
