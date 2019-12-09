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
package de.iwes.widgets.html.form.button;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class ButtonData extends WidgetData {
	
	public static final WidgetStyle<Button> BOOTSTRAP_BLUE = new WidgetStyle<Button>("ogemaButton",Arrays.asList("btn","btn-primary"),0);
	public static final WidgetStyle<Button> BOOTSTRAP_RED = new WidgetStyle<Button>("ogemaButton",Arrays.asList("btn","btn-danger"),0);
	public static final WidgetStyle<Button> BOOTSTRAP_GREEN = new WidgetStyle<Button>("ogemaButton",Arrays.asList("btn","btn-success"),0);
	public static final WidgetStyle<Button> BOOTSTRAP_LIGHT_BLUE = new WidgetStyle<Button>("ogemaButton",Arrays.asList("btn","btn-info"),0);
	public static final WidgetStyle<Button> BOOTSTRAP_DEFAULT = new WidgetStyle<Button>("ogemaButton",Arrays.asList("btn","btn-default"),0);
	public static final WidgetStyle<Button> BOOTSTRAP_ORANGE = new WidgetStyle<Button>("ogemaButton",Arrays.asList("btn","btn-warning"),0);
	public static final WidgetStyle<Button> BOOTSTRAP_LIGHTGREY = new WidgetStyle<Button>("ogemaButton",Arrays.asList("btn","btn-light"),0);
	public static final WidgetStyle<Button> BOOTSTRAP_DARKGREY = new WidgetStyle<Button>("ogemaButton",Arrays.asList("btn","btn-secondary"),0);
	public static final WidgetStyle<Button> BOOTSTRAP_LARGE = new WidgetStyle<Button>("ogemaButton",Arrays.asList("btn","btn-lg"),0);
	public static final WidgetStyle<Button> BOOTSTRAP_SMALL = new WidgetStyle<Button>("ogemaButton",Arrays.asList("btn","btn-sm"),0);
	
    public static final String defaultText = "Click me";
    private String text = defaultText;
    private String textEscaped = StringEscapeUtils.escapeHtml4(text);
    private String css = null;
    private String glyphicon = null;

    
    /************* constructor **********************/

    public ButtonData(Button button) {
    	super(button);
    }
        
    /******* Inherited methods ******/
	 
    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {  
        JSONObject result = new JSONObject();            
        result.put("text", textEscaped);
        if (css!= null) result.put("css", css);
        if (glyphicon != null) result.put("glyphicon", glyphicon);        
        return result;
    }

   @Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		return new JSONObject();
	}
   
    @Override
	protected String getWidthSelector() {
		return ">button";
	}
   
    /******** public methods ***********/
    
    /**
     * Use {@link WidgetData#setStyle(WidgetStyle)} or {@link WidgetData#addCssItem(String, Map)}, or one of the related methods instead
     */
    @Deprecated
    public void setCss(String css) {
        this.css = css;
    }

    public void setText(String text) {
        this.text = text;
        this.textEscaped = StringEscapeUtils.escapeHtml4(text);
    }
    public String getText() {
    	return text;
    }

    public void setGlyphicon(String glyphicon) {
        this.glyphicon = glyphicon;
        
    }

}
