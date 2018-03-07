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
package de.iwes.widgets.html.geomap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import de.iwes.widgets.api.extended.plus.TemplateWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class TemplateMap<T> extends GeoMap implements TemplateWidget<T> {

	private static final long serialVersionUID = 1L;
	private final MapTemplate<T> template; 
	// exactly one of the following two is non-null
//	private final OgemaWidget markerSnippet;
//	private final String markerSnippetHtml;
	private List<T> defaultItems;

	/**
	 * @param page
	 * @param id
	 * @param template
	 * @param markerSnippet
	 * 		typically a PageSnippet widget
	 */
	public TemplateMap(WidgetPage<?> page, String id, MapTemplate<T> template) {
		this(page, id, template, false);
	}
	
	protected TemplateMap(WidgetPage<?> page, String id, MapTemplate<T> template, boolean globalWidget) {
		super(page, id, globalWidget);
		this.template = template;
	}
	
	@Override
	public TemplateMapData<T> createNewSession() {
		return new TemplateMapData<T>(this, template);
//		if (markerSnippet != null)
//			return new TemplateMapData<>(this, template, markerSnippet);
//		else 
//			return new TemplateMapData<>(this, template, markerSnippetHtml);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TemplateMapData<T> getData(OgemaHttpRequest req) {
		return (TemplateMapData<T>) super.getData(req);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void setDefaultValues(GeoMapData opt) {
		super.setDefaultValues(opt);
		if (defaultItems != null)
			((TemplateMapData<T>) opt).update(defaultItems);
	}
	
	public void update(final Collection<? extends T> items, OgemaHttpRequest req) {
		getData(req).update(items);
	}
	
	public T getSelectedItem(OgemaHttpRequest req) {
		return getData(req).getSelectedItem();
	}
	
	public boolean addItem(T item, OgemaHttpRequest req) {
		return getData(req).addItem(item);
	}
	
	public boolean removeItem(T item, OgemaHttpRequest req) {
		return getData(req).removeItem(item);
	}
	
	public void setDefaultItems(Collection<T> items) {
		if (items == null) {
			this.defaultItems = null;
			return;
		}
		this.defaultItems = new ArrayList<>(items);
	}

	@Override
	public List<T> getItems(OgemaHttpRequest req) {
		return getData(req).getItems();
	}

}
