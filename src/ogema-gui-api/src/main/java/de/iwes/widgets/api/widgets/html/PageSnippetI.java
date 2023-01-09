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
	 * @param text
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
	//public PageSnippetI remove(String text,OgemaHttpRequest req);
	default PageSnippetI remove(HtmlItem item,OgemaHttpRequest req) {
		throw new UnsupportedOperationException("not implemented");
	}
	default PageSnippetI remove(OgemaWidget widget,OgemaHttpRequest req) {
		throw new UnsupportedOperationException("not implemented");
	}
	
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
