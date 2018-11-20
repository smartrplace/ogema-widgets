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
package org.ogema.channelmapper.visualisation;

import org.ogema.channelmapperv2.config.ChannelMapperConfiguration;
import org.ogema.channelmapperv2.config.PersistentChannelLocator;
import org.ogema.core.channelmanager.ChannelConfiguration.Direction;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.DefaultValue;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.DisplayValue;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.Entry;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.EntryType;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.FilterString;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.FilterValueLong;

public class ChannelMapperConfigVisPattern extends ResourcePattern<ChannelMapperConfiguration> {

	public ChannelMapperConfigVisPattern(Resource match) {
		super(match);
	}
	
	@EntryType(setAsReference=true)
	public final SingleValueResource target = model.target();
	
	@DisplayValue
	public final String resourceType() {
		return target.getResourceType().getName();
	}
	
	@Entry(show=false)
	public final PersistentChannelLocator channelLocator = model.channelLocator();
	
	@FilterString(regexp="^(?=\\s*\\S).*$") // not empty
	public final StringResource driverId = channelLocator.driverId();
	@FilterString(regexp="^(?=\\s*\\S).*$") // not empty
	public final StringResource interfaceId = channelLocator.interfaceId();
	@FilterString(regexp="^(?=\\s*\\S).*$") // not empty
	public final StringResource deviceAddress = channelLocator.deviceAddress();
	public final StringResource parameters = channelLocator.parameters();
	@FilterString(regexp="^(?=\\s*\\S).*$") // not empty
	public final StringResource channelAddress = channelLocator.channelAddress();
	
	@EntryType(enumType=Direction.class, setAsReference=false)
	public final StringResource direction = model.direction();
	
	@FilterValueLong(lowerBound=1)
	@DefaultValue(value="10000")
	@EntryType(rawData=true,setAsReference=false)
	@Existence(required=CreateMode.OPTIONAL)
	public final TimeResource samplingInterval = model.samplingInterval();
	
	@DefaultValue(value="1")
	@Existence(required=CreateMode.OPTIONAL)
	public final FloatResource scalingFactor =  model.scalingFactor();
	
	@DefaultValue(value="0")
	@Existence(required=CreateMode.OPTIONAL)
	public final FloatResource valueOffset = model.valueOffset();
	
	@Existence(required=CreateMode.OPTIONAL)
	public final StringResource description = model.description();

	@Entry(show=false)
	@Existence(required=CreateMode.OPTIONAL)
	public final BooleanResource channelRegistered = model.registrationSuccessful();
	
	@DisplayValue
	public final String registrationSuccessful() {
		return String.valueOf(channelRegistered.isActive() && channelRegistered.getValue());
	}
	
}
