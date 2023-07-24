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
package de.iwes.widgets.html.form.dropdown;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.plus.SubmitWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.NaturalStringComparator;
import de.iwes.widgets.html.SubmitUtil;
import de.iwes.widgets.template.LabelledItem;

/**
 * A dropdown, corresponding to the Html &lt;select&gt; tag.
 * @see de.iwes.widgets.html.multiselect.Multiselect allows to select more
 * 		than one item at a time
 * @see TemplateDropdown a version of the dropdown widget where the selectable
 *  	items are modeled on a class T
 */
public class Dropdown extends OgemaWidgetBase<DropdownData> implements SubmitWidget {

    private static final long serialVersionUID = -7033410493559705108L;
	protected final SubmitUtil util;
    private Collection<DropdownOption> defaultOptions = null;
	private boolean defaultAddEmptyOption = false; 
	private String defaultEmptyOptLabel = null;
	
    Comparator<DropdownOption> comparator = new Comparator<DropdownOption>() {
		
		@Override
		public int compare(DropdownOption o1, DropdownOption o2) {
			String s1 = o1.getLabel();
			String s2 = o2.getLabel();
			return NaturalStringComparator.compareNatural(s1, s2);
		}
	};

	/*********** Constructors **********/

	
    public Dropdown(WidgetPage<?> page, String id) {
        this(page, id, null, SendValue.TRUE);
    }
    
    public Dropdown(WidgetPage<?> page, String id, boolean globalWidget) {
    	super(page, id, globalWidget);
 		this.util = new SubmitUtil(this);
    }
    
    public Dropdown(WidgetPage<?> page, String id, Set<DropdownOption> values) {
        this(page,id,values, SendValue.TRUE);
    }

    public Dropdown(WidgetPage<?> page, String id, SendValue sendValuesOnChange) {
        this(page, id, null, sendValuesOnChange);
    }
    public Dropdown(WidgetPage<?> page, String id, SendValue sendValuesOnChange, OgemaHttpRequest req) {
    	this(page, id, null, sendValuesOnChange, req);
    }
    public Dropdown(WidgetPage<?> page, String id, Set<DropdownOption> values, SendValue sendValuesOnChange) {  
    	this(page,  id, values, sendValuesOnChange, null);
    }
    public Dropdown(WidgetPage<?> page, String id, Set<DropdownOption> values, SendValue sendValuesOnChange, OgemaHttpRequest req) {  
        super(page, id, req);
        setDefaultSendValueOnChange(sendValuesOnChange == SendValue.TRUE);
        if (values != null) {
            this.defaultOptions = values;
        }
		this.util = new SubmitUtil(this);
    } 
    
    public Dropdown(OgemaWidget parent, String id, OgemaHttpRequest req) {
        super(parent, id, req);
		this.util = new SubmitUtil(this);
    }
    
    public Dropdown(OgemaWidget parent, String id,  Set<DropdownOption> values,OgemaHttpRequest req) {
        this(parent, id, req);
        setOptions(values, req);
    }
    
