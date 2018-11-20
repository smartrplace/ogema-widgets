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

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.template.LabelledItem;

public class DropdownOption implements Serializable, Cloneable, LabelledItem {

    private static final long serialVersionUID = 7544565465456464l;
    private final String label;
    private final String labelEncoded;
    private final String value;
    final String valueEncoded;
    private boolean selected = false;
    
    DropdownOption(String value, boolean selected) {
    	this.label = null;
    	this.labelEncoded = null;
        this.value = Objects.requireNonNull(value);
        this.valueEncoded = WidgetData.escapeHtmlAttributeValue(value);
        this.selected = selected;
    }
    
    public DropdownOption(String value, String label, boolean selected) {
        this.label = Objects.requireNonNull(label);
        this.labelEncoded = StringEscapeUtils.escapeHtml4(label);
        this.value = Objects.requireNonNull(value);
        this.valueEncoded = WidgetData.escapeHtmlAttributeValue(value);
        this.selected = selected;
    }
    
    @Override
    public String id() {
    	return value;
    }
    
    @Override
    public String label(OgemaLocale locale) {
    	return label;
    }

    /**
     * Use {@link #label(OgemaLocale)} instead
     * @return
     */
    @Deprecated
    public String getLabel() {
        return label;
    }

    /**
     * @deprecated use {@link #id()} instead
     * @return
     */
    @Deprecated
    public String getValue() {
        return value;
    }

    public boolean isSelected() {
        return selected;
    }
    
    public void select(boolean newState) {
    	selected = newState;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.label);
        hash = 89 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
    	if (obj == this)
    		return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DropdownOption other = (DropdownOption) obj;
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
    	return getJSON(OgemaLocale.ENGLISH).toString();
    }

    public JSONObject getJSON(final OgemaLocale locale) {
        JSONObject result = new JSONObject();
        if (labelEncoded != null)
        	result.put("label", labelEncoded);
        result.put("value", valueEncoded);
        result.put("selected", selected);
        return result;
    }
    
    @Override
	public DropdownOption clone() throws CloneNotSupportedException {
    	return (DropdownOption) super.clone();
    }

}
