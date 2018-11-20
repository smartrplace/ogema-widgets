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
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.util.action;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.model.action.Action;

/**Pattern to manage a custom model or a resource containing an action named "run". Other assumptions as in
 * {@link ActionPattern}.
 */
public abstract class SubActionPattern<T extends Resource, C> extends ContextSensitivePattern<T, C> {
	/**Override this to provide application if of the Action*/
	protected abstract String getControllingApplication();
	/**Override this. This method is called when the Action shall be executed*/
	protected abstract void performAction();
	
	public Action action = model.getSubResource("run", Action.class);
	public StringResource controllingApp = action.controllingApplication();
	public BooleanResource stateControl = action.stateControl();

	boolean actionActive = false;
	boolean init = false;
	public ResourceValueListener<BooleanResource> controlListener = null;
	
	protected SubActionPattern(Resource match) {
		super(match);
	}

	@Override
	public boolean accept() {
		
		if(!controllingApp.getValue().equals(getControllingApplication())) return false;
		if(controlListener == null) {
			controlListener = new ResourceValueListener<BooleanResource>() {
				@Override
				public void resourceChanged(BooleanResource arg0) {
					if((!actionActive) && arg0.getValue()) {
						actionActive = true;
						performAction();
						stateControl.setValue(false);
						actionActive = false;
					}
				}
			};
			stateControl.addValueListener(controlListener, true);
		}
		init = true;
		return super.accept();
	}
}
