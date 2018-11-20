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

import java.util.Map;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.plus.SubmitWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.SubmitUtil;

/**
 * A button. The corresponding Html tag is <code>&lt;button&gt;</code>. 
 * 
 * This can be used to submit form data: a widget whose data shall be submitted 
 * when the user clicks the button should be passed to the {@link #addWidget(OgemaWidget)}
 * method.
 */
public class Button extends OgemaWidgetBase<ButtonData> implements SubmitWidget {
	
    private static final long serialVersionUID = 550713654103033621L;
    private String defaultText = null;
    private final SubmitUtil sutil;
    
    /************* constructor **********************/

    public Button(WidgetPage<?> page, String id) {
    	this(page, id, null);
    }
    
    public Button(WidgetPage<?> page, String id, String text) {
    	this(page, id, text, false);
    }
    public Button(WidgetPage<?> page, String id, String text, OgemaHttpRequest req) {
    	super(page, id, req);
    	this.defaultText = text;
    	sutil = new SubmitUtil(this);
    }
    
    public Button(WidgetPage<?> page, String id, boolean globalWidget) {
    	this(page, id, null, globalWidget);
    }
    
    public Button(WidgetPage<?> page, String id, String text, boolean globalWidget) {
    	super(page, id, globalWidget);
    	this.defaultText = text;
    	sutil = new SubmitUtil(this);
    }
    
    public Button(OgemaWidget parent, String id, OgemaHttpRequest req) {
    	super(parent, id, req);
    	sutil = new SubmitUtil(this);
    }

    public Button(OgemaWidget parent, String id, String text, OgemaHttpRequest req) {
    	super(parent, id, req);
    	this.defaultText = text;
    	sutil = new SubmitUtil(this);
    }
    
    
    /******* Inherited methods ******/
    
    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Button.class;
    }

	@Override
	public ButtonData createNewSession() {
		return new ButtonData(this);
	}
	
	@Override
	protected void setDefaultValues(ButtonData opt) {
		if (defaultText != null) opt.setText(defaultText);
		super.setDefaultValues(opt);
	}
    
    /******** public methods ***********/
	
	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}

    public void setText(String text, OgemaHttpRequest req) {
    	getData(req).setText(text);
    }
    public String getText(OgemaHttpRequest req) {
    	return getData(req).getText();
    }

    public void setGlyphicon(String glyphicon, OgemaHttpRequest req) {
    	getData(req).setGlyphicon(glyphicon);
    }

    @Deprecated
    public void setCss(String css, OgemaHttpRequest req) {
        getData(req).setCss(css);
    }

	@Override
	public void addWidget(OgemaWidget widget) {
		sutil.registerWidget(widget);
	}

	@Override
	public void removeWidget(OgemaWidget widget) {
		sutil.unregisterWidget(widget);
	}

}
