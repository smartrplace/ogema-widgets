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

import java.net.URLEncoder;
import java.util.Map;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.TemplateRedirectButton;

public class InitUtil {
	
	public static String[] getInitParameters(WidgetPage<?> page, OgemaHttpRequest req) {
		Map<String,String[]> params = page.getPageParameters(req);
		if (params == null || params.isEmpty())
			return null;
		String[] patterns = params.get(TemplateRedirectButton.PAGE_CONFIG_PARAMETER);
//		convertParameters(patterns);
		return patterns;
	}

	/**
	 * 
	 * @param in
	 * @deprecated use {@link URLEncoder#encode(String, String)} to encode parameters
	 */
	@Deprecated
    public static final void convertParameters(String[] in) {
    	if (in == null) return;
    	for (int i=0;i<in.length;i++) {
    		in[i] = in[i].replace('_', '/');
    	}
    }
    
}
