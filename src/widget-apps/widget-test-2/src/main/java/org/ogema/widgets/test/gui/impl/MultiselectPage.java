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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.osgi.service.component.annotations.Component;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.checkbox.Checkbox;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.template.DisplayTemplate;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE, 
				LazyWidgetPage.RELATIVE_URL + "=multiselect.html",
				LazyWidgetPage.START_PAGE + "=false",
				LazyWidgetPage.MENU_ENTRY + "=Multiselect page"
		}
)
public class MultiselectPage implements LazyWidgetPage {
	
	private static class TemplateClass {
		
		final String id;
		final String label;
		
		TemplateClass(String id, String label) {
			this.id = id;
			this.label = label; 
		}
		
	}
	
	private static TemplateClass london = new TemplateClass("ld", "London");
	private static TemplateClass newYork = new TemplateClass("ny", "New York");
	private static TemplateClass beijing = new TemplateClass("bj", "Beijing");
	private static TemplateClass kassel = new TemplateClass("ks", "Kassel");
	
	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new MultiselectPageInit(page);
	}
	
	private static class MultiselectPageInit {
	
		private final WidgetPage<?> page;
		private final Header header;
		private final Checkbox disabledBox;
		private final TemplateMultiselect<TemplateClass> selector;
		private final Label selectedLabel;
		
		@SuppressWarnings("serial")
		MultiselectPageInit(WidgetPage<?> page) {
			this.page = page;
			this.header = new Header(page, "header", "Multiselect test page");
			header.setDefaultColor("blue");
			header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
			this.disabledBox = new Checkbox(page, "disabledBox");
			disabledBox.setDefaultList(Collections.singletonMap("", false));
			this.selector = new TemplateMultiselect<TemplateClass>(page, "selector") {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final boolean disabled = disabledBox.getCheckboxList(req).get("");
					if (disabled)
						this.disable(req);
					else
						this.enable(req);
				}
				
			};
			selector.selectDefaultItems(Arrays.asList(london, newYork, beijing, kassel));
			selector.setTemplate(new DisplayTemplate<MultiselectPage.TemplateClass>() {
				
				@Override
				public String getLabel(TemplateClass object, OgemaLocale locale) {
					return object.label;
				}
				
				@Override
				public String getId(TemplateClass object) {
					return object.id;
				}
			});
			this.selectedLabel = new Label(page, "selectedLabel") {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final List<TemplateClass> selectedItems = selector.getSelectedItems(req);
					final StringBuilder builder = new StringBuilder();
					boolean first = true;
					for (TemplateClass item: selectedItems) {
						if (!first)
							builder.append(", ");
						first = false;
						builder.append(item.label);
					}
					setText(builder.toString(), req);
				}
			};
			
			
			
			buildPage();
			setDependencies();
		}
		
	
		private final void buildPage() {
			int row = 0;
			page.append(header).linebreak().append(new StaticTable(3, 2, new int[] {3,3})
				.setContent(row, 0, "Disable selection?").setContent(row++, 1, disabledBox)
				.setContent(row, 0, "Select items").setContent(row++, 1, selector)
				.setContent(row, 0, "Selected items:").setContent(row++, 1, selectedLabel)
			);
		}
		
		private final void setDependencies() {
			disabledBox.triggerAction(selector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			selector.triggerAction(selectedLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
		
	}
}