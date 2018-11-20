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
package de.iwes.widgets.pattern.widget.init;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.extended.pattern.PatternSelector;
import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.emptywidget.EmptyData;
import de.iwes.widgets.html.emptywidget.EmptyWidget;
import de.iwes.widgets.resource.widget.init.InitUtil;

/**
 * A widget that does not provide any Html, but can extract parameters from the page URL, and select a corresponding pattern
 * of the specified type. This can then be used to initialize other widgets on the page.
 *
 * @param <P>
 * @param <R>
 */
public class PatternInitSingleEmpty<P extends ResourcePattern<R>, R extends Resource> extends EmptyWidget implements InitWidget, PatternSelector<P> {

	private static final long serialVersionUID = 1L;
	private final ApplicationManager am;
	private P defaultSelected = null;
	private final Class<P> type;
	private final boolean allowIncompletePattern;
	
	public PatternInitSingleEmpty(WidgetPage<?> page, String id, boolean allowIncompletePattern, Class<P> type, ApplicationManager am) {
		this(page, id, false, allowIncompletePattern,type, am);
	}

	public PatternInitSingleEmpty(WidgetPage<?> page, String id, boolean globalWidget, boolean allowIncompletePattern, Class<P> type, ApplicationManager am) {
		super(page, id, globalWidget);
		Objects.requireNonNull(am);
		this.am=am;
		this.type = type;
		this.allowIncompletePattern = allowIncompletePattern;
	}
	
	public PatternInitSingleEmpty(OgemaWidget parent, String id, boolean allowIncompletePattern, OgemaHttpRequest req, Class<P> type, ApplicationManager am) {
		super(parent,id, req);
		Objects.requireNonNull(am);
		this.am =am;
		this.type = type;
		this.allowIncompletePattern = allowIncompletePattern;
	}
	
	public class PatternInitSingleEmptyOptions extends EmptyData {
		
		private volatile P selectedPattern;

		public PatternInitSingleEmptyOptions(PatternInitSingleEmpty<P,R> empty) {
			super(empty);
		}

		public P getSelectedItem() {
			return selectedPattern;
		}

		public void selectItem(P item) {
			this.selectedPattern = item;
		}
		
	}

	@Override
	public PatternInitSingleEmptyOptions createNewSession() {
		return new PatternInitSingleEmptyOptions(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PatternInitSingleEmptyOptions getData(OgemaHttpRequest req) {
		return (PatternInitSingleEmptyOptions) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(EmptyData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		PatternInitSingleEmptyOptions opt2= (PatternInitSingleEmptyOptions) opt;
		opt2.selectItem(defaultSelected);
	}
	
	@Override
	public P getSelectedItem(OgemaHttpRequest req) {
		return getData(req).getSelectedItem();
	}

	@Override
	public void selectItem(P item, OgemaHttpRequest req) {
		getData(req).selectItem(item);
	}

	@Override
	public void selectDefaultItem(P item) {
		this.defaultSelected = item;
	}

	// select the pattern
	@Override
	public void init(OgemaHttpRequest req) {
		String[] patterns = InitUtil.getInitParameters(getPage(), req);
		if (patterns == null || patterns.length == 0)
			return;
		final String selected = patterns[0];
		R aux;
		try {
			aux = am.getResourceAccess().getResource(selected); // may return null or throw an exception
		} catch (Exception e) { // if the type does not match
			am.getLogger().info("Empty pattern widget could not be initialized with the selected value {}",selected,e);
			aux = null;
		}
		if (aux == null || (!allowIncompletePattern && !aux.isActive()) || !aux.exists())
			return;
		final R res = aux;
		P pattern = AccessController.doPrivileged(new PrivilegedAction<P>() {

			@Override
			public P run() {
				try {
					P pattern = type.getConstructor(Resource.class).newInstance(res);
					if (!allowIncompletePattern) {
						boolean satisfied = am.getResourcePatternAccess().isSatisfied(pattern, type);
						if (!satisfied) {
							am.getLogger().info("Page has been initialized with an incomplete pattern - ignoring this: {}",pattern);
							return null;
						}
					}
					return pattern;
				} catch (Exception e) {
					am.getLogger().warn("Could not initalize page with pattern {} of type {}",selected,type.getName(),e);
					return null;
				}
			}
		});
		if (pattern == null)
			return;
		am.getLogger().debug("Initializing empty pattern widget with pattern {}",pattern);
		getData(req).selectItem(pattern);
	}
	
	
	
}
