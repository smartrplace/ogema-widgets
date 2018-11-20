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
package de.iwes.widgets.html.geomap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.iwes.widgets.api.extended.plus.TemplateData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class TemplateMapData<T> extends GeoMapData implements TemplateData<T> {
	
	private final Map<String, T> objects = new HashMap<>();
	private final MapTemplate<T> template;
	// exactly one of the following two is non-null
//	private final OgemaWidget markerSnippet;
//	private final String markerSnippetHtml;
	
	protected TemplateMapData(TemplateMap<T> widget, MapTemplate<T> template) {
		super(widget, true);
		this.template = template;
	}
	
//	protected TemplateMapData(TemplateMap<T> widget, MapTemplate<T> template, OgemaWidget markerSnippet) {
//		this(Objects.requireNonNull(widget), Objects.requireNonNull(template), Objects.requireNonNull(markerSnippet), null);
//	}
//	
//	protected TemplateMapData(TemplateMap<T> widget, MapTemplate<T> template, String markerSnippetHtml) {
//		this(Objects.requireNonNull(widget), Objects.requireNonNull(template), null, Objects.requireNonNull(markerSnippetHtml));
//	}
//	
//	private TemplateMapData(TemplateMap<T> widget, MapTemplate<T> template, OgemaWidget markerSnippet, String markerSnippetHtml) {
//		super(widget, true);
//		this.template = template;
////		this.markerSnippet = markerSnippet;
////		this.markerSnippetHtml = markerSnippetHtml;
//	}

	protected void update(final Collection<? extends T> items) {
		Objects.requireNonNull(items);
		writeLock();
		try {
			final Iterator<String> idIt = objects.keySet().iterator();
			while (idIt.hasNext()) {
				String id = idIt.next();
				final Iterator<? extends T> it = items.iterator();
				boolean found = false;
				while (it.hasNext()) {
					if (id.equals(template.getId(it.next()))) {
						found = true;
						break;
					}
				}
				if (!found) {
					idIt.remove();
					markers.remove(id);
				}
			}
			for (T item: items) {
				final String id = template.getId(item);
				if (!objects.containsKey(id)) {
					objects.put(id, item);
					markers.put(id, new TemplateMarker<T>(template, item, this)); 
				}
			}
		} finally {
			writeUnlock();
		}
		
	}
	
	public boolean addItem(final T item) {
		Objects.requireNonNull(item);
		final String id = template.getId(item);
		writeLock();
		try {
			if (objects.containsKey(id))
				return false;
			objects.put(id, item);
			markers.put(id, new TemplateMarker<T>(template, item, this));
		} finally {
			writeUnlock();
		}
		return true;
	}
	
	public boolean removeItem(final T item) {
		Objects.requireNonNull(item);
		final String id = template.getId(item);
		writeLock();
		try {
			objects.remove(id);
			return markers.remove(id)!= null;
		} finally {
			writeUnlock();
		}
	}

	public T getSelectedItem() {
		readLock();
		try {
			if (selectedMarker == null)
				return null;
			return objects.get(selectedMarker);
		} finally {
			readUnlock();
		}
	}
	
	
	protected static class TemplateMarker<T> extends Marker {
		
		public TemplateMarker(final MapTemplate<T> template, final T instance, TemplateMapData<T> data) {
			super(template.getId(instance), new double[]{ template.getLatitude(instance), template.getLongitude(instance) });
//			setInfoWindowHtml(data.markerSnippet != null ? (((OgemaWidgetBase<?>) data.markerSnippet).getTag()) : data.markerSnippetHtml);
			setTitle(template.getLabel(instance, OgemaLocale.ENGLISH)); // locale?
			setIcon(template.getIconUrl(instance), template.getIconSize(instance));
		}
		
	}


	@Override
	public List<T> getItems() {
		readLock();
		try {
			return new ArrayList<>(objects.values());
		} finally {
			readUnlock();
		}
	}
	
}
