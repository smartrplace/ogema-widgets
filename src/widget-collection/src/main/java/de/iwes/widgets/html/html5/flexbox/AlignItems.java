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

package de.iwes.widgets.html.html5.flexbox;

public enum AlignItems {
	
	/**
	 * For {@link FlexDirection#ROW} (the default) this option aligns 
	 * the items vertically at the top.
	 */
	FLEX_LEFT("flex-start"), 
	
	/**
	 * For {@link FlexDirection#ROW} (the default) this option aligns 
	 * the items vertically at the bottom.
	 */
	FLEX_RIGHT("flex-end"), 
	
	/**
	 * For {@link FlexDirection#ROW} (the default) this option centers 
	 * the items vertically .
	 */
	CENTER("center"), 
	BASELINE("baseline"), 
	STRETCH("stretch");
	
	private final String identifier;
	
	private AlignItems(String identifier) {
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}

}
