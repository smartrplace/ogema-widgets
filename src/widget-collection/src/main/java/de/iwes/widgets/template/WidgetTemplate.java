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
package de.iwes.widgets.template;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

// TODO use in ListGroup, and other widgets?
public interface WidgetTemplate<T> extends DisplayTemplate<T> {

	/**
	 * Return either
	 * <ul>
	 *   <li>an OGEMA widget
	 *   <li>an HTML item
	 *   <li>a String, or object whose toString method returns the value to be displayed
	 * </ul>
	 * @param item
	 * @return
	 */
	Object getItem(T item, OgemaWidget parent, OgemaHttpRequest req);
	
	@Override
	default String getId(T object) {
		return object instanceof OgemaWidget ? ((OgemaWidget) object).getId() : String.valueOf(object);
	}
	
	@Override
	default String getLabel(T object, OgemaLocale locale) {
		return getId(object);
	}
	
	default JSONObject serialize(Object item) {
		final JSONObject json = new JSONObject();
		final int type = item instanceof OgemaWidget ? 2 : item instanceof HtmlItem ? 1 : 0;
		json.put("type", type);
		final String content = type == 2 ? ((OgemaWidget) item).getId() :
			type == 1 ? ((HtmlItem) item).getHtml() : StringEscapeUtils.escapeHtml4(String.valueOf(item));
		json.put("content", content);
		return json;
	}
	
}
