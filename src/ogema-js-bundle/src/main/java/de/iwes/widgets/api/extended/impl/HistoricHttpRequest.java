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
package de.iwes.widgets.api.extended.impl;

import javax.servlet.http.HttpSession;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * raison d'etre for this class is that the {@link HttpSession#getId()} method
 * throws an exception when called from a cached object. Hence, we need to 
 * extract the sessionId immediately when keeping track of a request.
 * FIXME ideally, it shouldn't be necessary to store these objects at all.
 * -&gt; currently used in PatternWidgets
 * @deprecated presumably not required any more... {@link OgemaHttpRequest#getSessionId()}
 * does not access {@link HttpSession} any more.
 */
@Deprecated 
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
