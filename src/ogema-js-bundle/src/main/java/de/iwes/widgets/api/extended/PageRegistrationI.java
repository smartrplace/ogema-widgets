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
package de.iwes.widgets.api.extended;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServlet;

import org.ogema.accesscontrol.AccessManager;
import org.ogema.core.model.simple.IntegerResource;

import de.iwes.widgets.api.extended.impl.AccessCounterAdvanced;
import de.iwes.widgets.api.extended.impl.SessionManagement;
import de.iwes.widgets.api.extended.xxx.ConfiguredWidget;
import de.iwes.widgets.api.extended.xxx.WidgetGroupDerived;
import de.iwes.widgets.api.widgets.WidgetPage;

// FIXME move to impl package
public abstract class PageRegistrationI extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected volatile AtomicInteger accessCountVolatile = new AtomicInteger(0);
	protected volatile IntegerResource accessCountPersistent = null;
	protected volatile Set<AccessCounterAdvanced> parameterAccessCounters;
	// XXX public
	public final SessionManagement sessionManagement;
	protected final AccessManager accessManager;
	protected final WidgetPageBase<?> page;
	
	public PageRegistrationI(SessionManagement sessionManagement, AccessManager accessManager, WidgetPageBase<?> page) {
		this.sessionManagement = sessionManagement;
		this.accessManager = accessManager;
		this.page = page;
	}
	
	public String getServletBase() {
		return ((WidgetPageBase<?>) page).getServletBase();
	}

	public abstract WidgetGroupDerived getGroup(String id);
	
	public abstract WidgetGroupDerived removeGroup(String id);
	
	public abstract void addGroup(WidgetGroupDerived group);
	
	public abstract void close();
	
	public abstract ConfiguredWidget<?> getConfiguredWidget(String id, String sessionId);
	
	public abstract ConfiguredWidget<?> getConfiguredWidget(OgemaWidgetBase<?> widget);
	
	public abstract ConfiguredWidget<?> removeWidget(OgemaWidgetBase<?> widget);
	
	public abstract void addWidget(ConfiguredWidget<?> cw);
	
	public void setPersistentAccessCount(final IntegerResource r) {
		if (r != null && r.exists()) {
			this.accessCountPersistent = r;
			this.accessCountVolatile = null;
		}
	}
	
	public void setPersistentAccessCountForParameters(WidgetPage<?> page, IntegerResource counter, Map<String,String[]> parameters) {
		if (this.parameterAccessCounters == null) {
			synchronized (this) {
				if (this.parameterAccessCounters == null) {
					this.parameterAccessCounters = Collections.newSetFromMap(new ConcurrentHashMap<AccessCounterAdvanced, Boolean>(4));
				}
			}
		}
		parameterAccessCounters.add(new AccessCounterAdvanced(page, parameters, counter));
	}
	
	public final void increaseAccessCount(Map<String,String[]> parameters) {
		final IntegerResource r = accessCountPersistent;
		if (r != null && r.exists()) {
			r.getAndAdd(1);
		} else {
			final AtomicInteger ai = accessCountVolatile;
			if (ai != null) {
				ai.incrementAndGet();
			}
		}
		for (AccessCounterAdvanced counter : getParameterAccessCounts()) {
			counter.touched(parameters);
		}
	}
	
	public int getAccessCount() {
		final IntegerResource r = accessCountPersistent;
		if (r != null && r.exists()) 
			return r.getValue();
		final AtomicInteger ai = accessCountVolatile;
		if (ai != null)
			return ai.get();
		return 0;
	}
	
	private Set<AccessCounterAdvanced> getParameterAccessCounts() {
		final Set<AccessCounterAdvanced> internal = this.parameterAccessCounters;
		if (internal == null)
			return Collections.emptySet();
		else
			return internal;
	}
	
}
