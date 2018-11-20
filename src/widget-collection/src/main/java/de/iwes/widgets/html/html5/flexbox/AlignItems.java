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
