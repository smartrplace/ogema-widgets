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
