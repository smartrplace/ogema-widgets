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

package de.iwes.widgets.html.form.label;

import java.util.Collections;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class HeaderData extends LabelData {
	
	private int type = 1;
	/**
	 * @deprecated use {@link WidgetData#TEXT_ALIGNMENT_CENTERED} instead
	 */
	@Deprecated
	public static final WidgetStyle<Header> CENTERED = new WidgetStyle<Header>("labelText",Collections.singletonList("text-center"),0);
	/**
	 * @deprecated use {@link WidgetData#TEXT_ALIGNMENT_LEFT} instead
	 */
	@Deprecated
	public static final WidgetStyle<Header> LEFT_ALIGNED = new WidgetStyle<Header>("labelText",Collections.singletonList("text-left"),0);
	/**
	 * @deprecated use {@link WidgetData#TEXT_ALIGNMENT_RIGHT} instead
	 */
	@Deprecated
	public static final WidgetStyle<Header> RIGHT_ALIGNED = new WidgetStyle<Header>("labelText",Collections.singletonList("text-right"),0);
   
   /*********** Constructor **********/
	
	public HeaderData(Header header) {
		super(header);
	}
    
	/******* Inherited methods ******/

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
    	JSONObject result = new JSONObject();	
   		result.put("text", "<h" + type + ">" + getText() + "</h" + type  + ">");
        return result;
    }

    /******* Public methods ******/

    public void setHeaderType(int type) {
    	if (type <= 0 || type > 6) throw new IllegalArgumentException("type argument must be a value between 1 and 6.");
        this.type = type;
    }
    
    public int getHeaderType() {
    	return type;
    }

    
    
}
