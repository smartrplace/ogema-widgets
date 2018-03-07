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
package org.ogema.widgets.trigger.level.test.gui;

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

public class IconWidthPage {

	public IconWidthPage(final WidgetPage<?> page) {
		
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
