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
package de.iwes.widgets.pattern.widget.accordion;

import java.util.Collections;
import java.util.List;
import org.json.JSONObject;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.PatternWidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.accordion.TemplateAccordionData;

 public class PatternAccordionData<P extends ResourcePattern<?>> extends TemplateAccordionData<P> implements PatternWidgetData<P> {
	 
	protected final ResourcePatternAccess rpa;
	protected volatile Class<? extends P> type = null;

	public PatternAccordionData (PatternAccordion<P> patternAccordion, ResourcePatternAccess rpa) {
		super(patternAccordion);
		this.rpa = rpa;
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		if (getUpdateMode() == UpdateMode.AUTO_ON_GET) {
			writeLock();
			try {
				updateOnGet();
			} finally {
				writeUnlock();
			}
		}
		return super.retrieveGETData(req);
	}
	
	protected void updateOnGet(){
		List<? extends P> patterns;
		if (type == null)
			patterns = Collections.emptyList();
		else
			patterns = rpa.getPatterns(type, AccessPriority.PRIO_LOWEST);
		update(patterns);
	}
	
	@Override
	public void setType(Class<? extends P> type) {
		writeLock();
		try {
			this.type = type;
			if (getUpdateMode() != UpdateMode.MANUAL) {
				updateOnGet();
			}
		} finally {
			writeUnlock();
		}
	}

	@Override
	public Class<? extends P> getType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	protected UpdateMode getUpdateMode() {
		return ((PatternAccordion<P>) widget).updateMode;
	}
	
}
