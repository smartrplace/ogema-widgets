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

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A special widget that mainly serves as a container for other widgets, which can 
 * be appended using {@link #append(OgemaWidget, OgemaHttpRequest)}, or be added to 
 * a {@link HtmlItem}, which is then appended to the snippet via 
 * {@link #append(HtmlItem, OgemaHttpRequest)}.
 */
public interface PageSnippetI extends OgemaWidget {

	/**
	 * Append a String to the snippet.
	 * 
	 * @param html
	 * @param req
	 * 	 	may be null, if the widget is global (no session management)
	 * @return
	 * 		this
	 */
	public PageSnippetI append (String text,OgemaHttpRequest req);
	
	/**
	 * Append some Html to the snippet.
	 * 
	 * @param item
	 * @param req
	 * 	 	may be null, if the widget is global (no session management)
	 * @return
	 * 		this
	 */
	public PageSnippetI append (HtmlItem item,OgemaHttpRequest req);
	
	/**
	 * Append a widget to the snippet.
	 * 
	 * @param widget
	 * @param req
	 * 		may be null, if the widget is global (no session management)
	 * @return
	 * 		this
	 */
	public PageSnippetI append (OgemaWidget widget,OgemaHttpRequest req);
	
	/**
	 * Append a line break to the snippet.
	 * 
	 * @param req
	 * 		may be null, if the widget is global (no session management)
	 * @return
	 * 		this
	 */
	public PageSnippetI linebreak(OgemaHttpRequest req);
	
}
