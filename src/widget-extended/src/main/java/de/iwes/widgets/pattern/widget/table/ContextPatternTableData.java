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
