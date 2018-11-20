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

/**
 * A triggerable client-side action of a widget, such as an Http request.<br> 
 * 
 * This class should be thought of as an enum of Strings; individual widgets can provide 
 * additional allowed values by offering their own static TriggeredAction fields. 
 */
public class TriggeredAction {
	
	public static final TriggeredAction GET_REQUEST = new TriggeredAction("sendGET");
	public static final TriggeredAction POST_REQUEST = new TriggeredAction("sendPOST");
	public static final TriggeredAction HIDE_WIDGET = new TriggeredAction("hideWidget");
	public static final TriggeredAction SHOW_WIDGET = new TriggeredAction("showWidget");
	
	private final String triggeredAction;
	private final Object[] args;
	
	public TriggeredAction(String triggeredAction) {
		this(triggeredAction, null);
	}
	
	public TriggeredAction(String triggeredAction, Object[] args) {
		this.triggeredAction = triggeredAction;
		this.args = args;
	}
	
	public String getAction() {
		return triggeredAction;
	}
	
	public Object[] getArgs() {
		return args;
	}
	
	@Override
	public int hashCode() {
		return triggeredAction.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof TriggeredAction))
			return false;
		TriggeredAction other = (TriggeredAction) obj;
		if (!other.triggeredAction.equals(triggeredAction))
			return false;
		if (args == null && other.args == null) // should be the most common case
			return true;
		if (args == null || other.args == null)
			return false;
		if (args.length != other.args.length)
			return false;
		for (int i=0;i<args.length;i++) {
			if (!args[i].equals(other.args[i]))
				return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "Triggered action: " +triggeredAction ;
	}
	
}

