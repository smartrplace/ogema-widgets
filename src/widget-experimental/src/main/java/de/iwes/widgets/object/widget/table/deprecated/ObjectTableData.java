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
package de.iwes.widgets.object.widget.table.deprecated;

import java.util.List;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTableData;

public class ObjectTableData<R> extends DynamicTableData<R> {

	protected List<? extends R> objectList;
	
	public ObjectTableData(ObjectTable<R> table, List<R> objectList) {
		super(table);
		this.objectList = objectList;
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		return super.retrieveGETData(req);
	}
	
	protected void updateOnGet() {
		updateRows(objectList);
	}
	
	public void setList(List<? extends R> objectList) {
		writeLock();
		try {
			this.objectList = objectList;
		} finally {
			writeUnlock();
		}
	}

	public List<? extends R> getList() {
		return objectList;
	}
}
