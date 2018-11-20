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

import java.util.Map;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class Slider extends OgemaWidgetBase<SliderData> {

    private static final long serialVersionUID = 550713654103033621L;
    private int defaultMin,defaultMax,defaultVal;

	/************* constructor **********************/
    
    public Slider(WidgetPage<?> page, String id, int min, int max, int value) {
    	this(page, id, min, max, value, false);
    }
    
    public Slider(WidgetPage<?> page, String id, int min, int max, int value, boolean globalWidget) {
    	super(page, id ,globalWidget);
        defaultMax = max;
        defaultMin = min;
        defaultVal = value;
    }
    
    public Slider(OgemaWidget parent, String id, OgemaHttpRequest req, int min, int max, int value) {
    	super(parent, id, req);
        defaultMax = max;
        defaultMin = min;
        defaultVal = value;
    }
    
  	/******* Inherited methods ******/

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Slider.class;
    }

	@Override
	public SliderData createNewSession() {
		return new SliderData(this);
	}
	
	@Override
	protected void setDefaultValues(SliderData opt) {
		opt.setMax(defaultMax);
		opt.setMin(defaultMin);
		opt.setValue(defaultVal);
		super.setDefaultValues(opt);
	}

    /******** public methods ***********/

    public void setDefaultMin(int defaultMin) {
		this.defaultMin = defaultMin;
	}

	public void setDefaultMax(int defaultMax) {
		this.defaultMax = defaultMax;
	}

	public void setDefaultVal(int defaultVal) {
		this.defaultVal = defaultVal;
	}

    public int getValue(OgemaHttpRequest req) {
        return getData(req).getValue();
    }

    public void setMin(int min, OgemaHttpRequest req) {
    	getData(req).setMin(min);
    }
    public int getMin(OgemaHttpRequest req) {
    	return getData(req).getMin();
    }

    public void setMax(int max, OgemaHttpRequest req) {
    	getData(req).setMax(max);
    }
    public int getMax(OgemaHttpRequest req) {
    	return getData(req).getMax();
    }

    public void setValue(int value, OgemaHttpRequest req) {
    	getData(req).setValue(value);
    }
    
    public void disable(OgemaHttpRequest req) {
    	getData(req).disable();
    }
    
    public void enable(OgemaHttpRequest req) {
    	getData(req).enable();
    }
    
    public boolean isEnabled(OgemaHttpRequest req) {
    	return getData(req).isEnabled();
    }
    
    /**
     * @deprecated Use {@link OgemaWidgetBase#addStyle(org.ogema.tools.widget.api.WidgetStyle, OgemaHttpRequest)} or 
     * {@link OgemaWidgetBase#addCssItem(String, Map, OgemaHttpRequest)}, or one of the related methods instead
     */
    @Deprecated
    public void setCss(String css, OgemaHttpRequest req) {
        getData(req).setCss(css);
    }


}
