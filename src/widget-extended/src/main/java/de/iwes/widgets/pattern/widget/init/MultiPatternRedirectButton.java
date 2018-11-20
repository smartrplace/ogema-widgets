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
package de.iwes.widgets.pattern.widget.init;

import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.extended.pattern.PatternMultiSelector;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.TemplateMultiRedirectButton;

/**
 * Like {@link PatternRedirectButton}, except that multiple pattern instances can be selected and 
 * written as parameters to the url of the new page.<br>
 * On the new page the parameter can be evaluated for instance by a {@link PatternInitMultiselect} widget.
 * 
 * @see TemplateMultiRedirectButton 
 * @see PatternRedirectButton
 * @param <P>
 */
public class MultiPatternRedirectButton<P extends ResourcePattern<?>> extends TemplateMultiRedirectButton<P> {

	private static final long serialVersionUID = 1L;
	
	public MultiPatternRedirectButton(WidgetPage<?> page, String id, String text, String defaultUrl) {
		super(page, id, text, defaultUrl);
	}
	
	public MultiPatternRedirectButton(OgemaWidget parent, String id, String text, String destinationUrl, OgemaHttpRequest req) {
    	super(parent, id, text, destinationUrl, req);
    }

	public MultiPatternRedirectButton(WidgetPage<?> page, String id, String text, PatternMultiSelector<P> selector) {
		super(page, id, text, selector);
	}
	
    public MultiPatternRedirectButton(OgemaWidget parent, String id, String text, String destinationUrl,PatternMultiSelector<P> selector, OgemaHttpRequest req) {
    	super(parent, id, text, destinationUrl, selector, req);
    }
    
    @Override
    protected String getConfigId(P object) {
    	return object.model.getPath();
    }

}
