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
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS
 * Fraunhofer ISE
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.model.user;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.stakeholders.LegalEntity;

public interface NaturalPerson extends LegalEntity {
	/**
	 * Note: If the name is not split into first, middle and last name, use only LegalEntity.userName
	 * for full name representation
	 */
	StringResource firstName();
	StringResource middleName();
	StringResource lastName();

	TimeResource dataOfBirth();

	/** Citizenship */
	//ResourceList<Region> citizenships();
	
	StringResource userRole();
	
	/** If true then the user access data can also be used for login as mobile user on
	 * standard servlets
	 */
	BooleanResource restAccessEnabled();
}
