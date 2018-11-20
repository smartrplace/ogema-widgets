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

import java.util.concurrent.atomic.AtomicInteger;

import org.ogema.core.application.ApplicationManager;
import org.osgi.service.component.annotations.Component;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.popup.Popup;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE, 
				LazyWidgetPage.RELATIVE_URL + "=libloading.html",
				LazyWidgetPage.START_PAGE + "=false",
				LazyWidgetPage.MENU_ENTRY + "=Lib loading page"
		}
)
public class LazyLibLoadingPage implements LazyWidgetPage {
	
	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new LazyLibLoadingPageInit(page);
	}
	
	private static class LazyLibLoadingPageInit {

		LazyLibLoadingPageInit(final WidgetPage<?> page) {
			
			final Header header = new Header(page, "header", true);
			header.setDefaultText("Lazy JS loading test page");
			header.addDefaultStyle(HeaderData.CENTERED);
			header.setDefaultColor("blue");
			
			final PageSnippet snippet = new PageSnippet(page, "snippet", false) {
				
				private static final long serialVersionUID = 1L;
				private final AtomicInteger counter = new AtomicInteger(0);
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					if (!isVisible(req))
						return;
					clear(req);
					final Datepicker picker = new Datepicker(this, getId() + "_picker_" + counter.getAndIncrement(), req);
					picker.setDate(System.currentTimeMillis(), req);
					append(picker, req);
				}
				
			};
			snippet.setDefaultVisibility(false);
			final Popup popup = new Popup(page, "popup", true);
			popup.setBody(snippet, null);
			popup.setTitle("Select a date", null);
			
			final Button submit = new Button(page, "btn", "Show popup", true) {
				
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					snippet.setWidgetVisibility(true, req);
				}
				
				
			};
			
			page.append(header).linebreak();
			page.append(submit).linebreak();
			page.append(popup);
			
			submit.triggerAction(snippet, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			submit.triggerAction(popup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
			
		}
		
	}
}
