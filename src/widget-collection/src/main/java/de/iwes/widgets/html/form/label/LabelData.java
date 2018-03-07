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
	
	@Deprecated
	public static final WidgetStyle<Label> BOOTSTRAP_BLUE = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-primary"),0);
	@Deprecated
	public static final WidgetStyle<Label> BOOTSTRAP_RED = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-danger"),0);
	@Deprecated
	public static final WidgetStyle<Label> BOOTSTRAP_GREEN = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-success"),0);
	@Deprecated
	public static final WidgetStyle<Label> BOOTSTRAP_LIGHT_BLUE = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-info"),0);
	@Deprecated
	public static final WidgetStyle<Label> BOOTSTRAP_DEFAULT = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-default"),0);
	@Deprecated
	public static final WidgetStyle<Label> BOOTSTRAP_ORANGE = new WidgetStyle<Label>("labelText",Arrays.asList("label","label-warning"),0);

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
