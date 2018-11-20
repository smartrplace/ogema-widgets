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
package de.iwes.widgets.html.form.slider;

import java.util.Arrays;
import java.util.Map;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class SliderData extends WidgetData {

	public static final WidgetStyle<Slider> BOOTSTRAP_BLUE = new WidgetStyle<Slider>("ogemaSlider",Arrays.asList("range","range-primary"),0);
	public static final WidgetStyle<Slider> BOOTSTRAP_RED = new WidgetStyle<Slider>("ogemaSlider",Arrays.asList("range","range-danger"),0);
	public static final WidgetStyle<Slider> BOOTSTRAP_GREEN = new WidgetStyle<Slider>("ogemaSlider",Arrays.asList("range","range-success"),0);
	public static final WidgetStyle<Slider> BOOTSTRAP_LIGHT_BLUE = new WidgetStyle<Slider>("ogemaSlider",Arrays.asList("range","range-info"),0);
	public static final WidgetStyle<Slider> BOOTSTRAP_DEFAULT = new WidgetStyle<Slider>("ogemaSlider",Arrays.asList("range","range-default"),0);
	public static final WidgetStyle<Slider> BOOTSTRAP_ORANGE = new WidgetStyle<Slider>("ogemaSlider",Arrays.asList("range","range-warning"),0);
    
    private int value;
    private int min, max;
    private String css = null;
    private boolean disabled = false;


    /************* constructor **********************/
    
    public SliderData(Slider slider) {
    	super(slider);
    }
    
    /******* Inherited methods ******/

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result = new JSONObject();
        if (css != null) {
        	result.put("css", css);
        }
        result.put("max", max);
        result.put("min", min);
        result.put("value", value);
        result.put("disabled", disabled);
        return result;
    }

    @Override
    public JSONObject onPOST(String data, OgemaHttpRequest req) {
    	JSONObject request = new JSONObject(data);
        value = request.getInt("data");
//            System.out.println("value: " + value);
        return request;
    }
    
    @Override
    protected String getWidthSelector() {
    	return ">div";
    }
    
    /******** public methods ***********/

    /**
     * Use {@link WidgetData#setStyle(WidgetStyle)} or {@link WidgetData#addCssItem(String, Map)}, or one of the related methods instead
     */
    @Deprecated
    public void setCss(String css) {
        this.css =css;
    }

    public int getValue() {
        return value;
    }

    public void setMin(int min) {
    	this.min = min;
    }
    public int getMin() {
    	return min;
    }

    public void setMax(int max) {
    	this.max = max;
    }
    public int getMax() {
    	return max;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    public void disable() {
    	this.disabled = true;
    }
    
    public void enable() {
    	this.disabled = false;
    }
    
    public boolean isEnabled() {
    	return !disabled;
    }

}
