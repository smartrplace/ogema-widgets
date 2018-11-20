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
package de.iwes.widgets.reswidget.scheduleviewer;

import java.util.Collection;
import java.util.Collections;

import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.flexbox.AlignItems;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;
import de.iwes.widgets.pattern.widget.dropdown.PatternDropdown;
import de.iwes.widgets.reswidget.scheduleviewer.api.ConditionalTimeSeriesFilter;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilter;
import de.iwes.widgets.template.DisplayTemplate;

public class ConditionalProgramSelector extends Flexbox {

	private static final long serialVersionUID = 1L;
	private final ResourcePatternAccess rpa;
//	private final Collection<ConditionalTimeSeriesFilter<?>> filters;
	final TemplateDropdown<ConditionalTimeSeriesFilter<?>> filterSelector;
	private final Label instanceSelectorLabel;
	final PatternDropdown<?> instanceSelector;
	
	public ConditionalProgramSelector(WidgetPage<?> page, String id, Collection<ConditionalTimeSeriesFilter<?>> filters, ResourcePatternAccess rpa) {
		super(page, id, true);
		this.setDefaultJustifyContent(JustifyContent.SPACE_BETWEEN);
		this.setDefaultAlignItems(AlignItems.CENTER);
		this.rpa = rpa;
//		this.filters = filters;
		this.filterSelector = new TemplateDropdown<ConditionalTimeSeriesFilter<?>>(page, id + "_filterSelector");
		filterSelector.setDefaultItems(filters);
		filterSelector.setDefaultAddEmptyOption(true);
		filterSelector.setTemplate(new DisplayTemplate<ConditionalTimeSeriesFilter<?>>() {
			
			@Override
			public String getLabel(ConditionalTimeSeriesFilter<?> object, OgemaLocale locale) {
				return object.label(locale);
			}
			
			@Override
			public String getId(ConditionalTimeSeriesFilter<?> object) {
				return object.id();
			}
		});
		this.instanceSelectorLabel = new Label(page, id + "_instanceSelectorLabel","Select instance") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				TimeSeriesFilter filter = filterSelector.getSelectedItem(req);
				if (filter == null) {
					setWidgetVisibility(false, req);
					setText("", req);
				}
				else {
					setWidgetVisibility(true, req);
					setText("Select instance", req);
				}
			}
		};
		this.instanceSelector = new PatternDropdown<ResourcePattern<?>>(page, id+ "_patternDropdown") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				ConditionalTimeSeriesFilter<?> filter = filterSelector.getSelectedItem(req);
				if (filter == null) {
					setWidgetVisibility(false, req);
					update(Collections.<ResourcePattern<?>> emptyList(), req);
				}
				else {
					setWidgetVisibility(true, req);
					Class<? extends ResourcePattern<?>> clazz = (Class<? extends ResourcePattern<?>>) filter.getPatternClass();
					update(ConditionalProgramSelector.this.rpa.getPatterns(clazz, AccessPriority.PRIO_LOWEST), req);
				}
			}
			
		};
		instanceSelector.setDefaultAddEmptyOption(true);
		buildWidget();
		setDependencies();
	}

	private final void buildWidget() {
		this.addItem(filterSelector, null);
		this.addItem(instanceSelectorLabel, null);
		this.addItem(instanceSelector, null);
	}
	
	private final void setDependencies() {
		filterSelector.triggerAction(instanceSelectorLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		filterSelector.triggerAction(instanceSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	
}
