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
package de.iwes.widgets.api.widgets;

import java.util.Set;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * a group of {@link OgemaWidget}s that can be updated in a single Http-Request
 */
// FIXME polling not working; needs to be triggered once
public interface WidgetGroup {
	
	String getId();
	
	/**
	 * @return
	 * 		a new set of the widgets that constitute the group
	 */
	Set<OgemaWidget> getWidgets();
	
	boolean addWidget(OgemaWidget widget);
	
	boolean removeWidget(OgemaWidget widget);
	
	int size();
	
	/**
	 * Enable polling for all widgets of this group, sent in a single request.
	 * Note that polling for the individual widgets should not be enabled in addition;
	 * it would lead to additional polling requests.	 * 
	 * 
	 * @param interval
	 * 		polling interval in ms; -1 to disable polling
	 * 		
	 */
	void setPollingInterval(long interval);
	
	/**
	 * @return
	 * 		polling interval in ms, or -1 if polling is disabled
	 */
	long getPollingInterval();

	/**
	 * Equivalent to calling {@link OgemaWidget#setDefaultSendValueOnChange(boolean)}
	 * on all member widgets of this group.
	 * @param sendValue
	 */
	void setDefaultSendValueOnChange(boolean sendValue);
	
	/**
	 * Equivalent to calling {@link OgemaWidget#setDefaultVisibility(boolean)} 
	 * on all member widgets of this group.
	 * @param visible
	 */
	void setDefaultVisibility(boolean visible);
	
	/**
	 * Equivalent to calling {@link OgemaWidget#setWidgetVisibility(boolean, OgemaHttpRequest)}
	 * on all member widgets of this group.
	 * @param visible
	 * @param req
	 */
	void setWidgetVisibility(boolean visible, OgemaHttpRequest req);
	
}
