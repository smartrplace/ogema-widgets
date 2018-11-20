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
	
    @Override
    protected void registerJsDependencies() {
    	registerLibrary(true, "Checkbox2", "/ogema/widget/checkbox/Checkbox2.js");
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
