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

package de.iwes.widgets.api.extended.impl;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * raison d'�tre for this class is that the {@see HttpSession#getId()} method
 * throws an exception when called from a cached object. Hence, we need to 
 * extract the sessionId immediately when keeping track of a request.
 * FIXME ideally, it shouldn't be necessary to store these objects at all.
 * -> currently used in PatternWidgets
 */
public class HistoricHttpRequest extends OgemaHttpRequest {
	
	private final String sessionIdSpecific;
//	private final String sessionIdUnspecific;


	public HistoricHttpRequest(OgemaHttpRequest req) {
		super(req.getReq(), req.isPolling());
		this.sessionIdSpecific  = req.getSessionId();
//		this.sessionIdUnspecific = req.getSessionId(false);
	}
	
	@Override
	public String getSessionId() {
		return sessionIdSpecific;
	}
	
	@Deprecated
	@Override
	public String getSessionId(boolean pageSpecificId) {
		return sessionIdSpecific;
	}
	
}
