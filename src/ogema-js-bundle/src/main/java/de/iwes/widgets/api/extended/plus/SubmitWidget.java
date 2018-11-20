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
package de.iwes.widgets.api.extended.plus;

import de.iwes.widgets.api.widgets.OgemaWidget;

/**
 * A widget that is used to submit POST data of several 
 * other widgets.
 */
public interface SubmitWidget extends OgemaWidget {

	/**
	 * Adding a widget to the group of dependent widgets 
	 * implies that its POST data will be submitted with
	 * every POST request of the governing widget, and that 
	 * no individual POST requests will be sent when the data
	 * changes on the client-side. This latter behaviour can 
	 * be overwritten by calling {@link #setDefaultSendValueOnChange(boolean)}
	 * with paramter <code>true</code> afterwards.
	 * @param widget
	 */
	public void addWidget(OgemaWidget widget);
	
	public void removeWidget(OgemaWidget widget);
	
}
