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
package de.iwes.widgets.reveal.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import de.iwes.widgets.api.extended.html.fragment.FragmentWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.html.HtmlStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

class ColumnSlides extends FragmentWidget {

	private static final long serialVersionUID = 1L;
	private final ColumnTemplate template;
	private final Collection<OgemaWidget> triggers;
	private final Collection<OgemaWidget> triggered;

	protected ColumnSlides(WidgetPage<?> page, String id, ColumnTemplate template) {
		this(page, id, false, template);
	}
	
	protected ColumnSlides(WidgetPage<?> page, String id, boolean globalWidget, ColumnTemplate template) {
		this(page, id, globalWidget, template, null, null);
	}
	
	protected ColumnSlides(OgemaWidget parent, String id, OgemaHttpRequest req, ColumnTemplate template) {
		this(parent, id, req, template, null, null);
	}
	
	protected ColumnSlides(WidgetPage<?> page, String id, boolean globalWidget, ColumnTemplate template, 
			Collection<OgemaWidget> triggers, final Collection<OgemaWidget> triggered) {
		super(page, id, globalWidget);
		this.template = Objects.requireNonNull(template);
		this.triggers = triggers == null || triggers.isEmpty() ? null : new ArrayList<>(triggers);
		this.triggered = triggered == null || triggered.isEmpty() ? null : new ArrayList<>(triggered);
	}
	
	protected ColumnSlides(OgemaWidget parent, String id, OgemaHttpRequest req, ColumnTemplate template, 
			Collection<OgemaWidget> triggers, final Collection<OgemaWidget> triggered) {
		super(parent, id, req);
		this.template = Objects.requireNonNull(template);
		this.triggers = triggers == null || triggers.isEmpty() ? null : new ArrayList<>(triggers);
		this.triggered = triggered == null || triggered.isEmpty() ? null : new ArrayList<>(triggered);
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		super.updateItems(template.update(this, req, triggers, triggered).entrySet().stream()
			.map(snippet -> {
				final Section s = new Section();
				s.addSubItem(snippet.getValue());
				final String id = snippet.getKey();
				s.addStyle(new HtmlStyle("id", id));
				return s;
			})
			.collect(Collectors.toList()), req);
	}
	
	@Override
	public void updateItems(Collection<Object> items, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addItem(Object item, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeItem(Object item, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
	private static class Section extends HtmlItem {

		Section() {
			super("section");
		}
		
	}
	
}
