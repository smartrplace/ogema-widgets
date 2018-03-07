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

