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
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;

import de.iwes.widgets.api.extended.mode.UpdateMode;

public class ResourceListTableData<R extends Resource> extends ResourceTableData<R> {

	protected ResourceList<R> list;
	
	public ResourceListTableData(ResourceListTable<R> table,  UpdateMode updateMode) {
		super(table, null, updateMode, null);
		// TODO if updateMode = LISTENER register listener in constructor
	}
	
	@Override
	protected void updateOnGet() {
		if (updateMode == UpdateMode.AUTO_ON_GET) {
			List<R> newResources;
			if (list == null)
				newResources = Collections.emptyList();
			else
				newResources = list.getAllElements();
			updateRows(newResources);
		}
	}

	public ResourceList<R> getList() {
		return list;
	}

	public void setList(ResourceList<R> list) {
		writeLock();
		try {
			this.list = list;
			if (updateMode != UpdateMode.MANUAL) {
				updateOnGet();
			}
		} finally {
			writeUnlock();
		}
	}

	@Override
	public Class<? extends R> getType() {
		if (list == null)
			return null;
		else
			return list.getElementType();
	}
	
	@Override
	public void setType(Class<? extends R> type) {
		throw new UnsupportedOperationException("not supported by ResourceListTable");
	}

}
