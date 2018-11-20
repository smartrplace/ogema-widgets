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
package org.ogema.widgets.test.gui.impl;

import org.ogema.core.application.ApplicationManager;
import org.osgi.service.component.annotations.Component;

import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.icon.Icon;
import de.iwes.widgets.html.icon.IconType;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE, 
				LazyWidgetPage.RELATIVE_URL + "=iconwidth.html",
				LazyWidgetPage.START_PAGE + "=false",
				LazyWidgetPage.MENU_ENTRY + "=Icon width page"
		}
)
public class IconWidthPage implements LazyWidgetPage {
	
	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new IconWidthPageInit(page);
	}
	
	private static class IconWidthPageInit {

		IconWidthPageInit(final WidgetPage<?> page) {
			
			final Header header = new Header(page, "header", true);
			header.setDefaultText("Icon width test page");
			header.addDefaultStyle(HeaderData.CENTERED);
			header.setDefaultColor("blue");
			
			final Alert alert = new Alert(page, "alert", "");
			alert.setDefaultVisibility(false);
			
			final Icon icon = new Icon(page, "icon");
			icon.setDefaultIconType(IconType.HELP_CONTENT);
			
			final TextField iconWidthField = new TextField(page, "iconWidthField");
			iconWidthField.setDefaultPlaceholder("Enter icon width CSS, such as \"75px\"");
			
			final Button submit = new Button(page, "submit", "Set icon width") {
	
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					final String width = iconWidthField.getValue(req).trim();
					if (width.isEmpty()) {
						alert.showAlert("Please enter a width string", false, req);
						return;
					}
					icon.setWidth(width, req);
					alert.showAlert("Width set to " + width, true, req);
				}
				
			};
			submit.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			submit.triggerAction(icon, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			
			final StaticTable tab = new StaticTable(2, 2, new  int[]{3,3})
					.setContent(0,0, iconWidthField).setContent(0, 1, icon)
					.setContent(1, 0, submit);
			page.append(header).linebreak().append(alert).append(tab);
		}
		
	}
}