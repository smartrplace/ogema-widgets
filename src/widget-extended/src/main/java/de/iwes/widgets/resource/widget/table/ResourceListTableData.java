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
