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

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.textfield.TextFieldData;

/** TextField that can be edited by the user / page- or session-dependent version */
public class TextField extends OgemaWidgetBase<TextFieldData>  {

    private static final long serialVersionUID = 550713654103033621L;
    private String defaultValue = "";
    private String defaultPlaceholder = "Fill me";
    private String defaultType = "text";  
    
    /*********** Constructor **********/

    public TextField(WidgetPage<?> page, String id) {
    	this(page, id, false);
    }
    public TextField(WidgetPage<?> page, String id, SendValue sendValueOnChange) {
    	this(page, id, false, sendValueOnChange);
    }
   
    public TextField(WidgetPage<?> page, String id, String defaultValue) {
    	this(page, id, false);
    	this.defaultValue = defaultValue;
    }
    public TextField(WidgetPage<?> page, String id, String defaultValue, SendValue sendValueOnChange) {
    	this(page, id, false, sendValueOnChange);
    	this.defaultValue = defaultValue;
    }
    
    public TextField(WidgetPage<?> page, String id, boolean globalWidget) {
     	super(page, id, globalWidget);
     	addDefaultStyle(TextFieldData.FORM_CONTROL);
    }
    public TextField(WidgetPage<?> page, String id, boolean globalWidget, SendValue sendValueOnChange) {
     	super(page, id, globalWidget, sendValueOnChange);
     	addDefaultStyle(TextFieldData.FORM_CONTROL);
    }
    public TextField(WidgetPage<?> page, String id, SendValue sendValueOnChange, OgemaHttpRequest req) {
     	super(page, id, req);
     	setDefaultSendValueOnChange(sendValueOnChange == SendValue.TRUE);
     	addDefaultStyle(TextFieldData.FORM_CONTROL);
    }

    public TextField(OgemaWidget parent, String id, OgemaHttpRequest req) {
     	super(parent, id, req);
     	addDefaultStyle(TextFieldData.FORM_CONTROL);
    }
    public TextField(OgemaWidget parent, String id, SendValue sendValueOnChange, OgemaHttpRequest req) {
     	super(parent, id, sendValueOnChange, req);
     	addDefaultStyle(TextFieldData.FORM_CONTROL);
    }
    
    public TextField(WidgetPage<?> page, String id, OgemaHttpRequest req) {
     	super(page, id, req);
     	addDefaultStyle(TextFieldData.FORM_CONTROL);
    }
    
    /******* Inherited methods *****/
    
    @Override
	public TextFieldData createNewSession() {
    	return new TextFieldData(this);
    }
    
    @Override
    protected void setDefaultValues(TextFieldData opt) {
    	opt.setValue(defaultValue);
    	opt.setPlaceholder(defaultPlaceholder);
    	opt.setType(defaultType);
    	super.setDefaultValues(opt);
    }
    
    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
    	return TextField.class;
    }
    
    /*
     ********** Override in derived class ********
     */
    
    /**
     * Filter text entered by the user
     * @param newValue
     * @param req identifies the user session; ignore for globally valid filters
     * @return
     * 		true: value admissible; false: value will be rejected.
     */
    public boolean valueAdmissible(String newValue,OgemaHttpRequest req) {
    	return true;
    }
    
	 /*********** Public methods **********/
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public void setDefaultPlaceholder(String placeholder) {
		this.defaultPlaceholder = placeholder;
	}
	
	public void setDefaultType(TextFieldType type) {
		this.defaultType = type.getTypeString();
	}
	
	/**
	 * use {@link #setDefaultType(TextFieldType)} instead
	 */
	@Deprecated
	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}
	
    public void setPlaceholder(String placeholder,OgemaHttpRequest req) {
    	getData(req).setPlaceholder(placeholder);
    }

    public void setType(TextFieldType type,OgemaHttpRequest req) {
    	getData(req).setType(type.getTypeString());
    }
    
	/**
	 * use {@link #setType(TextFieldType, OgemaHttpRequest)} instead
	 */
	@Deprecated
    public void setType(String type,OgemaHttpRequest req) {
    	getData(req).setType(type);
    }
  
	public void setValue(String value,OgemaHttpRequest req) {
		getData(req).setValue(value);
	}
	
	public String getValue(OgemaHttpRequest req) {
		return getData(req).getValue();
	}

}
