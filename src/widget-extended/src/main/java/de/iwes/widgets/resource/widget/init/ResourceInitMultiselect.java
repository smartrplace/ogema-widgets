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

import java.util.Arrays;
import java.util.Collection;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.resource.widget.multiselect.ResourceMultiselect;

/**
 * Allows the user to select multiple {@link Resource}s from a list. The options can 
 * be specified either through the pattern type (see {@link #setType(Class, OgemaHttpRequest)},
 * or can be set explicitly (see {@link #update(Collection, OgemaHttpRequest)}). <br>
 * 
 * This is similar to a {@see ResourceInitDropdown}, but allows to select more than one resource at a time.
 * @param <R>
 */
// TODO listener based updates
public class ResourceInitMultiselect<R extends Resource> extends ResourceMultiselect<R> implements InitWidget {

	private static final long serialVersionUID = 1L;
    /*
     ******** Constructors **********
     */

	/**
	 * Constructor for default update mode AUTO_ON_GET.
	 * @param page
	 * @param id
	 * @param rpa
	 * @param baseType
	 */
	public ResourceInitMultiselect(WidgetPage<?> page, String id,ResourceAccess ra, Class<R> baseType) {
		this(page, id, false, ra, UpdateMode.AUTO_ON_GET, baseType);
	}

	/**
	 * @param page
	 * @param id
	 * @param rpa
	 * 		may be null if and only if updateMode is MANUAL
	 * @param updateMode
	 * @param baseType
	 * 		may be null if and only if updateMode is MANUAL
	 */
	public ResourceInitMultiselect(WidgetPage<?> page, String id,boolean globalWidget,ResourceAccess ra, UpdateMode updateMode, Class<R> baseType) {
		super(page, id, globalWidget, ra, updateMode, baseType);
	}
	
	/**
	 * Constructor for session-specific widget with default update mode AUTO_ON_GET
	 * @param parent
	 * @param id
	 * @param rpa
	 * @param baseType
	 * @param req
	 */
    public ResourceInitMultiselect(OgemaWidget parent, String id,ResourceAccess ra, Class<R> baseType, OgemaHttpRequest req) {
        this(parent, id, ra, UpdateMode.AUTO_ON_GET, baseType, req);
    }
    
    /**
     * 
     * @param parent
     * @param id
     * @param rpa
     * 		may be null if and only if updateMode is MANUAL
     * @param updateMode
     * @param baseType
     * 	 	may be null if and only if updateMode is MANUAL
     * @param req
     */
    public ResourceInitMultiselect(OgemaWidget parent, String id,ResourceAccess ra, UpdateMode updateMode, Class<R> baseType, OgemaHttpRequest req) {
        super(parent, id, ra, updateMode, baseType, req);
    }

    @Override
    public void init(OgemaHttpRequest req) {
		String[] patterns = InitUtil.getInitParameters(getPage(), req);
		if (patterns == null || patterns.length == 0)
			return;
		getData(req).selectMultipleOptions(Arrays.asList(patterns));
    }


}
