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
package de.iwes.widgets.html.accordion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import de.iwes.widgets.api.extended.plus.TemplateData;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.DisplayTemplate;
import de.iwes.widgets.template.LabelledItem;
import de.iwes.widgets.template.PageSnippetTemplate;

public class TemplateAccordionData<T> extends AccordionData implements TemplateData<T> {
	
	protected final List<T> objects = new ArrayList<>();

	public TemplateAccordionData(TemplateAccordion<T> accordion) {
		super(accordion);
	}
	
	public boolean addItem(T object) {
		if (object == null)
			return false;
		PageSnippetTemplate<T> template = getTemplate();
		OgemaHttpRequest req = getInitialRequest();
		final String id = template.getId(object);
//		String title = template.getLabel(object, (req != null ? req.getLocale() : OgemaLocale.ENGLISH));  // differentiate between title and id!
		if (hasItem(id))
			return false;
		PageSnippetI snippet = template.getSnippet(object, req);
//		Objects.requireNonNull(snippet);
		if (snippet == null)
			return false;
		writeLock();
		try {
			addWidget(new TemplateBasedLabelledItem<T>(object, template), snippet, false);
			objects.add(object);
		} finally {
			writeUnlock();
		}
		return true;
	}
	
	public boolean removeItem(T object) {
		if (object == null)
			return false;
		removeItem(getTemplate().getId(object)); // not fully synchronized because removeItem is problematic... we must not hold a lock while calling it // XXX
		writeLock();
		try {
			return objects.remove(object);
		} finally {
			writeUnlock();
		}
	}
	
	public List<T> getItems() {
		readLock();
		try {
			return new ArrayList<>(objects);
		} finally {
			readUnlock();
		}
	}
	
	
	public void update(Collection<? extends T> objects) {
		if (objects == null)
			objects = Collections.emptyList();
		Map<T,String> newIds = new LinkedHashMap<>();
		PageSnippetTemplate<T> template = getTemplate();
		for (T t:objects) {
//			String lab = template.getLabel(t, (getInitialRequest() != null ? getInitialRequest().getLocale() : OgemaLocale.ENGLISH)); // XXX
			final String lab = template.getId(t);
			newIds.put(t,lab);
		}
		List<String> toDelete = new ArrayList<>();
		writeLock();
		try {
			Set<String> oldItems = getAllItems();
			for (String old: oldItems) {
				if (!newIds.values().contains(old))
					toDelete.add(old);
			}
			for (Map.Entry<T, String> entry: newIds.entrySet()) {
				if (!oldItems.contains(entry.getValue())) 
					addItem(entry.getKey());
			}
		} finally {
			writeUnlock();
		}
		// we cannot synchronise on this, because removing subwidgets theoretically could lead to deadlock (tbc); remove has internal synchro
		for (String del: toDelete) { 
			removeItem(del);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	protected final PageSnippetTemplate<T> getTemplate() {
		return ((TemplateAccordion<T>) widget).template;
	}
	
	private static class TemplateBasedLabelledItem<T> implements LabelledItem {
		
		private final T object;
		private final DisplayTemplate<T> template;
		
		public TemplateBasedLabelledItem(T object, DisplayTemplate<T> template) {
			this.object = object;
			this.template = template;
		}

		@Override
		public String id() {
			return template.getId(object);
		}

		@Override
		public String label(OgemaLocale locale) {
			return template.getLabel(object, locale);
		}
		
		@Override
		public String description(OgemaLocale locale) {
			return template.getDescription(object, locale);
		}
		
	}
	
}
