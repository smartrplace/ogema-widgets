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
package de.iwes.widgets.html.multiselect;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownOption;

/** 
 *  Dropdown that supports to select multiple options.
 *  Based on the "chosen" jquery-plugin (http://harvesthq.github.io/chosen/)
 *  Does not yet support all features of chosen (e.g. groups), to be extended...
 *  @see TemplateMultiselect a version of the multiselect widget where the selectable
 *  	items are modeled on a class T
 *  @see de.iwes.widgets.html.form.dropdown.Dropdown for a version that only 
 *  	allows to select one item at a time.
 *  
 *  @author cnoelle
 */
public class Multiselect extends OgemaWidgetBase<MultiselectData> {

    private static final long serialVersionUID = -7033410423559705108L;
    private Collection<DropdownOption> defaultOptions = null;
    protected String defaultUrlParam = null;


	/*********** Constructors **********/

    public Multiselect(WidgetPage<?> page, String id) {
        this(page, id, null, SendValue.TRUE);
    }
    
    public Multiselect(WidgetPage<?> page, String id, boolean globalWidget) {
        this(page, id, globalWidget, SendValue.TRUE);
    }
    
    public Multiselect(WidgetPage<?> page, String id, Set<DropdownOption> values) {
        this(page,id,values, SendValue.TRUE);
    }

    public Multiselect(WidgetPage<?> page, String id, SendValue sendValuesOnChange) {
        this(page, id, null, sendValuesOnChange);
    }
    
    public Multiselect(WidgetPage<?> page, String id, boolean globalWidget, SendValue sendValuesOnChange) {
        super(page,id,globalWidget,sendValuesOnChange);
    }
    
    public Multiselect(WidgetPage<?> page, String id, SendValue sendValuesOnChange, OgemaHttpRequest req) {
    	this(page, id, null, sendValuesOnChange, req);
    }
    public Multiselect(WidgetPage<?> page, String id, Set<DropdownOption> values, SendValue sendValuesOnChange) {  
    	this(page,  id, values, sendValuesOnChange, null);
    }
    public Multiselect(WidgetPage<?> page, String id, Set<DropdownOption> values, SendValue sendValuesOnChange, OgemaHttpRequest req) {  
        super(page, id, req);
        setDefaultSendValueOnChange(sendValuesOnChange == SendValue.TRUE);
        if (values != null) {
            this.defaultOptions = values;
        }
    } 
    
    public Multiselect(OgemaWidget parent, String id, OgemaHttpRequest req) {
        super(parent, id, req);
    }
    
    public Multiselect(OgemaWidget parent, String id,  Set<DropdownOption> values,OgemaHttpRequest req) {
        this(parent, id, req);
        setOptions(values, null);  // FIXME this does not make sense...
    }
    
    /******* Inherited methods *****/

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Multiselect.class;
    }

	@Override
	public MultiselectData createNewSession() {
		return new MultiselectData(this);
	}
	
	@Override
	protected void setDefaultValues(MultiselectData opt) {
		if (defaultOptions != null) {
			opt.setOptions(defaultOptions);
		}
		if (defaultUrlParam != null)
			opt.setSelectByUrlParam(defaultUrlParam);
//		if (defaultWidth != null)
//			opt.setWidth(defaultWidth);
		super.setDefaultValues(opt);
	}

    /******* Public methods *****/
	
    
    public Collection<DropdownOption> getDefaultOptions() {
		return defaultOptions;
	}

	public void setDefaultOptions(Collection<DropdownOption> defaultOptions) {
		this.defaultOptions = defaultOptions;
	}
    
    public List<DropdownOption> getMultiselectOptions(OgemaHttpRequest req) {
    	return getData(req).getOptions();
    }
    
    public DropdownOption getOption(String value,OgemaHttpRequest req) {
    	return getData(req).getOption(value);
    }

    public void setOptions(Collection<DropdownOption> options,OgemaHttpRequest req) {
    	getData(req).setOptions(options);
    }

    public void addOption(String label, String value, boolean selected,OgemaHttpRequest req) {
    	getData(req).addOption(label, value, selected);
    }
    
    public boolean isEmpty(OgemaHttpRequest req) {
    	return getData(req).isEmpty();
    }
    
    public Collection<DropdownOption> getSelected(OgemaHttpRequest req) {
    	return getData(req).getSelected();
    }
    
    public Collection<String> getSelectedValues(OgemaHttpRequest req) {
    	return getData(req).getSelectedValues();
    }
    
    public Collection<String> getSelectedLabels(OgemaHttpRequest req) {
    	return getData(req).getSelectedLabels(req.getLocale());
    }

    public void selectSingleOption(String value,OgemaHttpRequest req) {
    	getData(req).selectSingleOption(value);
    }
    
    public void selectMultipleOptions(Collection<String> selectedOptions,OgemaHttpRequest req) {
    	getData(req).selectMultipleOptions(selectedOptions);
    }

    public void changeSelection(String value, boolean newState,OgemaHttpRequest req) {
    	getData(req).changeSelection(value, newState);
    }

    public void removeOption(String value,OgemaHttpRequest req) {
    	getData(req).removeOption(value);
    }

    
	public String getDefaultWidth() {
		throw new UnsupportedOperationException("is this required?");
	}
	
	/*
	 * @param defaultWidth
	 * 		e.g. a percentage: "30%",
	 * 		or nr of pixels: "100px"
	 */
	/*
	public void setDefaultWidth(String defaultWidth) {
		this.defaultWidth = defaultWidth;
	}
	*/
	
	public String getWidth(OgemaHttpRequest req) {
		final Map<String,String> items = getData(req).getCssItem("width");
		if (items == null || items.isEmpty())
			return null;
		return items.get("width");
	}
	
	public void setWidth(String width,OgemaHttpRequest req) {
		getData(req).setWidth(width);
	}
	
    /**
     * Set new dropdown options. Old ones not contained in values will be removed.
     * @param values
     * 		Map&lt;value, label&gt;
     * @param req
     */
	public void update(Map<String,String> values, OgemaHttpRequest req) {
		getData(req).update(values);
	}
	
	public void clear(OgemaHttpRequest req) {
		getData(req).clear();
	}
    

	public void setDefaultSelectByUrlParam(String param) {
		defaultUrlParam = param;
	}

	public void setSelectByUrlParam(String param, OgemaHttpRequest req) {
		getData(req).setSelectByUrlParam(param);
	}

	public String getSelectByUrlParam(OgemaHttpRequest req) {
		return getData(req).getSelectByUrlParam();
	}
	
}
