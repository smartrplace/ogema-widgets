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
