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
