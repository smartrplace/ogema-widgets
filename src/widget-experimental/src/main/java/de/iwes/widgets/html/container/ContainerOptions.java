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
package de.iwes.widgets.html.container;

import java.util.HashMap;
import java.util.Map;

import de.iwes.widgets.api.extended.html.bricks.PageSnippetData;

public class ContainerOptions extends PageSnippetData {
	
	private String backgroundColor = null;
	private boolean fullscreen = false;

	public ContainerOptions(Container container) {
		super(container);
	}
	
	@Override
	protected String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"container");
		if (fullscreen)
			sb.append("-fluid");
		sb.append("\">");
		sb.append(super.getHtml());
		sb.append("</div>");
		return sb.toString();
	}
	
	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
		if (backgroundColor != null) {
			Map<String,String> cl = new HashMap<String, String>();
			cl.put("background-color", this.backgroundColor);
			addCssItem(">div>div", cl);
		}
		else
			removeCSSItem(".app-container", "background-color");
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}
	
}
