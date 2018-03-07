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

import java.util.Arrays;
import java.util.Collection;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.pattern.widget.multiselect.PatternMultiselect;
import de.iwes.widgets.resource.widget.init.InitUtil;

/**
 * Allows the user to select multiple {@link ResourcePattern}s from a list. The options can 
 * be specified either through the pattern type (see {@link #setType(Class, OgemaHttpRequest)},
 * or can be set explicitly (see {@link #update(Collection, OgemaHttpRequest)}). <br>
 * 
 * This is similar to a {@see PatternInitDropdown}, but allows to select more than one resource at a time.
 * @param <P>
 */
// TODO listener based updates
public class PatternInitMultiselect<P extends ResourcePattern<?>> extends PatternMultiselect<P> implements InitWidget {

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
	public PatternInitMultiselect(WidgetPage<?> page, String id,ResourcePatternAccess rpa, Class<P> baseType) {
		this(page, id, rpa, UpdateMode.AUTO_ON_GET, baseType);
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
	public PatternInitMultiselect(WidgetPage<?> page, String id,ResourcePatternAccess rpa, UpdateMode updateMode, Class<P> baseType) {
		super(page, id, rpa, updateMode, baseType);
	}
	
	/**
	 * Constructor for session-specific widget with default update mode AUTO_ON_GET
	 * @param parent
	 * @param id
	 * @param rpa
	 * @param baseType
	 * @param req
	 */
    public PatternInitMultiselect(OgemaWidget parent, String id, ResourcePatternAccess rpa, Class<P> baseType, OgemaHttpRequest req) {
        this(parent, id, rpa, UpdateMode.AUTO_ON_GET, baseType, req);
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
    public PatternInitMultiselect(OgemaWidget parent, String id, ResourcePatternAccess rpa, UpdateMode updateMode, Class<P> baseType, OgemaHttpRequest req) {
        super(parent, id, rpa, updateMode, baseType, req);
    }

    @Override
    public void init(OgemaHttpRequest req) {
		String[] patterns = InitUtil.getInitParameters(getPage(), req);
		if (patterns == null || patterns.length == 0)
			return;
		getData(req).selectMultipleOptions(Arrays.asList(patterns));
    }


}
