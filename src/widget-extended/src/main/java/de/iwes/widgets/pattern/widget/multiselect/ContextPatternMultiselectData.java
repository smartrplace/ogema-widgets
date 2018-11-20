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
