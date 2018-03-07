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

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.html5.Flexbox;

public class TriggeredByPage {
	
	private final WidgetPage<?> page;
	private final Header header;
	private final Alert info;
	private final Button btn1;
	private final Button btn2;
	private final Label response;
	
	@SuppressWarnings("serial")
	public TriggeredByPage(final WidgetPage<?> page) {
		this.page = page;
		this.header = new Header(page, "header", "TriggeredBy page");
		this.header.setDefaultColor("blue");
		final String description = "This page demonstrates the identification of the widget that triggered an action on some other widget "
				+ "(WidgetPage#getTriggeringWidget(OgemaHttpRequest).";
		this.info = new Alert(page, "info", description);
		info.setDefaultTextAsHtml(true);
		info.addDefaultStyle(AlertData.BOOTSTRAP_INFO);
		btn1 = new Button(page, "btn1", "Button 1");
		btn2 = new Button(page, "btn2", "Button 2");
		response = new Label(page, "response", "Push a button") {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				final OgemaWidget widget = getPage().getTriggeringWidget(req);
				if (widget == null) {
					setText("Please push a button.", req);
					return;
				}
				final int value = Integer.parseInt(widget.getId().substring(3));
				setText("Last pressed: button " + value + ".", req);
			}
			
		};
		buildPage();
		setDependencies();
	}
	
	private final void buildPage() {
		final Flexbox container = new Flexbox(page, "container", true);
		container.addItem(btn1, null).addItem(btn2, null);
		btn1.setDefaultMargin("0.5em");
		btn2.setDefaultMargin("0.5em");
		page.append(header).linebreak().append(info).linebreak().append(new StaticTable(2, 1)
			.setContent(0, 0, container).setContent(1, 0, response)
		);
	}
	
	private final void setDependencies() {
		btn1.triggerAction(response, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		btn2.triggerAction(response, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	

}
