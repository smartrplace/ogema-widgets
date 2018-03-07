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

package de.iwes.widgets.pattern.widget.multiselect;

import java.util.Collections;
import java.util.List;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

public class ContextPatternMultiselectData<P extends ContextSensitivePattern<?, C>,C> extends PatternMultiselectData<P>  {
	
	private volatile C context = null;

	public ContextPatternMultiselectData(ContextPatternMultiselect<P,C> multiselect, ResourcePatternAccess rpa) {
		super(multiselect,rpa);
	}
	
	public void setPatternType(Class<? extends P> type, C context) {
		writeLock();
		try {
			this.patternType = type;
			setContext(context);
		} finally {
			writeUnlock();
		}
	}
	
//	public List<R> getSelectedResources() {
//		return new ArrayList<R>(selected.values()); 
//	}
	
	@Override
	protected void updateOnGet() {
		List<? extends P> resources;
		if (patternType == null || context == null) {
			resources = Collections.emptyList();
		} else {
			resources = rpa.getPatterns(patternType, AccessPriority.PRIO_LOWEST, context);
		}
		update(resources);
	}

	public C getContext() {
		return context;
	}

	public void setContext(C context) {
		this.context = context;
	}
	
}
