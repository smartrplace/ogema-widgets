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

import java.util.concurrent.atomic.AtomicInteger;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.popup.Popup;

public class LazyLibLoadingPage {

	public LazyLibLoadingPage(final WidgetPage<?> page) {
		
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
