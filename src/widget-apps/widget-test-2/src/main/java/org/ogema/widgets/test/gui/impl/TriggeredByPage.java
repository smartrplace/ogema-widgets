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

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE, 
				LazyWidgetPage.RELATIVE_URL + "=triggeredByPage.html",
				LazyWidgetPage.START_PAGE + "=false",
				LazyWidgetPage.MENU_ENTRY + "=Triggering page"
		}
)
public class TriggeredByPage implements LazyWidgetPage {
	
	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new TriggeredByPageInit(page);
	}
	
	private static class TriggeredByPageInit {
	
		private final WidgetPage<?> page;
		private final Header header;
		private final Alert info;
		private final Button btn1;
		private final Button btn2;
		private final Label response;
		
		@SuppressWarnings("serial")
		TriggeredByPageInit(final WidgetPage<?> page) {
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
}