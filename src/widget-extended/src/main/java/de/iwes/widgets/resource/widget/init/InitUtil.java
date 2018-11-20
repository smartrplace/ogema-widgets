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
