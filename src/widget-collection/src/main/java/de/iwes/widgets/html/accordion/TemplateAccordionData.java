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
		Objects.requireNonNull(snippet);
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
