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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class Checkbox2 extends OgemaWidgetBase<CheckboxData2> {

	private static final long serialVersionUID = 1L;
	private List<CheckboxEntry> defaultList;

	public Checkbox2(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	public Checkbox2(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}

	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return Checkbox2.class;
	}

	@Override
	public CheckboxData2 createNewSession() {
		return new CheckboxData2(this);
	}
	
	@Override
	protected void setDefaultValues(CheckboxData2 opt) {
		super.setDefaultValues(opt);
		if (defaultList != null)
			opt.setCheckboxList(defaultList);
	}
	
	
  /******* Public methods ******/
	
	public void setDefaultCheckboxList(Collection<CheckboxEntry> entries) {
		if (entries == null || entries.isEmpty())
			this.defaultList = null;
		else
			this.defaultList = new ArrayList<>(entries);
	}
	
	public void addDefaultEntry(final CheckboxEntry entry) {
		Objects.requireNonNull(entry);
		if (defaultList == null)
			defaultList = new ArrayList<>(1);
		defaultList.add(entry);
	}
    
	public List<String> getCheckboxIds(OgemaHttpRequest req) {
		return getData(req).getCheckboxIds();
    }
	
	/**
	 * Returns a map, with keys = entry ids.
	 * @param req
	 * @return
	 */
    public List<CheckboxEntry> getCheckboxList(OgemaHttpRequest req) {
        return getData(req).getCheckboxList();
    }
    
    public void setCheckboxList(Collection<CheckboxEntry> newList, OgemaHttpRequest req) {
    	getData(req).setCheckboxList(newList);
    }
    
    public void addEntry(final CheckboxEntry entry, OgemaHttpRequest req) {
    	getData(req).addEntry(entry);
    }
    
    public boolean removeEntry(final String id, OgemaHttpRequest req) {
    	return getData(req).removeEntry(id);
    }
    
    public void deselectAll(OgemaHttpRequest req) {
    	getData(req).deselectAll();
    }
    
    public void selectAll(OgemaHttpRequest req) {
    	getData(req).selectAll();
    }
    
    public boolean isChecked(final String id, OgemaHttpRequest req) {
    	return getData(req).isChecked(id);
    }
    
    public boolean checkSingleValue(final String id, OgemaHttpRequest req) {
    	return getData(req).checkSingleValue(id);
    }
   
    public boolean setState(final String id, final boolean checked, OgemaHttpRequest req) {
    	return getData(req).setState(id, checked);
    }

}
