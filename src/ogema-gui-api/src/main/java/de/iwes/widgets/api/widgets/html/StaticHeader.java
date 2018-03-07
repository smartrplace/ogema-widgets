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

package de.iwes.widgets.api.widgets.html;

/**
 * This is a static header, i.e. it does not provide localisation options.
 * Use a widget instead for more flexible headers. See for instance
 * {@see org.ogema.tools.widget.html.form.label.Header}.
 */
public class StaticHeader extends HtmlItem {
	
	/**
	 * Default size header (corresponds to &lt;h1&gt; tag)
	 * @param headerText
	 */
	public StaticHeader(String headerText) {
		this(1, headerText);
	}
	
	/**
	 * @param size from <br>
	 * 		1: largest <br>
	 * 		6: smallest 	
	 * @param headerText
	 */
	public StaticHeader(int size, String headerText) {
		super("h" + size);
		addSubItem(headerText);
	}
	
	
}
