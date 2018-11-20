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
package de.iwes.widgets.resource.widget.multiselect;

import java.util.Collections;
import java.util.List;
import org.json.JSONObject;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.resource.ResourceWidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.multiselect.TemplateMultiselectData;

public class ResourceMultiselectData<R extends Resource> extends TemplateMultiselectData<R> implements ResourceWidgetData<R> {
	
	private final ResourceAccess ra;
	// Map<Path, Resource>
//	private final Map<String, R> selected = new HashMap<String, R>();
	// either resourceType or resources can / must be != null
	private volatile Class<? extends R> resourceType = null;

	public ResourceMultiselectData(ResourceMultiselect<R> multiselect, ResourceAccess ra) {
		super(multiselect);
		this.ra = ra;
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		if (getUpdateMode() == UpdateMode.AUTO_ON_GET)
			setResourceOptions();
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
	
	public void setType(Class<? extends R> type) {
		this.resourceType = type;
	}
	
	public Class<? extends R> getType() {
		return this.resourceType;
	}
	
	private void setResourceOptions() {
		List<? extends R> resources;
		if (resourceType == null) {
			resources = Collections.emptyList();
		} else {
			resources = ra.getResources(resourceType);
		}
		super.update(resources);
	}
	
	@SuppressWarnings("unchecked")
	private UpdateMode getUpdateMode() {
		return ((ResourceMultiselect<R>) widget).updateMode;
	}


}
