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

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.extended.html.bricks.PageSnippetData;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class Container extends PageSnippet {

	private static final long serialVersionUID = 1L;
	private String defaultBackgroundColor = "#EEEEEE";;
	private boolean defaultFullscreen = false;

	public Container(WidgetPage<?> page, String id) {
		super(page, id);
	}

	public Container(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}
	
	public Container(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent,id,req);
	}
	
	@Override
	public ContainerOptions createNewSession() {
		return new ContainerOptions(this);
	}
	
	@Override
	public ContainerOptions getData(OgemaHttpRequest req) {
		return (ContainerOptions) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(PageSnippetData opt) {
		super.setDefaultValues(opt);
		ContainerOptions opt2 = (ContainerOptions) opt;
		opt2.setBackgroundColor(defaultBackgroundColor);
		opt2.setFullscreen(defaultFullscreen);
	}
	
	public void setDefaultBackgroundColor(String backgroundColor) {
		this.defaultBackgroundColor = backgroundColor;
	}
	
	public String getBackgroundColor(OgemaHttpRequest req) {
		return getData(req).getBackgroundColor();
	}

	public void setBackgroundColor(String backgroundColor, OgemaHttpRequest req) {
		getData(req).setBackgroundColor(backgroundColor);
	}
	
	public void setDefaultFullscreen(boolean fullscreen) {
		this.defaultFullscreen = fullscreen;
	}
	
	public boolean isFullscreen(OgemaHttpRequest req) {
		return getData(req).isFullscreen();
	}

	public void setFullscreen(boolean fullscreen, OgemaHttpRequest req) {
		getData(req).setFullscreen(fullscreen);
	}
}
