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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ogema.core.model.array.StringArrayResource;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.template.DefaultDisplayTemplate;

/** See also {@link MultiSelectExtendedStringArray}. This requires to provide options that can be in the
 * StringArrayResource, whereas this widget allows the user to add arbitrary Strings.
 *
 */
public abstract class MultiSelectStringArrayFreeText {
	public final TemplateMultiselect<String> multiSelect;
	public final Button submit;
	public final TextField newValue;

	protected abstract StringArrayResource getStringArrayResource(OgemaHttpRequest req);
	
	public MultiSelectStringArrayFreeText(OgemaWidget parent, String id, OgemaHttpRequest req) {
		multiSelect = new TemplateMultiselect<String>(parent, id+"Multi", req) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				StringArrayResource appLoc = getStringArrayResource(req); //initAdminData.getSelectedItem(req);
				if((appLoc != null)&&(appLoc.isActive())) {
					List<String> items = Arrays.asList(appLoc.getValues());
					//update(items, req);
					selectItems(items, req);
				}
				//else update(Collections.emptyList(), req);
			}
		};
		multiSelect.setTemplate(new DefaultDisplayTemplate<String>());
		//multiSelect.setDefaultWidth("600px");
		StringArrayResource appLoc = getStringArrayResource(req);
		if(appLoc != null) {
			//List<String> gws = new ArrayList<>();
			//for(Gateway gw: gatewaysToOffer) {
			//	gws.add(gw.agentId().getValue());
			//}
			multiSelect.update(Arrays.asList(appLoc.getValues()), req);			
		}
		
		newValue = new TextField(parent, id+"newValue", req);
		newValue.setDefaultPlaceholder("Enter value to add here");
		
		submit = new Button(parent, id+"submit", "Submit/Add value:", req) {
			private static final long serialVersionUID = 1L;
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				StringArrayResource appLoc = getStringArrayResource(req); //initAdminData.getSelectedItem(req);
				if(appLoc == null) return;
				List<String> out = multiSelect.getSelectedItems(req);
				String newVal = newValue.getValue(req);
				if((newVal != null)&&(!newVal.equals(""))) {
					out.add(newVal);
				}
				appLoc.setValues(out.toArray(new String[0]));
			}
		};
		submit.addWidget(multiSelect);
		submit.addWidget(newValue);
		submit.registerDependentWidget(multiSelect);
	}
	
	public MultiSelectStringArrayFreeText(WidgetPage<?> page, String id) {
		multiSelect = new TemplateMultiselect<String>(page, id+"Multi") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				StringArrayResource appLoc = getStringArrayResource(req); //initAdminData.getSelectedItem(req);
				if((appLoc != null)&&(appLoc.isActive())) {
					List<String> items = Arrays.asList(appLoc.getValues());
					if(getSelectedItems(req).isEmpty())
						update(items, req);
					selectItems(items, req);
				}
				else update(Collections.emptyList(), req);
			}
		};
		multiSelect.setTemplate(new DefaultDisplayTemplate<String>());
		//multiSelect.setDefaultWidth("600px");
		
		newValue = new TextField(page, id+"newValue");
		newValue.setDefaultPlaceholder("Enter value to add here");
		
		submit = new Button(page, id+"submit", "Submit/Add value:") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				StringArrayResource appLoc = getStringArrayResource(req); //initAdminData.getSelectedItem(req);
				if(appLoc == null) return;
				List<String> out = multiSelect.getSelectedItems(req);
				String newVal = newValue.getValue(req);
				if((newVal != null)&&(!newVal.equals(""))) {
					out.add(newVal);
					newValue.setValue("", req);
				}
				boolean doCreate = false;
				if(!out.isEmpty() && (!appLoc.exists())) {
					appLoc.create();
					doCreate = true;
				}
				appLoc.setValues(out.toArray(new String[0]));
				if(doCreate) appLoc.activate(true);
			}
		};
		submit.addWidget(multiSelect);
		submit.addWidget(newValue);
		submit.registerDependentWidget(multiSelect);
		submit.registerDependentWidget(newValue);
	}

}
