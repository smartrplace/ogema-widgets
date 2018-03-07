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

package de.iwes.widgets.resource.widget.table;

import java.util.Collections;
import java.util.List;
import org.json.JSONObject;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.resource.ResourceWidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTableData;

public class ResourceTableData<R extends Resource> extends DynamicTableData<R> implements ResourceWidgetData<R> {

	protected final UpdateMode updateMode;
	protected final ApplicationManager am;
	protected final Class<R> type;
	protected volatile Class<? extends R> currentType;
	
	public ResourceTableData(ResourceTable<R> table, Class<R> type, UpdateMode updateMode, ApplicationManager am) {
		super(table);
		this.updateMode = updateMode;
		this.am = am;
		this.type = type;
		this.currentType = type;
		// TODO if updateMode = LISTENER register listener in constructor // change it every time setType is called!
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		if (updateMode == UpdateMode.AUTO_ON_GET) {
			writeLock(); 
			try {
				updateOnGet();
			} finally {
				writeUnlock();
			}
		}
		return super.retrieveGETData(req);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void updateOnGet() {
		List<? extends R> newResources;
		if (currentType != null)
			newResources = am.getResourceAccess().getResources(currentType);
		else
			newResources = Collections.emptyList();
		updateRows((List) newResources);
	}

	@Override
	public void setType(Class<? extends R> type) {
		writeLock();
		try {
			this.currentType = type;
			if (updateMode != UpdateMode.MANUAL) {
				updateOnGet();
			}
		} finally {
			writeUnlock();
		}
	}

	@Override
	public Class<? extends R> getType() {
		return currentType;
	}
	
	

}
