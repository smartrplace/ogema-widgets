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
package de.iwes.widgets.api.widgets.dynamics;

import java.util.Objects;

/**
 * A client-side action of a widget that can trigger further actions, such as an Http request.<br> 
 * 
 * This class should be thought of as an enum of Strings; individual widgets can provide 
 * additional allowed values by offering their own static TriggeringAction fields. 
 */
public class TriggeringAction {
	
	public static final TriggeringAction GET_REQUEST = new TriggeringAction("GET");
	public static final TriggeringAction POST_REQUEST = new TriggeringAction("POST");
	public static final TriggeringAction PRE_POST_REQUEST = new TriggeringAction("prePOST");
	public static final TriggeringAction ON_CLICK = new TriggeringAction("click");
	
	private final String triggeringAction;
	
	public TriggeringAction(String triggeringAction) {
		Objects.requireNonNull(triggeringAction);
		this.triggeringAction = triggeringAction;
	}
	
	public String getAction() {
		return triggeringAction;
	}
	
	@Override
	public int hashCode() {
		return triggeringAction.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof TriggeringAction))
			return false;
		return ((TriggeringAction) obj).triggeringAction.equals(this.triggeringAction);
	}
	
	@Override
	public String toString() {
		return "Triggering action: " +triggeringAction;
	}
	
}
