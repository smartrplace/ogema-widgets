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
