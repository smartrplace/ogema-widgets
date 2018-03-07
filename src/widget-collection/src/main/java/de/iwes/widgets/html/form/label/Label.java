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

import org.apache.commons.lang3.StringEscapeUtils;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/** Dynamic text to be displayed
 *
 * @author tgries
 */
public class Label extends OgemaWidgetBase<LabelData>  {
    
    private static final long serialVersionUID = 7367326133405921539L;
    private String defaultText = "Lorem ipsum";
    private String defaultTextEscaped = defaultText;
    private String defaultColorString = null;

    /*********** Constructors **********/
    
    public Label(WidgetPage<?> page, String id) {
    	this(page,id,null);
    }
    public Label(WidgetPage<?> page, String id, long defaultUpdateRate) {
    	this(page,id,null);
    	setDefaultPollingInterval(defaultUpdateRate);
    }
    public Label(WidgetPage<?> page, String id, long defaultUpdateRate, OgemaHttpRequest req) {
    	this(page,id, null, req);
    	setDefaultPollingInterval(defaultUpdateRate);
    }
    
    public Label(WidgetPage<?> page, String id, String text) {
    	this(page, id, text, false);
    }
    public Label(WidgetPage<?> page, String id, String text, OgemaHttpRequest req) {
    	super(page, id, req);
    	this.defaultText = text;
    	this.defaultTextEscaped = StringEscapeUtils.escapeHtml4(text);
    }
    
    public Label(WidgetPage<?> page, String id, boolean globalWidget) {
    	this(page, id, null, globalWidget);
    }
    
    public Label(WidgetPage<?> page, String id, String text, boolean globalWidget) {
    	super(page, id, globalWidget);
    	this.defaultText = text;
    	this.defaultTextEscaped = StringEscapeUtils.escapeHtml4(text);
    }
    
    public Label(OgemaWidget parent, String id, OgemaHttpRequest req) {
    	super(parent,id,req);
    }
    public Label(OgemaWidget parent, String id, String text, OgemaHttpRequest req) {
    	super(parent, id, req);
    	this.defaultText = text;
    	this.defaultTextEscaped = StringEscapeUtils.escapeHtml4(text);
    }
    
    /******* Inherited methods ******/
    

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Label.class;
    }

	@Override
	public LabelData createNewSession() {
		return new LabelData(this);
	}
	
	@Override
	protected void setDefaultValues(LabelData opt) {
		opt.text = defaultText;
		opt.textEscaped = defaultTextEscaped;
		if (defaultColorString!=null)
			opt.setColor(defaultColorString);
		super.setDefaultValues(opt);
	}

    /******* Public methods ******/
	
	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
		this.defaultTextEscaped = StringEscapeUtils.escapeHtml4(defaultText);
	}
	
    /**
     * Never feed unvalidated user input into this method, as this enables XSS attacks. It is recommended to prefer 
     * {@link #setDefaultText(String)} where possible.
     * <br>
     * The passed String argument will be interpreted as Html.
     * @param defaultHtml
     */
	public void setDefaultHtml(String defaultHtml) {
		this.defaultText = defaultHtml;
		this.defaultTextEscaped = defaultHtml;
	}
	
	/** 
	 * see {@link #setColor(String, OgemaHttpRequest)}
	 * @param colorString
	 */
    public void setDefaultColor(String colorString) {
    	defaultColorString = colorString;
    }
    
    /**
     * Never feed unvalidated user input into this method, as this enables XSS attacks. It is recommended to prefer 
     * {@link #setText(String)} where possible.
     * <br>
     * The passed String argument will be interpreted as Html.
     * @param html
     */
    public void setHtml(final String html, OgemaHttpRequest req) {
    	getData(req).setHtml(html);
    }

    public void setText(String text, OgemaHttpRequest req) {
    	getData(req).setText(text);
    }
    
    public String getText(OgemaHttpRequest req) {
    	return getData(req).getText();
    }
    
    /**
 	 * See also {@link OgemaWidgetBase#setStyle(org.ogema.tools.widget.api.WidgetStyle, OgemaHttpRequest)},
 	 * and predefined (static) styles in {@link LabelData}.
     * @param colorString
     * 		a valid Html color string, e.g. "#FF6633", or "blue".
     * @param req
     */
    public void setColor(String colorString, OgemaHttpRequest req) {
    	getData(req).setColor(colorString);
    }
    
}
