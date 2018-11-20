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
package org.ogema.widgets.update.test.gui;

import java.util.LinkedHashMap;
import java.util.Map;

import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.label.Label;

class TestTableTemplate extends RowTemplate<String> {
	
	private final DynamicTable<String> table;
 	
	TestTableTemplate(DynamicTable<String> table) {
		this.table = table;
	}

	/**
	 * Note: widgets in the table only exist for the specific session, since each user is supposed to 
	 * see a different table content. Therefore, the widgets in this method must use the constructor 
	 * passing an OgemaWidgetI as parent and an OgemaHttSession object as session identifier.
	 * The other type of constructors, taking a WidgetPageI as first argument, is reserved for 
	 * globally existing widgets (which need not show the same content in all sessions, however).
	 */
	@Override
	public Row addRow(String object, OgemaHttpRequest req) {
		Row row =new Row();
		final String id = getLineId(object);
		Label label = new Label(table, "label_row" + id, "This is row " + id,req);
		row.addCell("labelCell",label);
		Button btn = new Button(table, "addRowBtn_row" + id,"Don't hit me",req) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				table.removeRow(id, req);
			}
			
		};
		btn.triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		WidgetStyle<Button> btnStyle = (Math.random() > 0.5 ? ButtonData.BOOTSTRAP_BLUE : ButtonData.BOOTSTRAP_RED);
		btn.addDefaultStyle(btnStyle);
		row.addCell("btn",btn);
		return row;
	}

	@Override
	public String getLineId(String object) {
		return object;
	}

	@Override
	public Map<String, Object> getHeader() {
		Map<String,Object> map = new LinkedHashMap<>();
		map.put("labelCell", "Column 1");
		map.put("btn", "Column 2");
		return map;
	}

}
