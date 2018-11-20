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
package de.iwes.widgets.html.html5.grid;

import java.util.Objects;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.html.HtmlItem;

public class Content {

	public static final Content EMPTY_CONTENT = new Content(); 
	private final Object content;
	private final ContentType contentType;
	
	private Content() {
		this.contentType = ContentType.EMPTY;
		this.content = null;
	}
	
	public Content(Object content) {
		this.content = Objects.requireNonNull(content);
		this.contentType = content instanceof OgemaWidget ? ContentType.OGEMA_WIDGET :
			content instanceof HtmlItem ? ContentType.HMTL : 
			content == null ? ContentType.EMPTY :
			ContentType.TEXT;
	}
	
	public Content(Object content, ContentType type) {
		this.content = Objects.requireNonNull(content);
		this.contentType = Objects.requireNonNull(type);
	}

	public Object getContent() {
		return content;
	}

	public ContentType getContentType() {
		return contentType;
	}
	
	public JSONObject toJson() {
		final JSONObject json  =new JSONObject();
		json.put("type", contentType.getType());
		json.put("content", 
			contentType == ContentType.EMPTY ? "" : 	
			contentType == ContentType.OGEMA_WIDGET ? ((OgemaWidgetBase<?>) content).getId() : 
			contentType == ContentType.HMTL ? (content instanceof HtmlItem ? ((HtmlItem) content).getHtml() : content.toString()): 
			StringEscapeUtils.escapeHtml4(content.toString())); 
		return json;
	}

}
