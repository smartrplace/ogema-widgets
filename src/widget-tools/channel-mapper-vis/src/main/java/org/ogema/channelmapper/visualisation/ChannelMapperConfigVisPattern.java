/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
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
