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
import org.json.JSONObject;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.PatternWidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTableData;

public class PatternTableData<P extends ResourcePattern<?>> extends DynamicTableData<P> implements PatternWidgetData<P> {

	protected volatile Class<? extends P> patternType = null;
	protected final ResourcePatternAccess rpa;
	
	public PatternTableData(PatternTable<P> patternTable, ResourcePatternAccess rpa) {
		super(patternTable);
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
	
	protected void updateOnGet() {
		List<? extends P> patterns;
		if (patternType == null)
			patterns = Collections.emptyList();
		else
			patterns = rpa.getPatterns(patternType, AccessPriority.PRIO_LOWEST);
		updateRows(patterns);
	}

	public Class<? extends P> getType() {
		return patternType;
	}

	public void setType(Class<? extends P> patternType) {
		writeLock();
		try {
			this.patternType = patternType;
			if (getUpdateMode() != UpdateMode.MANUAL) {
				updateOnGet();
			}
		} finally {
			writeUnlock();
		}
	}

	@SuppressWarnings("unchecked")
	protected UpdateMode getUpdateMode() {
		return ((PatternTable<P>) widget).updateMode;
	}
	

}
