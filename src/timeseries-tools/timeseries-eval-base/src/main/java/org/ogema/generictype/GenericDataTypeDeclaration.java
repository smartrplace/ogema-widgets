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
package org.ogema.generictype;

import java.util.List;

import org.ogema.core.model.Resource;

import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.widgets.template.LabelledItem;

/** A generic data type declaration that can be used to declare input and output types
 * to evaluation and import, merging modules etc. as well as input types to visualization and export
 * modules.<br>
 * Note that the interface {@link RequiredInputData} contains similar information for evaluation
 * input, but this is only suitable for input declaration, not for output declaration.<br>
 * In the future more information on the position in the OGEMA resource structure may be
 * provided such as the device type and the sensor/actor type. For now this shall just be coded
 * by the id and the respective documentation.
 */
public interface GenericDataTypeDeclaration extends LabelledItem {
	/** The fundamental description of a GenericDataTypeDeclaration usually is the OGEMA
	 * resource type. With {@link #attributes()} additional information may be provided,
	 * e.g. if representingResourceType returns TemperatureSensor the attribute may describe
	 * to what kind of device the sensor shall belong in order to match the GenericDataTypeDeclaration.
	 */
	Class<? extends Resource> representingResourceType();
	
	List<GenericAttribute> attributes();

	/** See {@link GenericDataTypeInstanceScalar}, {@link GenericDataTypeInstanceObject} and
	 * {@link GenericDataTypeInstanceTS} for the definition of these types*/
	public enum TypeCardinality {
		SINGLE_VALUE,
		OBJECT,
		TIME_SERIES
	}
	TypeCardinality typeCardinality();
	
	/** In most cases this should be a {@link PhysicalElement}. The field can be
	 * null if no device type or other higher OGEMA resource type shall be specified*/
	//Class<? extends Resource> deviceType(); 
	/** This may be null. The representingResourceType can be generated automatically
	 * in many cases if this is given
	 */
	//Class<? extends PhysicalElement> sensorActorType();
}
