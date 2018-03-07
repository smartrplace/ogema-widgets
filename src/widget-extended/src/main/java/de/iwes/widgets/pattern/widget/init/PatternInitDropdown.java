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

package de.iwes.widgets.pattern.widget.init;

import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.pattern.widget.dropdown.PatternDropdown;
import de.iwes.widgets.resource.widget.init.InitUtil;

/**
 * A pattern dropdown that is initialized from the url parameter "configId".
 * Include it in a page and open the url:<br> 
 * <code>https://<HOST>:<PORT>/path/to/page?configId=<SELECTED_PATTERN></code>
 * <br>
 * where SELECTED_PATTERN is the path to some pattern model included in the dropdown options. 
 * The pattern will then be preselected on the page.
 * <br>
 * The URL may be created for instance by a {@link PatternRedirectButton}
 *
 * @see PatternDropdown
 *
 * @param <P>
 * @param <R>
 */
public class PatternInitDropdown<P extends ResourcePattern<?>> extends PatternDropdown<P> implements InitWidget {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Update mode {@link UpdateMode#MANUAL}
	 * @param page
	 * @param id
	 */
	public PatternInitDropdown(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}
	
	/**
	 * Update mode {@link UpdateMode#AUTO_ON_GET}
	 * @param page
	 * @param id
	 * @param defaultType
	 * @param rpa
	 */
	public PatternInitDropdown(WidgetPage<?> page, String id, Class<? extends P> defaultType, ResourcePatternAccess rpa) {
		this(page, id, false, UpdateMode.AUTO_ON_GET, defaultType, rpa);
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
	public PatternInitDropdown(WidgetPage<?> page, String id, boolean globalWidget, UpdateMode updateMode, Class<? extends P> defaultType, ResourcePatternAccess rpa) {
		super(page, id, globalWidget, updateMode, defaultType, rpa);
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
	public PatternInitDropdown(OgemaWidget parent, String id, OgemaHttpRequest req, UpdateMode updateMode, Class<? extends P> defaultType, ResourcePatternAccess rpa) {
		super(parent, id, req, updateMode, defaultType, rpa);
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
