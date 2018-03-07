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

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import de.iwes.widgets.api.extended.plus.TemplateWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.PageSnippetTemplate;

/**
 * A special {@link Accordion}, whose tabs are modeled on some class T. 
 * A {@link PageSnippetTemplate template} is used to specify what the tabs look like.
 *  
 * @param <T>
 */
public class TemplateAccordion<T> extends Accordion implements TemplateWidget<T> {

	private static final long serialVersionUID = 1L;
	protected final PageSnippetTemplate<T> template;

	public TemplateAccordion(WidgetPage<?> page, String id, PageSnippetTemplate<T> template) {
		super(page, id);
		Objects.requireNonNull(template);
		this.template = template;
	}
	
	public TemplateAccordion(WidgetPage<?> page, String id, boolean globalWidget, PageSnippetTemplate<T> template) {
		super(page, id, globalWidget);
		Objects.requireNonNull(template);
		this.template = template;
	}
	
	public TemplateAccordion(OgemaWidget parent, String id, OgemaHttpRequest req, PageSnippetTemplate<T> template) {
		super(parent, id, req);
		Objects.requireNonNull(template);
		this.template = template;
	}

	@Override
	public TemplateAccordionData<T> createNewSession() {
		return new TemplateAccordionData<>(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TemplateAccordionData<T> getData(OgemaHttpRequest req) {
		return (TemplateAccordionData<T>) super.getData(req);
	}
	
	public boolean addItem(T object, OgemaHttpRequest req) {
		return getData(req).addItem(object);
	}
	
	public boolean removeItem(T object, OgemaHttpRequest req) {
		return getData(req).removeItem(object);
	}
	
	public List<T> getItems(OgemaHttpRequest req) {
		return getData(req).getItems();
	}
	
	public void update(Collection<? extends T> objects, OgemaHttpRequest req) {
		getData(req).update(objects);
	}
	
	/**
	 * Not supported by TemplateAccordion, use {@link #addItem(Object, OgemaHttpRequest)} instead
	 */
	@Override
	public void addItem(String title, OgemaWidget widget, boolean expanded, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not supported by TemplateAccordion, use {@link #addItem(Object, OgemaHttpRequest)} instead
	 */
	@Override
	public void addItem(String title, OgemaWidget widget, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not supported by TemplateAccordion, use {@link #addItem(Object, OgemaHttpRequest)} instead
	 */
	@Override
	public void addItem(String title, String html, boolean expanded, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not supported by TemplateAccordion, use {@link #addItem(Object, OgemaHttpRequest)} instead
	 */
	@Override
	public void addItem(String title, String html, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not supported by TemplateAccordion, use {@link #addItem(Object, OgemaHttpRequest)} instead
	 */
	@Override
	public void addPage(String title, String path, boolean expanded, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not supported by TemplateAccordion, use {@link #addItem(Object, OgemaHttpRequest)} instead
	 */
	@Override
	public void addPage(String title, String path, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
}
