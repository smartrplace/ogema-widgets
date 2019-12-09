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
        readLock();
        try {
            result.put("text", "<h" + type + ">" + textEscaped + "</h" + type  + ">");
        } finally {
            readUnlock();
        }
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
