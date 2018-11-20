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

public enum JustifyContent {
	
	FLEX_LEFT("flex-start"), 
	FLEX_RIGHT("flex-end"), 
	CENTER("center"), 
	SPACE_BETWEEN("space-between"), 
	SPACE_AROUND("space-around"),
	SPACE_EVENLY("space-evenly");
	
	private final String identifier;
	
	private JustifyContent(String identifier) {
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}

}
