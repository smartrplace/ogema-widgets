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

package de.iwes.widgets.resource.widget.init;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.resource.widget.dropdown.ResourceDropdown;

/**
 * A pattern dropdown that is initialized from the url parameter "configId".
 * Include it in a page and open the url:<br> 
 * <code>https://<HOST>:<PORT>/path/to/page?configId=<SELECTED_RESOURCe></code>
 * <br>
 * where SELECTED_RESOURCE is the path to some resource included in the dropdown options. 
 * The resource will then be preselected on the new page.
 * <br>
 * The URL may be created for instance by a {@link ResourceRedirectButton}
 *
 * @see ResourceDropdown
 *
 * @param <R>
 */
public class ResourceInitDropdown<R extends Resource> extends ResourceDropdown<R> implements InitWidget {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Update mode {@link UpdateMode#MANUAL}
	 * @param page
	 * @param id
	 */
	public ResourceInitDropdown(WidgetPage<?> page, String id, boolean globalWidget) {
		this(page, id, globalWidget, UpdateMode.MANUAL, null,null);
	}
	
	/**
	 * Update mode {@link UpdateMode#AUTO_ON_GET}
	 * @param page
	 * @param id
	 * @param defaultType
	 * @param rpa
	 */
	public ResourceInitDropdown(WidgetPage<?> page, String id, Class<? extends R> defaultType, ResourceAccess ra) {
		this(page, id, false, UpdateMode.AUTO_ON_GET, defaultType, ra);
	}
	
	/**
	 * Generic constructor.
	 * @param page
	 * @param id
	 * @param globalWidget
	 * @param updateMode
	 * @param defaultType
	 * @param rpa
	 */
	public ResourceInitDropdown(WidgetPage<?> page, String id, boolean globalWidget, UpdateMode updateMode,  Class<? extends R> defaultType, ResourceAccess ra) {
		super(page, id, globalWidget, defaultType, updateMode, ra);
	}

	/**
	 * Session widget; only exists for one user
	 * @param parent
	 * @param id
	 * @param req
	 * @param updateMode
	 * @param defaultType
	 * @param rpa
	 */
	public ResourceInitDropdown(OgemaWidget parent, String id, OgemaHttpRequest req, UpdateMode updateMode,  Class<? extends R> defaultType, ResourceAccess ra) {
		super(parent, id, defaultType, updateMode, ra, req);
	}


	@Override
	public void init(OgemaHttpRequest req) {
		String[] patterns = InitUtil.getInitParameters(getPage(), req);
		if (patterns == null || patterns.length == 0)
			return;
		String selected = patterns[0];
		getData(req).selectSingleOption(selected);
	}

	
}
