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
package de.iwes.widgets.api.extended.pattern;

import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public interface PatternSnippetTemplate<P extends ResourcePattern<?>> {

	/**
	 * Create a page snippet for a pattern match.<br>
	 * Remember to use the pattern id (newPattern.model.getLocation()) in the widget id
	 * of the page snippet, and also in the id of all of its subwidgets. Otherwise name 
	 * collisions will occur if there is more than one pattern match.
	 * @param snippet
	 * @param newPattern
	 * @param req
	 * 			note that this can be null (for global widgets)
	 */
	public void addPattern(PageSnippet snippet, P newPattern, OgemaHttpRequest req);
	
}
