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

    /**
     * @deprecated Use {@link OgemaWidgetBase#addStyle(org.ogema.tools.widget.api.WidgetStyle)} or {@link OgemaWidgetBase#addCssItem(String, Map)}, or one of the related methods instead
     */
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
