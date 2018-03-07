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

package de.iwes.widgets.pattern.widget.table;

import java.util.Collections;
import java.util.List;

import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.ContextPatternData;

public class ContextPatternTableData<P extends ContextSensitivePattern<?, C>, C> 
		extends PatternTableData<P> implements ContextPatternData<P, C> {

	protected volatile C context = null; 
	
	public ContextPatternTableData(ContextPatternTable<P, C> cpt, ResourcePatternAccess rpa) {
		super(cpt, rpa);
	}
	
	@Override
	protected void updateOnGet() {
		if (getUpdateMode() == UpdateMode.AUTO_ON_GET) {
			List<? extends P> patterns;
			if (patternType == null || context == null)
				patterns = Collections.emptyList();
			else
				patterns = rpa.getPatterns(patternType, AccessPriority.PRIO_LOWEST, context);
			updateRows(patterns);
		}
	}
	
	public void setContext(C context) {
		writeLock();
		try {
			this.context = context;
			if (getUpdateMode() != UpdateMode.MANUAL) {
				updateOnGet();
			}
		} finally {
			writeUnlock();
		}
	}

	@Override
	public C getContext() {
		return context;
	}

}
