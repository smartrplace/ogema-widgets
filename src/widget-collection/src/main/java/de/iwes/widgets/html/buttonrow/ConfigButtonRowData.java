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

package de.iwes.widgets.html.buttonrow;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**Widget that presents three buttons:
 * OK: Causes a POST event and closes the Tab/Window
 * Cancel: Only closes the Tab/Window
 * Save: Only sends POST event
 * 
 * TODO: Currently it is not possible to exchange the writing on the buttons
 * @author dnestle
 *
 */
public class ConfigButtonRowData extends WidgetData {
	
    /*********** Constructor **********/
	
	public ConfigButtonRowData(ConfigButtonRow cbr) {
		super(cbr);
	}
	
    /******* Inherited methods ******/

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		return new JSONObject();
	};
	
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		return new JSONObject();
	}
	
}
