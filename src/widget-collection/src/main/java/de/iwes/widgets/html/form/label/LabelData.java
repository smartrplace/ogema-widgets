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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 *
 * @author tgries
 */
public class LabelData extends WidgetData {
	
	public static final WidgetStyle<Label> BOOTSTRAP_BLUE = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-primary"),0);
	public static final WidgetStyle<Label> BOOTSTRAP_RED = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-danger"),0);
	public static final WidgetStyle<Label> BOOTSTRAP_GREEN = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-success"),0);
	public static final WidgetStyle<Label> BOOTSTRAP_LIGHT_BLUE = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-info"),0);
	public static final WidgetStyle<Label> BOOTSTRAP_GREY = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-default"),0);
	public static final WidgetStyle<Label> BOOTSTRAP_ORANGE = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-warning"),0);
	//public static final WidgetStyle<Label> BOOTSTRAP_LIGHTGREY = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-light"),0);
	//public static final WidgetStyle<Label> BOOTSTRAP_DARKGREY = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-secondary"),0);

    String text = "";
    String textEscaped = "";
    String css = null;
   
   /*********** Constructor **********/
	
	public LabelData(Label label) {
		super(label);
	}
    
	/******* Inherited methods ******/

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
    	JSONObject result = new JSONObject();	
    	readLock();
    	try {
	   		if (css != null) result.put("css", css);
	   		result.put("text", textEscaped);
    	} finally {
    		readUnlock();
    	}
        return result;
    }
    
    @Override
    protected String getWidthSelector() {
    	return ">span";
    }
    
    /******* Public methods ******/

    public void setText(String text) {
    	writeLock();
    	try {
	    	if (text == null) {
	    		this.text = "";
	    		this.textEscaped = "";
	    	}
	    	else {
	    		this.text = text;
	    		this.textEscaped = StringEscapeUtils.escapeHtml4(text);
	    	}
    	} finally {
    		writeUnlock();
    	}
    }
    
    /**
     * Never feed unvalidated user input into this method, as this enables XSS attacks. It is recommended to prefer 
     * {@link #setText(String)} where possible.
     * <br>
     * The passed String argument will be interpreted as Html.
     * @param html
     */
    protected void setHtml(final String html) {
    	writeLock();
    	try {
	    	if (html == null) {
	    		this.text = "";
	    		this.textEscaped = "";
	    	}
	    	else {
	    		this.text = html;
	    		this.textEscaped = html;
	    	}
    	} finally {
    		writeUnlock();
    	}
    }
    
    public String getText() {
    	readLock();
    	try {
    		return text;
    	} finally {
    		readUnlock();
    	}
    }

    public void setColor(String colorString) {
    	Map<String,String> map = new HashMap<String, String>();
    	if (colorString != null && !colorString.isEmpty()) {
    		if (!colorString.startsWith("#") && isInt(colorString))
    			colorString = "#" + colorString;
    		map.put("color", colorString);
    	}
    	addCssItem("#labelText", map);
    }
    
    private static final boolean isInt(String s) {
    	try {
    		Integer.parseInt(s);
    		return true;
    	} catch (Exception e) {
    		return false;
    	}
    }
    
}
