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
     * @deprecated Use {@link OgemaWidgetBase#addStyle(org.ogema.tools.widget.api.WidgetStyle)} or {@link OgemaWidgetBase#addCssItem(String, Map)}, or one of the related methods instead
     */
    @Deprecated
    public void setCss(String css, OgemaHttpRequest req) {
        getData(req).setCss(css);
    }


}
