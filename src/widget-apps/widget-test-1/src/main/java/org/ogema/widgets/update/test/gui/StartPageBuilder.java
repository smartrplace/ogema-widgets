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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.BoldText;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.form.label.Label;

public class StartPageBuilder implements Consumer<WidgetPage<LocaleDictionary>> {
	
	@Override
	public void accept(final WidgetPage<LocaleDictionary> page) {
		// TODO Auto-generated method stub
		/**
		 * Page header
		 */
		Header header = new Header(page, "header");
		header.setDefaultText("This is a widget test page");
		header.addDefaultStyle(HeaderData.CENTERED);
		page.append(header).linebreak();
		
		/**
		 * Alert: displays messages to the user. Initially this is hidden, but
		 * will be shown if you click the "Submit" button 
		 */
		final Alert alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);
		page.append(alert).linebreak();
		
		/**
		 * A table with a fixed number of rows. Since its cells contain widgets (set via table.setContent() below),
		 * hence their content may change.
		 */
		StaticTable table = new StaticTable(2, 2, new int[]{2,2});
		/**
		 * The label simply displays some text, completely static in this case.
		 */
		Label lab = new Label(page, "label");
		lab.setDefaultText("Press the button:");
		lab.setDefaultColor("blue");
		
		/**
		 * The button triggers the alert to be shown
		 */
		Button btn = new Button(page, "btn","Submit") {

			private static final long serialVersionUID = 1L;
			/**
			 * This variable is shared between sessions, therefore if 
			 * any user presses the button, then the counter is increased
			 * for all of them. This is usually not the intended behaviour. 
			 * See Button "tableRowBtn" below for how to manage session-specific data. 
			 */
			private AtomicInteger count = new AtomicInteger(0);
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				alert.showAlert("Button pressed " + count.incrementAndGet() + " times", true, req);
			}
			
		};
		// whenever the user presses the button, the alert shall be reloaded
		btn.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		table.setContent(0, 0, new BoldText("Static content")).setContent(0, 1, new BoldText("Static widget content"));
		table.setContent(1, 0, lab).setContent(1, 1, btn);
		page.append(table).linebreak();
		
		/**
		 * This table is not global, i.e. it does not show the same rows for all users. Hence, the
		 * widgets created within the rows must only exist for the specific session as well.
		 * To create a global table, we would pass another boolean parameter (globalWidget = true)
		 * to the table. In this case, widgets within the table could be global (shared between sessions),
		 * although they would not need to show the same content.
		 */
		final DynamicTable<String> dynamicTable = new DynamicTable<>(page, "dynamicTable");
		/**
		 * In the template class we specify how a row looks like.
		 */
		dynamicTable.setRowTemplate(new TestTableTemplate(dynamicTable));
		
		/**
		 * This button is special in that maintains some session-specific data (a row counter).
		 * (In this simple case, ThreadLocal would work as well, but this is not recommended)
		 */
		Button tableRowBtn = new Button(page, "tableRowBtn", "Add a row") {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				/**
				 * Add a row to the table.
				 * This method only affects the session from which the request
				 * is sent, like all widget methods that take an OgemaHttRequest 
				 * as last argument (given the widget in question is not global).
				 */
				dynamicTable.addItem(getData(req).count++ +"", req);
			}
						
			/**
			 * This Options class contains session-specific data. I.e., if the user
			 * reloads the page, a fresh instance is created, with a new counter starting at 0.
			 * We need to tell the Button to use this options class instead of the 
			 * default ButtonOptions, however, which results in the boilerplate code
			 * below (necessary to override #createNewOptions and #getOptions). 
			 * Otherwise we could only use global objects, which are shared between sessions.
			 */
			class TableButtonOptions extends ButtonData {
				
				private int count = 0;

				public TableButtonOptions(Button button) {
					super(button);
				}
				
			}
			
			/**
			 * see TableButtonOptions class for an explanation
			 */
			@Override
			public TableButtonOptions createNewSession() {
				return new TableButtonOptions(this);
			}
			
			/**
			 * see TableButtonOptions class for an explanation
			 */
			@Override
			public TableButtonOptions getData(OgemaHttpRequest req) {
				return (TableButtonOptions) super.getData(req);
			}
			
		};
		/**
		 *  whenever the user presses the button, the table shall be reloaded (because a row has been added)
		 */
		tableRowBtn.triggerAction(dynamicTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		page.append(tableRowBtn).linebreak();
		page.append(dynamicTable);
		
	}
	
}
