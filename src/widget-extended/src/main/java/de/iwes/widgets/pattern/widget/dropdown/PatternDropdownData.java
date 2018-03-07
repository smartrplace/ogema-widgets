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

package de.iwes.widgets.pattern.widget.dropdown;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.PatternWidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.TemplateDropdownData;

public class PatternDropdownData<P extends ResourcePattern<?>> extends TemplateDropdownData<P> implements PatternWidgetData<P> {

	protected volatile P initialSelected = null;  
	protected volatile boolean firstRequest = true;
	protected final ResourcePatternAccess rpa;
	protected volatile Class<? extends P> type = null;
	
	public PatternDropdownData(PatternDropdown<P> patternDropdown, ResourcePatternAccess rpa) {
		super(patternDropdown);
		this.rpa = rpa;
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		if (getUpdateMode() == UpdateMode.AUTO_ON_GET) {
			writeLock();
			try {
				updateOnGet();
			} finally {
				writeUnlock();
			}
		}
		if (firstRequest) {
			writeLock();
			try {
				firstRequest = false;
				Map<String,String[]> params = widget.getPage().getPageParameters(req);
				if (params != null && params.containsKey("selectedPattern")) {
					String[] selected = params.get("selectedPattern");
					if (selected.length > 0) {
						selectSingleOption(selected[0]);
						initialSelected = null; // override
					}
				}
				else {
					if (initialSelected != null)
						selectItem(initialSelected);
				}
			} finally {
				writeUnlock();
			}
		}
		return super.retrieveGETData(req);
	}
	
	protected void updateOnGet(){
		List<? extends P> patterns;
		if (type == null)
			patterns = Collections.emptyList();
		else
			patterns = rpa.getPatterns(type, AccessPriority.PRIO_LOWEST);
		update(patterns);
	}
	
	
	@SuppressWarnings("unchecked")
	public UpdateMode getUpdateMode() {
		return ((PatternDropdown<P>) widget).updateMopde;
	}

	@Override
	public void setType(Class<? extends P> type) {
		writeLock();
		try {
			this.type = type;
			if (getUpdateMode() != UpdateMode.MANUAL)
				updateOnGet();
		} finally {
			writeUnlock();
		}
	}

	@Override
	public Class<? extends P> getType() {
		return type;
	}

}
