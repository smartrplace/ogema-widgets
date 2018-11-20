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
package de.iwes.widgets.html.form.textfield;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * By default, this uses {@link TextFieldType#NUMBER}, but 
 * {@link TextFieldType#RANGE} is also possible (but see see Slider widget).
 */
public class NumberInputField extends TextField {

	private static final long serialVersionUID = 1L;
	private int defaultNrDecimals = 0; // better use 1?
	private float defaultMin = -Float.MAX_VALUE;
	private float defaultMax = Float.MAX_VALUE;
	private float defaultStep = 0.1f;
	
	/********** Constructors ********/

	public NumberInputField(WidgetPage<?> page, String id) {
		super(page, id);
		setDefaultType(TextFieldType.NUMBER);
		setDefaultStyles(null);
	}
	    
    public NumberInputField(WidgetPage<?> page, String id, boolean globalWidget, SendValue sendValueOnChange) {
     	super(page, id, globalWidget, sendValueOnChange);
		setDefaultType(TextFieldType.NUMBER);
		setDefaultStyles(null);
    }
    
    public NumberInputField(WidgetPage<?> page, String id, SendValue sendValueOnChange, OgemaHttpRequest req) {
     	super(page, id, sendValueOnChange, req);
		setDefaultType(TextFieldType.NUMBER);
		setDefaultStyles(null);
    }
   
    public NumberInputField(OgemaWidget parent, String id, OgemaHttpRequest req) {
     	super(parent, id, req);
		setDefaultType(TextFieldType.NUMBER);
		setDefaultStyles(null);
    }
	
	/********** Public methods ********/
    
    public void setDefaultNrDecimals(int nrDecimals) {
		defaultNrDecimals = nrDecimals;
	}
    
	public void setDefaultMin(float min) {
		defaultMin = min;
	}
    
    public void setDefaultMax(float max) {
    	defaultMax = max;
    }
    
    public void setDefaultStep(float step) {
    	defaultStep = step;
    }
    
	@Override
	public void setDefaultValue(String value) {
		if (value != null)
			Float.parseFloat(value); // throws NumberFormatException if String is not a number
		super.setDefaultValue(value);
	}
	
	public void setDefaultValue(float value) {
		setDefaultValue(Float.toString(value));
	}
	
	public void setDefaultValue(int value) {
		setDefaultValue(Integer.toString(value));
	}
    
	public int getNrDecimals(OgemaHttpRequest req) {
		return getData(req).getNrDecimals();
	}

	public void setNrDecimals(int nrDecimals,OgemaHttpRequest req) {
		getData(req).setNrDecimals(nrDecimals);
	}

	public float getMin(OgemaHttpRequest req) {
		return getData(req).getMin();
	}

	public void setMin(float min,OgemaHttpRequest req) {
		getData(req).setMin(min);
	}

	public float getMax(OgemaHttpRequest req) {
		return getData(req).getMax();
	}

	public void setMax(float max,OgemaHttpRequest req) {
		getData(req).setMax(max);
	}
    
	public float getStep(OgemaHttpRequest req) {
		return getData(req).getStep();
	}

	public void setStep(float step,OgemaHttpRequest req) {
		getData(req).setStep(step);
	}
	
	@Override
	public void setValue(String value, OgemaHttpRequest req) {
		if (value != null)
			Float.parseFloat(value); // throws NumberFormatException if String is not a number
		super.setValue(value, req);
	}
	
	public void setValue(float value, OgemaHttpRequest req) {
		setValue(Float.toString(value), req);
	}
	
	public void setValue(int value, OgemaHttpRequest req) {
		setValue(Integer.toString(value), req);
	}
	
    /********** Internal methods **********/
    
    public class NumberInputFieldOptions extends TextFieldData {

		private int nrDecimals = 0;
		private float min = Float.NaN;
		private float max = Float.NaN;
		private float step = Float.NaN;
    	
    	public NumberInputFieldOptions(NumberInputField nif) {
			super(nif);
		}
    	
    	@Override
    	public JSONObject retrieveGETData(OgemaHttpRequest req) {
    		JSONObject obj = super.retrieveGETData(req);
    		if (!Float.isNaN(min)) 
//    			addAttribute("min", String.valueOf(min));
    			obj.put("min", min);
    		if (!Float.isNaN(max))
//    			addAttribute("max", String.valueOf(max));
    			obj.put("max", max);
    		if (!Float.isNaN(step))
//    			addAttribute("step", String.valueOf(step));
    			obj.put("step", step);
    		obj.put("nrDecimals", nrDecimals);    	// TODO do something with it...
    		return obj;
    	}
 
		public int getNrDecimals() {
			return nrDecimals;
		}

		public void setNrDecimals(int nrDecimals) {
			this.nrDecimals = nrDecimals;
		}

		public float getMin() {
			return min;
		}

		public void setMin(float min) {
			this.min = min;
		}

		public float getMax() {
			return max;
		}

		public void setMax(float max) {
			this.max = max;
		}	
		
		public float getStep() {
			return step;
		}

		public void setStep(float step) {
			this.step = step;
		}
		
		private void removeStl(WidgetStyle<?> style) {
			removeStyle(style);
		}
		
    }
    
    @Override
    public NumberInputFieldOptions createNewSession() {
    	return new NumberInputFieldOptions(this);
    }
    
    @Override
    public NumberInputFieldOptions getData(OgemaHttpRequest req) {
    	return (NumberInputFieldOptions) super.getData(req);
    }
    
    @Override
    protected void setDefaultValues(TextFieldData opt) {
    	super.setDefaultValues(opt);
    	NumberInputFieldOptions opt2 = (NumberInputFieldOptions) opt;
    	opt2.setNrDecimals(defaultNrDecimals);
    	opt2.setMax(defaultMax);
    	opt2.setMin(defaultMin);
    	opt2.setStep(defaultStep);
    	opt2.removeStl(TextFieldData.FORM_CONTROL);
    }
 
}
