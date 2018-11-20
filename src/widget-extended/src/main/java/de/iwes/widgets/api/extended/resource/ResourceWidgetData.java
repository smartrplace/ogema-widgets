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
package de.iwes.widgets.api.extended.resource;

import org.ogema.core.model.Resource;

import de.iwes.widgets.api.extended.plus.TemplateData;

public interface ResourceWidgetData<R extends Resource> extends TemplateData<R> {

//	/**
//	 * This method should only be called directly if the {@link ResourceWidget}
//	 * this belongs to has update mode {@link UpdateMode#MANUAL}.
//	 * @param resource
//	 * @return
//	 */
//	boolean addItem(R resource);
//	
//	/**
//	 * This method should only be called directly if the {@link ResourceWidget}
//	 * this belongs to has update mode {@link UpdateMode#MANUAL}.
//	 * @param resource
//	 * @return
//	 */
//	boolean removeItem(R resource);
//	
//	List<R> getItems();
	
	void setType(Class<? extends R> type);
	
	Class<? extends R> getType();
	
}
