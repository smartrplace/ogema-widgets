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