    /******* Inherited methods *****/

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Dropdown.class;
    }

	@Override
	public DropdownData createNewSession() {
		return new DropdownData(this);
	}
	
	@Override
	protected void setDefaultValues(DropdownData opt) {
		if (defaultEmptyOptLabel != null)  
			opt.setAddEmptyOption(defaultAddEmptyOption, defaultEmptyOptLabel);
		else
			opt.setAddEmptyOption(defaultAddEmptyOption);
		if (defaultOptions != null) {
			opt.setOptions(defaultOptions);
		}
		super.setDefaultValues(opt);
	}

    /******* Public methods *****/
	
    /**
     * Returns a live view of the default options. May be null.
     * @return
     */
    public Collection<DropdownOption> getDefaultOptions() {
		return defaultOptions;
	}

	public void setDefaultOptions(Collection<DropdownOption> defaultOptions) {
		this.defaultOptions = defaultOptions;
	}
    
    public List<DropdownOption> getDropdownOptions(OgemaHttpRequest req) {
    	return getData(req).getOptions();
    }

    public void setOptions(Collection<DropdownOption> options,OgemaHttpRequest req) {
    	getData(req).setOptions(options);
    }

    public void addOption(String label, String value, boolean selected,OgemaHttpRequest req) {
    	getData(req).addOption(label, value, selected);
    }
    
    public void addOption(LabelledItem item, boolean selected,OgemaHttpRequest req) {
    	getData(req).addOption(item, selected);
    }
    
    public DropdownOption getSelected(OgemaHttpRequest req) {
    	return getData(req).getSelected();
    }
    
    public String getSelectedValue(OgemaHttpRequest req) {
    	return getData(req).getSelectedValue();
    }
    
    public String getSelectedLabel(OgemaHttpRequest req) {
    	return getData(req).getSelectedLabel(req.getLocale());
    }

    /**
     * Note: this leaves the selection unchanged if the specified value is not found
     * among the current selection options.
     * @param value
     * @param req
     */
    public void selectSingleOption(String value,OgemaHttpRequest req) {
    	getData(req).selectSingleOption(value);
    }
    
    public void selectMultipleOptions(Collection<String> selectedOptions,OgemaHttpRequest req) {
    	getData(req).selectMultipleOptions(selectedOptions);
    }

    public void removeOption(String value,OgemaHttpRequest req) {
    	getData(req).removeOption(value);
    }

    /**
     * Pass null to disable comparator based sorting
     * @param comparator
     */
    public void setComparator(Comparator<DropdownOption> comparator) {
		this.comparator = comparator;
    }
    
    /**
     * Check whether an option with the demanded value exists.
     * @param value
     * @param req
     * @return
     */
    public boolean containsValue(String value, OgemaHttpRequest req) {
    	return getData(req).containsValue(value);
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
     
     /**
      * Set new dropdown options. Old ones not contained in values will be removed.
      * 
      * @param values
      * 		Map&lt;value, label&gt;
      * @param selected 
      * 		Specify a key for the newly selected item. This is only applied, if the previsouly 
      * 		selected key is no longer contained the map.
      * @param req
      */
      public void update(Map<String,String> values, String selected, OgemaHttpRequest req) {
      	 getData(req).update(values, selected);
      }
 	
     /**
      * Add an empty option when using the {@link #update(Map, OgemaHttpRequest)} or
      * {@link #setOptions(Collection, OgemaHttpRequest)} methods? The id of the 
      * empty item will be chosen as {@link DropdownData#EMPTY_OPT_ID}.
      * @param defaultAddEmptyOption
      */
 	public void setDefaultAddEmptyOption(boolean defaultAddEmptyOption) {
 		this.defaultAddEmptyOption = defaultAddEmptyOption;
 	}
 	
    /**
     * Add an empty option when using the {@link #update(Map, OgemaHttpRequest)} or
     * {@link #setOptions(Collection, OgemaHttpRequest)} methods? The id of the 
     * empty item will be chosen as {@link DropdownData#EMPTY_OPT_ID}.
     * @param defaultAddEmptyOption
     * @param emptyOptionLabel
     */
 	public void setDefaultAddEmptyOption(boolean defaultAddEmptyOption, String emptyOptionLabel) {
 		this.defaultAddEmptyOption = defaultAddEmptyOption;
 		this.defaultEmptyOptLabel = emptyOptionLabel;
 	}

 	/**
 	 * @see #setAddEmptyOption(boolean, OgemaHttpRequest)
 	 * @param req
 	 * @return
 	 */
 	public boolean isAddEmptyOption(OgemaHttpRequest req) {
 		return getData(req).isAddEmptyOption();
 	}

 	/**
 	 * Add an empty option when using the {@link #update(Map, OgemaHttpRequest)} method?
 	 * The id of the empty item will be chosen as {@link DropdownData#EMPTY_OPT_ID}. 
 	 * @param addEmptyOption
 	 * @param req
 	 */
 	public void setAddEmptyOption(boolean addEmptyOption, OgemaHttpRequest req) {
 		getData(req).setAddEmptyOption(addEmptyOption);
 	}
 	
 	public void setAddEmptyOption(boolean addEmptyOption, String emptyOptLabel, OgemaHttpRequest req) {
 		getData(req).setAddEmptyOption(addEmptyOption, emptyOptLabel);
 	}
    
	@Override
	public void destroyWidget() {
		util.destroy();
		super.destroyWidget();
	}	
	
	@Override
	public void addWidget(OgemaWidget widget) {
		util.registerWidget(widget);
	}

	@Override
	public void removeWidget(OgemaWidget widget) {
		util.registerWidget(this);
	}
 	
    /**
     * Set new dropdown options with label = value.
     * @param values
     * @param req
     */
    // name conflict with method in TemplateDropdown
//    public void update(Collection<String> values, OgemaHttpRequest req) {
//    	getOptions(req).update(values);
//    }
}
