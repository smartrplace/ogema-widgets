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
package org.ogema.tools.widget.test.tools;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.xxx.WidgetGroupDerived;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class WidgetGroupImpl implements WidgetGroupDerived {
	
	private final Set<OgemaWidgetBase<?>> widgets;
	private final String id;
	private long pollingInterval = -1;
	
	/**
	 * TODO make sure all widgets belong to the same page?
	 * @param id
	 * @param widgets
	 */
	public WidgetGroupImpl(String id, Collection<OgemaWidget> widgets) {
		this.id = id;	
		this.widgets = Collections.synchronizedSet(new LinkedHashSet<OgemaWidgetBase<?>>());
		for (OgemaWidget w: widgets) {
			this.widgets.add((OgemaWidgetBase<?>) w);
		}
	}
	
	/**
	 * @return
	 * 		a new set of the widgets that constitute the group
	 */
	@Override
	public Set<OgemaWidget> getWidgets() {
		return new LinkedHashSet<OgemaWidget>(widgets);
	}
	
	@Override
	public boolean addWidget(OgemaWidget widget) {
		OgemaWidgetBase<?> wImpl = (OgemaWidgetBase<?>) widget;
		wImpl.addGroup(id);
		return this.widgets.add(wImpl);
	}
	
	@Override
	public boolean removeWidget(OgemaWidget widget) {
		OgemaWidgetBase<?> wImpl = (OgemaWidgetBase<?>) widget;
		wImpl.removeGroup(id);
		return this.widgets.remove(wImpl);
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	
	boolean removeWidget(String widgetId) {
		synchronized (widgets) {
			Iterator<OgemaWidgetBase<?>> it = widgets.iterator();
			while (it.hasNext()) {
				OgemaWidgetBase<?> wg = it.next();
				if (wg.getId()==widgetId) { 
					it.remove();
					return true;
				}
			}	
			return false;
		}		
	}

	@Override
	public int size() {
		return widgets.size();
	}

	@Override
	public Set<OgemaWidgetBase<?>> getWidgetsImpl() {
		return widgets;
	}

	@Override
	public void setPollingInterval(long interval) {
		pollingInterval = interval;
	}

	@Override
	public long getPollingInterval() {
		return pollingInterval;
	}

	@Override
	public void setDefaultSendValueOnChange(boolean sendValue) {
		synchronized (widgets) {
			for (OgemaWidget w: widgets) {
				w.setDefaultSendValueOnChange(sendValue);
			}
		}
	}
	
	@Override
	public void setDefaultVisibility(boolean visible) {
		synchronized (widgets) {
			for (OgemaWidget w: widgets) {
				w.setDefaultVisibility(visible);
			}
		}
	}
	
	@Override
	public void setWidgetVisibility(boolean visible, OgemaHttpRequest req) {
		synchronized (widgets) {
			for (OgemaWidget w: widgets) {
				w.setWidgetVisibility(visible, req);
			}
		}
	}
	
	
}
