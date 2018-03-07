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

package de.iwes.widgets.resource.widget.init;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;

import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.extended.resource.ResourceMultiSelector;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.emptywidget.EmptyData;
import de.iwes.widgets.html.emptywidget.EmptyWidget;
import de.iwes.widgets.html.form.button.TemplateRedirectButton;

/**
 * A widget that does not provide any Html, but can extract parameters from the page URL, and select corresponding resources
 * of the specified type. This can then be used to initialize other widgets on the page.
 *
 * @param <R>
 */
public class ResourceInitMultiEmpty<R extends Resource> extends EmptyWidget implements InitWidget, ResourceMultiSelector<R> {

	private static final long serialVersionUID = 1L;
	private final ApplicationManager am;
	private List<R> defaultSelected = null;
	private final boolean allowIncompletePattern;
	
	public ResourceInitMultiEmpty(WidgetPage<?> page, String id,boolean allowIncompletePattern, ApplicationManager am) {
		this(page, id, false, allowIncompletePattern, am);
	}

	public ResourceInitMultiEmpty(WidgetPage<?> page, String id, boolean globalWidget,boolean allowIncompletePattern, ApplicationManager am) {
		super(page, id, globalWidget);
		Objects.requireNonNull(am);
		this.am=am;
		this.allowIncompletePattern = allowIncompletePattern;
	}
	
	public ResourceInitMultiEmpty(OgemaWidget parent, String id, OgemaHttpRequest req,boolean allowIncompletePattern, ApplicationManager am) {
		super(parent,id, req);
		Objects.requireNonNull(am);
		this.am =am;
		this.allowIncompletePattern= allowIncompletePattern;
	}
	
	public class PatternInitMultiEmptyOptions extends EmptyData {
		
		private List<R> selectedResources;

		public PatternInitMultiEmptyOptions(ResourceInitMultiEmpty<R> empty) {
			super(empty);
		}

		public List<R> getSelectedItems() {
			readLock();
			try {
				return new ArrayList<>(selectedResources);
			} finally {
				readUnlock();
			}
		}

		public void selectItems(Collection<R> items) {
			if (items == null)
				items = Collections.emptyList();
			writeLock();
			try {
				this.selectedResources = new ArrayList<>(items);
			} finally {
				writeUnlock();
			}
		}
		
	}

	@Override
	public PatternInitMultiEmptyOptions createNewSession() {
		return new PatternInitMultiEmptyOptions(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PatternInitMultiEmptyOptions getData(OgemaHttpRequest req) {
		return (PatternInitMultiEmptyOptions) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(EmptyData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		PatternInitMultiEmptyOptions opt2= (PatternInitMultiEmptyOptions) opt;
		opt2.selectItems(defaultSelected);
	}
	
	@Override
	public void selectItems(Collection<R> items, OgemaHttpRequest req) {
		getData(req).selectItems(items);
	}
	
	@Override
	public void selectDefaultItems(Collection<R> items) {
		this.defaultSelected = new ArrayList<>(items);
	}
	
	@Override
	public List<R> getSelectedItems(OgemaHttpRequest req) {
		return getData(req).getSelectedItems();
	}

	// select the pattern
	@Override
	public void init(OgemaHttpRequest req) {
		String[] patterns = InitUtil.getInitParameters(getPage(), req);
		if (patterns == null || patterns.length == 0)
			return;
		final List<R> resources = new ArrayList<>();
		for (String pt: patterns) {
			R aux;
			try {
				aux = am.getResourceAccess().getResource(pt); // may return null or throw an exception
			} catch (Exception e) { // if the type does not match
				am.getLogger().info("Empty resource widget could not be initialized with the selected value {}",pt,e);
				aux = null;
			}
			if (aux == null || (!allowIncompletePattern && !aux.isActive()) || !aux.exists())
				continue;
			resources.add(aux);
		}
		am.getLogger().debug("Initializing empty pattern widget with patterns {}",resources);
		getData(req).selectItems(resources);
	}
	
	
	
}
