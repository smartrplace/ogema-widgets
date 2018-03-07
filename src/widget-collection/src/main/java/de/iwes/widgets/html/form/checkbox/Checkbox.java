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

package de.iwes.widgets.html.form.checkbox;

import java.util.Map;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * @deprecated Checkbox does not distinguish between id and label ->  
 * consider using {@link Checkbox2} instead
 */
@Deprecated
public class Checkbox extends OgemaWidgetBase<CheckboxData> {

    private static final long serialVersionUID = 550713654103033621L;
    private Map<String,Boolean> defaultList = null;
    private String defaultTitle = null;
    
    
    /************* constructor **********************/

    public Checkbox(WidgetPage<?> page, String id) {
    	this(page, id,false);
    }

    public Checkbox(WidgetPage<?> page, String id, SendValue sendValueOnChange) {
    	super(page, id, sendValueOnChange);
    }
    
    public Checkbox(OgemaWidget parent, String id, OgemaHttpRequest req) {
    	super(parent, id, req);
    }
    
    public Checkbox(WidgetPage<?> page, String id, SendValue sendValueOnChange, OgemaHttpRequest req) {
    	super(page, id,  req);
    	setDefaultSendValueOnChange(sendValueOnChange == SendValue.TRUE);
    }    
    
    public Checkbox(OgemaWidget parent, String id, SendValue sendValueOnChange, OgemaHttpRequest req) {
    	super(parent, id, sendValueOnChange, req);
    }    
    
    public Checkbox(WidgetPage<?> page, String id, boolean globalWidget) {
    	super(page, id, globalWidget);
    }

    /******* Inherited methods ******/

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Checkbox.class;
    }
    
	@Override
	public CheckboxData createNewSession() {
		return new CheckboxData(this);
	}
	
	@Override
	protected void setDefaultValues(CheckboxData opt) {
		if (defaultList != null) opt.setCheckboxList(defaultList);
		if (defaultTitle != null) opt.setTitle(defaultTitle);
		super.setDefaultValues(opt);
	}

    /******** public methods ***********/
	
    public Map<String, Boolean> getDefaultList() {
		return defaultList;
	}

	public void setDefaultList(Map<String, Boolean> defaultList) {
		this.defaultList = defaultList;
	}

	public String getDefaultTitle() {
		return defaultTitle;
	}

	public void setDefaultTitle(String defaultTitle) {
		this.defaultTitle = defaultTitle;
	}

    public Map<String, Boolean> getCheckboxList(OgemaHttpRequest req) {
        return getData(req).getCheckboxList();
    }
    
    public void setCheckboxList(Map<String, Boolean> newList, OgemaHttpRequest req) {
    	getData(req).setCheckboxList(newList);
    }
    
    public String getTitle(OgemaHttpRequest req) {
		return getData(req).getTitle();
	}

	public void setTitle(String title, OgemaHttpRequest req) {
		getData(req).setTitle(title);
	}

    public void deselectAll(OgemaHttpRequest req) {
    	getData(req).deselectAll();
    }
    
    public void selectAll(OgemaHttpRequest req) {
    	getData(req).selectAll();
    }

}
