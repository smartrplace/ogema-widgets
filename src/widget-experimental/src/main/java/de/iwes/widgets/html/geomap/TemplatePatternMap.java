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

import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.PatternWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class TemplatePatternMap<P extends ResourcePattern<?>> extends TemplateMap<P> implements PatternWidget<P> {
	
	private static final long serialVersionUID = 1L;
	protected final ResourcePatternAccess rpa;
	protected Class<? extends P> defaultType = null;
	protected final UpdateMode updateMopde;

	public TemplatePatternMap(WidgetPage<?> page, String id, MapTemplate<P> template) {
		this(page, id, template, false);
	}
	
	public TemplatePatternMap(WidgetPage<?> page, String id, MapTemplate<P> template, boolean globalWidget) {
		this(page, id, template, globalWidget, UpdateMode.MANUAL, null, null);
	}
	
	public TemplatePatternMap(WidgetPage<?> page, String id, MapTemplate<P> template, boolean globalWidget, 
				UpdateMode updateMode, Class<? extends P> defaultType, ResourcePatternAccess rpa) {
		super(page, id, template, globalWidget);
		this.rpa = rpa;
		this.defaultType  =defaultType;
		this.updateMopde = updateMode;
	}
	
	protected boolean filter(P instance, OgemaHttpRequest req) {
		return true;
	}


	@Override
	public UpdateMode getUpdateMode() {
		return updateMopde;
	}

	@Override
	public void setType(Class<? extends P> type, OgemaHttpRequest req) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<? extends P> getType(OgemaHttpRequest req) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
