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

package de.iwes.widgets.html.autocomplete;

import java.util.ArrayList;
import java.util.List;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.textfield.TextFieldData;

/**
 * An input text field with autocomplete functionality. It does not allow
 * to select a value different from the proposed ones.
 * 
 * A derived widget {@see ResourcePathAutocomplete} exists as well,
 * which provides autocomplete functionality for resource paths.
 */
public class Autocomplete extends OgemaWidgetBase<AutocompleteData> {

	private static final long serialVersionUID = 1L;
//    private String defaultValue = null;
    private String defaultPlaceholder = "Enter text";
    private int defaultMinLength = 1;
    private List<String> defaultAutocompleteOptions = null;
	
	/*********** Constructor **********/
    
    public Autocomplete(WidgetPage<?> page, String id) {
        super(page, id);
        addDefaultStyle(TextFieldData.FORM_CONTROL); // this requires TextField and Autocomplete to use the same identifier on their input element
    }
    
    public Autocomplete(OgemaWidget parent, String id, OgemaHttpRequest req) {
    	super(parent, id, req);
    	addDefaultStyle(TextFieldData.FORM_CONTROL);
    }
    
    /******* Inherited methods *****/

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Autocomplete.class;
    }

	@Override
	public AutocompleteData createNewSession() {
		return new AutocompleteData(this);
	}
	
	@Override
	protected void setDefaultValues(AutocompleteData opt) {
//		opt.setValue(defaultValue);
		opt.setPlaceholder(defaultPlaceholder);
		opt.setMinLength(defaultMinLength);
		if (defaultAutocompleteOptions != null)
			opt.setOptions(defaultAutocompleteOptions);
		super.setDefaultValues(opt);
	}
	
    @Override
    protected void registerJsDependencies() {
    	super.registerLibrary(true, "jQuery.fn.typeahead", "/ogema/widget/autocomplete/lib/typeahead.bundle-0.11.1.min.js");
    	super.registerJsDependencies();
    }

    /******* Public methods ********/

//	public String getDefaultValue() {
//		return defaultValue;
//	}

//	public void setDefaultValue(String defaultValue) {
//		this.defaultValue = defaultValue;
//	}
	
	public void setDefaultPlaceholder(String placeholder) {
		this.defaultPlaceholder = placeholder;
	}
	

    public void setPlaceholder(String placeholder,OgemaHttpRequest req) {
    	getData(req).setPlaceholder(placeholder);
    }

//	public void setValue(String value,OgemaHttpRequest req) {
//		getOptions(req).setValue(value);
//	}
	
    /**
     * Returns the value currently selected by the user, or null,
     * if no value has been selected.
     * @param req
     * @return
     */
	public String getValue(OgemaHttpRequest req) {
		return getData(req).getValue();
	}
	
	public void setDefaultAutocompleteOptions(List<String> options) {
		this.defaultAutocompleteOptions = new ArrayList<String>(options);
	}
	
	public void setAutocompleteOptions(List<String> options,OgemaHttpRequest req) {
		getData(req).setOptions(options);
	}

	public void addAutocompleteOption(String option,OgemaHttpRequest req) {
		getData(req).addOption(option);
	}
	
	public void removeAutocompleteOption(String option,OgemaHttpRequest req) {
		getData(req).removeOption(option);
	}
	
	public List<String> getAutocompleteOptions(OgemaHttpRequest req) {
		return getData(req).getOptions();
	}
	
	/**
	 * Set the minimum number of letters the user needs to type, before
	 * any hints are shown. May be set to 0, in which case hints are 
	 * always shown.
	 * Default: 1.
	 * @param minLength
	 * 		Non-negative integer
	 * @param req
	 */
	public void setDefaultMinLength(int minLength) {
		this.defaultMinLength = minLength;
	}
	
	/**
	 * @see #setMinLength(int, OgemaHttpRequest)
	 * @param req
	 * @return
	 */
	public int getMinLength(OgemaHttpRequest req) {
		return getData(req).getMinLength();
	}

	/**
	 * Set the minimum number of letters the user needs to type, before
	 * any hints are shown. May be set to 0, in which case hints are 
	 * always shown.
	 * Default: 1.
	 * @param minLength
	 * 		Non-negative integer
	 * @param req
	 */
	public void setMinLength(int minLength, OgemaHttpRequest req) {
		getData(req).setMinLength(minLength);
	}
	
	public void clear(OgemaHttpRequest req) {
		getData(req).clear();
	}
	
}
