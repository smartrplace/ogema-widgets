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
package de.iwes.widgets.multiselect.extended;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ogema.core.model.array.StringArrayResource;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.multiselect.extended.MultiSelectExtended;
import de.iwes.widgets.template.DefaultDisplayTemplate;

/** The widget writes to the resource, but provides no further feedback when the
 * StringArrayResource is changed. This should be done via a listener if required.
 */
public abstract class MultiSelectExtendedStringArray<T> extends MultiSelectExtended<T> {
	private static final long serialVersionUID = 1L;

	protected abstract Collection<T> getItems(String[] currentlySelected, OgemaHttpRequest req);
	protected abstract T getItemByString(String id);
	protected abstract String getId(T item);
	protected abstract String getLabel(T item, OgemaLocale locale);
	
	protected final StringArrayResource resource;
	
	public MultiSelectExtendedStringArray(WidgetPage<?> page, String id,
			boolean useGlyphicons, String buttonStyle, boolean buttonsOnTop, boolean registerDependentWidget,
			StringArrayResource resource) {
		super(page, id, null, useGlyphicons, buttonStyle, buttonsOnTop, registerDependentWidget);
		
		TemplateMultiselect<T> multiLoc = new TemplateMultiselect<T>(page, "multiSelect"+id) {
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				String[] current = resource.getValues();
				update(MultiSelectExtendedStringArray.this.getItems(current, req), req);
				Collection<T> currentItems = new ArrayList<>();
				for(String s: current) {
					T item = MultiSelectExtendedStringArray.this.getItemByString(s);
					if(item != null) currentItems.add(item);
				}
				selectItems(currentItems, req);
			}

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				List<T> currentItems = getSelectedItems(req);
				String[] result = new String[currentItems.size()];
				for(int i=0; i<currentItems.size(); i++) {
					result[i] = MultiSelectExtendedStringArray.this.getId(currentItems.get(i));
				}
				resource.setValues(result);
			}
			
		};
		multiLoc.setTemplate(new DefaultDisplayTemplate<T>() {
			@Override
			public String getLabel(T arg0, OgemaLocale locale) {
				return MultiSelectExtendedStringArray.this.getLabel(arg0, locale);
			}
		});
		
		this.resource = resource;
		
		initSnippet(page, null, id, multiLoc, null);
	}
	
}
