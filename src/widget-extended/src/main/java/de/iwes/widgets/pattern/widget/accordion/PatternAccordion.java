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

package de.iwes.widgets.pattern.widget.accordion;

import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.PatternWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.accordion.Accordion;
import de.iwes.widgets.html.accordion.AccordionData;
import de.iwes.widgets.html.accordion.TemplateAccordion;
import de.iwes.widgets.template.PageSnippetTemplate;

/**
 * A version of the {@link Accordion} that contains one Accordion item per {@link ResourcePattern} match. 
 * Implement {@link PageSnippetTemplate} and pass it to the constructor, to specify how the 
 * accordion item for a pattern match shall look like.
 *
 * @param <P>
 * @param <R>
 */
public class PatternAccordion<P extends ResourcePattern<?>> extends TemplateAccordion<P> implements PatternWidget<P> {

	private static final long serialVersionUID = 1L;
	
	protected Class<? extends P> patternType;
	protected final ResourcePatternAccess rpa;
	protected final UpdateMode updateMode;
	
	public PatternAccordion(WidgetPage<?> page, String id, Class<? extends P> defaultType, PageSnippetTemplate<P> template, ResourcePatternAccess rpa) {
		this(page, id, false, defaultType, template, rpa, UpdateMode.AUTO_ON_GET);
	}
	
	public PatternAccordion(WidgetPage<?> page, String id, boolean globalWidget, Class<? extends P> defaultType, PageSnippetTemplate<P> template, 
			ResourcePatternAccess rpa, UpdateMode updateMode) {
		super(page, id, globalWidget, template);
		this.rpa = rpa;
		this.patternType= defaultType;
		this.updateMode = updateMode;
	}
	
	public PatternAccordion(OgemaWidget parent, String id, OgemaHttpRequest req, Class<? extends P> defaultType, PageSnippetTemplate<P> template, 
			ResourcePatternAccess rpa, UpdateMode updateMode) {
		super(parent, id, req, template);
		this.rpa = rpa;
		this.patternType= defaultType;
		this.updateMode = updateMode;
	}
	
	@Override
	public PatternAccordionData<P> createNewSession() {
		return new PatternAccordionData<P>(this,rpa);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PatternAccordionData<P> getData(OgemaHttpRequest req) {
		PatternAccordionData<P> opt = (PatternAccordionData<P>) super.getData(req);
		return opt;
	}
	
	@Override
	protected void setDefaultValues(AccordionData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		PatternAccordionData<P> opt2 = (PatternAccordionData<P>) opt;
		opt2.setType(patternType);
	}

	@Override
	public UpdateMode getUpdateMode() {
		return updateMode;
	}

	@Override
	public void setType(Class<? extends P> type, OgemaHttpRequest req) {
		getData(req).setType(type);
	}

	@Override
	public Class<? extends P> getType(OgemaHttpRequest req) {
		return getData(req).getType();
	}


}
