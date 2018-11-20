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
package de.iwes.widgets.pattern.widget.accordion;

import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.ContextPatternWidget;
import de.iwes.widgets.api.extended.pattern.PatternSnippetTemplate;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.accordion.Accordion;
import de.iwes.widgets.html.accordion.AccordionData;
import de.iwes.widgets.template.PageSnippetTemplate;

/**
 * A version of the {@link Accordion} that contains one Accordion item per pattern match. 
 * Implement {@link PatternSnippetTemplate} and pass it to the constructor, to specify the
 * accordion item for a pattern match.
 * 
 * @author cnoelle
 */
public class ContextPatternAccordion<P extends ContextSensitivePattern<?, C>, C> extends PatternAccordion<P>
		implements ContextPatternWidget<P,C> {

	private static final long serialVersionUID = 1L;
	private C defaultContext = null;
	
	public ContextPatternAccordion(WidgetPage<?> page, String id, Class<? extends P> defaultType, PageSnippetTemplate<P> template, ResourcePatternAccess rpa) {
		super(page, id, defaultType, template, rpa);
	}
	
	public ContextPatternAccordion(WidgetPage<?> page, String id, boolean globalWidget, Class<? extends P> defaultType, 
			PageSnippetTemplate<P> template, ResourcePatternAccess rpa, UpdateMode updateMode) {
		super(page, id, globalWidget, defaultType, template, rpa, updateMode);
	}
	
	public ContextPatternAccordion(OgemaWidget parent, String id, OgemaHttpRequest req, Class<? extends P> defaultType, 
			PageSnippetTemplate<P> template, ResourcePatternAccess rpa,  UpdateMode updateMode) {
		super(parent, id, req, defaultType, template, rpa, updateMode);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ContextPatternAccordionData<P,C> getData(OgemaHttpRequest req) {
		return (ContextPatternAccordionData<P,C>) super.getData(req);
	}
	
	@Override
	public ContextPatternAccordionData<P,C> createNewSession() {
		return new ContextPatternAccordionData<P,C>(this, rpa);
	}

	@Override
	protected void setDefaultValues(AccordionData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		ContextPatternAccordionData<P,C> opt2 = (ContextPatternAccordionData<P, C>) opt;
		opt2.setContext(defaultContext);
	}
	
	@Override
	public C getContext(OgemaHttpRequest req) {
		return getData(req).getContext();
	}
	
	@Override
	public void setContext(C context, OgemaHttpRequest req) {
		getData(req).setContext(context);
	}
	
	@Override
	public void setDefaultContext(C context) {
		this.defaultContext = context;
	}
}
