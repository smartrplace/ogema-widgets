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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.iwes.widgets.api.extended.WidgetData;
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

public class MultiselectPage {
	
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
	
	private final WidgetPage<?> page;
	private final Header header;
	private final Checkbox disabledBox;
	private final TemplateMultiselect<TemplateClass> selector;
	private final Label selectedLabel;
	
	@SuppressWarnings("serial")
	public MultiselectPage(WidgetPage<?> page) {
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
