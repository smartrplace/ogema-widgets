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
	 * @return
	 */
	public void addPattern(PageSnippet snippet, P newPattern, OgemaHttpRequest req);
	
}
