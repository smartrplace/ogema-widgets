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

package de.iwes.widgets.pattern.widget.multiselect;

import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.PatternWidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.multiselect.TemplateMultiselectData;

public class PatternMultiselectData<P extends ResourcePattern<?>> extends TemplateMultiselectData<P> implements PatternWidgetData<P>{
	
	protected final ResourcePatternAccess rpa;
	// Map<Path, Resource>
//	private final Map<String, R> selected = new HashMap<String, R>();
	// either resourceType or resources can / must be != null
	protected volatile Class<? extends P> patternType = null;

	public PatternMultiselectData(PatternMultiselect<P> multiselect, ResourcePatternAccess rpa) {
		super(multiselect);
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
		return super.retrieveGETData(req);
	}
	
	// in parent
//	@Override
//	public String onPOST(String json, OgemaHttpRequest req) {
//		String result = super.onPOST(json, req);
//		Collection<String> selected = getSelectedValues();
//		Iterator<String> it = this.selected.keySet().iterator();
//		while (it.hasNext()) {
//			String oldSel = it.next();
//			if (!selected.contains(oldSel))
//				it.remove();
//		}
//		for (String newSel: selected) {
//			if (!this.selected.containsKey(newSel)) {
//				R res = ra.getResource(newSel);
//				if (res != null) {
//					this.selected.put(newSel, res);
//				}
//				else {
//					LoggerFactory.getLogger(getClass()).error("Selected value does not correspond to resource"); // may happen in case of race conditions?
//				}
//			}
//		}
//		
//		return result;
//	}
	
	public void setType(Class<? extends P> type) {
		this.patternType = type;
	}
	
	public Class<? extends P> getType() {
		return this.patternType;
	}
	
	protected void updateOnGet() {
		List<? extends P> resources;
		if (patternType == null) {
			resources = Collections.emptyList();
		} else {
			resources = rpa.getPatterns(patternType, AccessPriority.PRIO_LOWEST);
		}
		update(resources);
	}
	
	@SuppressWarnings("unchecked")
	protected UpdateMode getUpdateMode() {
		return ((PatternMultiselect<P>) widget).updateMode;
	}


}
