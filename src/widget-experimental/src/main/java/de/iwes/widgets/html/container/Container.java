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
