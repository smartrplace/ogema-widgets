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
package de.iwes.widgets.resource.widget.arrays;

import org.ogema.core.model.array.ArrayResource;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.arrays.ArrayManipulatorConfiguration;
import de.iwes.widgets.html.arrays.ArrayManipulatorConfigurationBuilder;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.FlexboxData;
import de.iwes.widgets.html.html5.flexbox.FlexDirection;

public class ResourceArrayManipulator<A extends ArrayResource> extends Flexbox {

	private static final long serialVersionUID = 1L;
	private final ArrayManipulatorConfiguration config;
	private final Class<A> type;
	
	public ResourceArrayManipulator(WidgetPage<?> page, String id, boolean globalWidget, Class<A> type) {
		this(page, id, globalWidget, type, null);
	}
	
	public ResourceArrayManipulator(WidgetPage<?> page, String id, boolean globalWidget, Class<A> type,
			ArrayManipulatorConfiguration config) {
		super(page, id, globalWidget);
		this.type = type;
		this.config = config != null ? config : 
			ArrayManipulatorConfigurationBuilder.newInstance().build();
		setDefaultFlexDirection(FlexDirection.COLUMN);
	}

	public ResourceArrayManipulator(OgemaWidget parent, String id, OgemaHttpRequest req, Class<A> type) {
		this(parent, id, req, type, null);
	}
	
	public ResourceArrayManipulator(OgemaWidget parent, String id, OgemaHttpRequest req, Class<A> type, 
			ArrayManipulatorConfiguration config) {
		super(parent, id, req);
		this.type = type;
		this.config = config != null ? config : 
			ArrayManipulatorConfigurationBuilder.newInstance().build();
		setDefaultFlexDirection(FlexDirection.COLUMN);
	}
	
	@Override
	public FlexboxData createNewSession() {
		return new ResourceArrayManipulatorData<>(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResourceArrayManipulatorData<A> getData(OgemaHttpRequest req) {
		return (ResourceArrayManipulatorData<A>) super.getData(req);
	}
	
	public void setResource(A resource, OgemaHttpRequest req) {
		getData(req).setResource(resource, req);
	}
	
	public A getResource(OgemaHttpRequest req) {
		return getData(req).getResource();
	}
	
	public Class<A> getType() {
		return type;
	}
	
	public ArrayManipulatorConfiguration getConfig() {
		return config;
	}
	
}
