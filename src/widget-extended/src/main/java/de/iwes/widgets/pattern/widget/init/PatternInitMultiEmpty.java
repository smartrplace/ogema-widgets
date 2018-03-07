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

package de.iwes.widgets.pattern.widget.init;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.extended.pattern.PatternMultiSelector;
import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.emptywidget.EmptyData;
import de.iwes.widgets.html.emptywidget.EmptyWidget;
import de.iwes.widgets.resource.widget.init.InitUtil;

/**
 * A widget that does not provide any Html, but can extract parameters from the page URL, and select corresponding patterns
 * of the specified type. This can then be used to initialize other widgets on the page.
 *
 * @param <P>
 * @param <R>
 */
public class PatternInitMultiEmpty<P extends ResourcePattern<R>, R extends Resource> extends EmptyWidget implements InitWidget, PatternMultiSelector<P> {

	private static final long serialVersionUID = 1L;
	private final ApplicationManager am;
	private List<P> defaultSelected = null;
	private final Class<P> type;
	private final boolean allowIncompletePattern;
	
	public PatternInitMultiEmpty(WidgetPage<?> page, String id,boolean allowIncompletePattern, Class<P> type, ApplicationManager am) {
		this(page, id, false, allowIncompletePattern,type, am);
	}

	public PatternInitMultiEmpty(WidgetPage<?> page, String id, boolean globalWidget,boolean allowIncompletePattern, Class<P> type, ApplicationManager am) {
		super(page, id, globalWidget);
		Objects.requireNonNull(am);
		this.am=am;
		this.type = type;
		this.allowIncompletePattern = allowIncompletePattern;
	}
	
	public PatternInitMultiEmpty(OgemaWidget parent, String id, OgemaHttpRequest req,boolean allowIncompletePattern, Class<P> type, ApplicationManager am) {
		super(parent,id, req);
		Objects.requireNonNull(am);
		this.am =am;
		this.type = type;
		this.allowIncompletePattern= allowIncompletePattern;
	}
	
	public class PatternInitMultiEmptyOptions extends EmptyData {
		
		private List<P> selectedPatterns;

		public PatternInitMultiEmptyOptions(PatternInitMultiEmpty<P,R> empty) {
			super(empty);
		}

		public List<P> getSelectedItems() {
			readLock();
			try {
				return new ArrayList<>(selectedPatterns);
			} finally {
				readUnlock();
			}
		}

		public void selectItems(Collection<P> items) {
			if (items == null)
				items = Collections.emptyList();
			writeLock();
			try {
				this.selectedPatterns = new ArrayList<>(items);
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
	public void selectItems(Collection<P> items, OgemaHttpRequest req) {
		getData(req).selectItems(items);
	}
	
	@Override
	public void selectDefaultItems(Collection<P> items) {
		this.defaultSelected = new ArrayList<>(items);
	}
	
	@Override
	public List<P> getSelectedItems(OgemaHttpRequest req) {
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
				am.getLogger().info("Empty pattern widget could not be initialized with the selected value {}",pt,e);
				aux = null;
			}
			if (aux == null || (!allowIncompletePattern && !aux.isActive()) || !aux.exists())
				continue;
			resources.add(aux);
		}
		
		List<P> patternList = AccessController.doPrivileged(new PrivilegedAction<List<P>>() {

			@Override
			public List<P> run() {
				List<P> result = new ArrayList<>();
				for (R res: resources) {
					try {							
						P pattern = type.getConstructor(Resource.class).newInstance(res);
						if (!allowIncompletePattern) {
							boolean satisfied = am.getResourcePatternAccess().isSatisfied(pattern, type);
							if (!satisfied) {
								am.getLogger().info("Found an incomplete pattern in page initialization - ignoring this: {}",pattern);
								continue;
							}
						}
						result.add(pattern);
					} catch (Exception e) {
						am.getLogger().warn("Could not initalize page with pattern type {} for resource {}",type.getName(),res,e);
					}
				}
				return result;
			}
		});
		if (patternList == null || patternList.isEmpty())
			return;
		am.getLogger().debug("Initializing empty pattern widget with patterns {}",patternList);
		getData(req).selectItems(patternList);
	}
	
	
	
}
