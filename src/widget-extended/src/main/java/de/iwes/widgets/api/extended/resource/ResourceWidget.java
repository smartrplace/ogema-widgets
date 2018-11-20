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

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.plus.TemplateWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A widget that displays a collection of resources of a specified type
 * 
 */
public interface ResourceWidget<R extends Resource> extends TemplateWidget<R> {
	 
//	/**
//	 * This method should only be supported if the widget has update mode {@link UpdateMode#MANUAL}.
//	 * @param resource
//	 * @return
//	 * @throws UnsupportedOperationException if the widget does not have update mode MANUAL.
//	 */
//	boolean addItem(R resource, OgemaHttpRequest req) throws UnsupportedOperationException;
//	
//	/**
//	 * This method should only be called if this widget has update mode {@link UpdateMode#MANUAL}.
//	 * @param resource
//	 * @return
//	 * @throws UnsupportedOperationException if the widget does not have update mode MANUAL.
//	 */
//	boolean removeItem(R resource, OgemaHttpRequest req) throws UnsupportedOperationException;
//	
//	List<R> getItems(OgemaHttpRequest req);
	
	UpdateMode getUpdateMode();
	
	void setType(Class<? extends R> type, OgemaHttpRequest req);

	Class<? extends R> getType(OgemaHttpRequest req);
}
