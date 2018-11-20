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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.PatternWidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class TemplatPatternMapData<P extends ResourcePattern<?>> extends TemplateMapData<P> implements PatternWidgetData<P> {
	
	protected volatile Class<? extends P> type = null;
	protected final ResourcePatternAccess rpa;
	
	protected TemplatPatternMapData(TemplatePatternMap<P> widget, MapTemplate<P> template, ResourcePatternAccess rpa) {
		super(widget, template);
		this.rpa = rpa;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		if (((TemplatePatternMap<P>) widget).getUpdateMode() == UpdateMode.AUTO_ON_GET) {
			writeLock();
			try {
				updateOnGet(req);
			} finally {
				writeUnlock();
			}
		}
		return super.retrieveGETData(req);
	}
	
	protected void updateOnGet(OgemaHttpRequest req){
		List<? extends P> patterns;
		if (type == null)
			patterns = Collections.emptyList();
		else
			patterns = rpa.getPatterns(type, AccessPriority.PRIO_LOWEST);
		@SuppressWarnings("unchecked")
		final TemplatePatternMap<P> w2 = (TemplatePatternMap<P>) widget;
		final Iterator<? extends P> it = patterns.iterator();
		while (it.hasNext()) {
			final P p = it.next();
			if (!w2.filter(p, req))
				it.remove();
		}
		update(patterns);
	}
	
	
	@Override
	public void setType(Class<? extends P> type) {
		writeLock();
		try {
			this.type = type;
		} finally {
			writeUnlock();
		}
	}

	@Override
	public Class<? extends P> getType() {
		readLock();
		try {
			return type;
		} finally {
			readUnlock();
		}
	}
	
	

}
