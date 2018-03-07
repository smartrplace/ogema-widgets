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
