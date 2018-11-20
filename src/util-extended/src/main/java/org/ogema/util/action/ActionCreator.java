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

import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.model.action.Action;

import de.iwes.util.resource.ValueResourceHelper;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.LoggerFactory;

/**Class creates action if it does not yet exist and listens for an action. We assume that this is only
 * called by the controlling application. The object has to be created each time the application is
 * started independently whether the action resource already exists.
 * @param <UNUSEDGENERICPARAMETER>
 */
public abstract class ActionCreator<UNUSEDGENERICPARAMETER extends Action> {
	protected abstract void performAction();
	
	public BooleanResource stateControl;
	public BooleanResource stateFeedback;

	public ResourceValueListener<BooleanResource> controlListener;
	public OgemaLogger log = null;
	
	protected ActionCreator(Action action, String controllingApplication) {
		this(action, controllingApplication, null);
	}
	/** Provide an Action.
	 * 
	 * @param action to be created/used. Provide this as a (potenitially) virtual resource,
	 * 		the ActionCreator will take care of creation/activation if necessary
	 * @param controllingApplication id of controlling application to be used for action
	 * @param logger if null no logging will be performed
	 */
	protected ActionCreator(Action action, String controllingApplication, OgemaLogger logger) {
		this.log = logger;
		if(ValueResourceHelper.setIfNew(action.stateControl(), false) |
				ValueResourceHelper.setIfNew(action.stateFeedback(), false) |
				ValueResourceHelper.setIfNew(action.controllingApplication(), controllingApplication)) {
			action.activate(true);
		}
        stateControl = action.stateControl();
        stateFeedback = action.stateFeedback();
        final AtomicBoolean actionActive = new AtomicBoolean();
        
        controlListener = new ResourceValueListener<BooleanResource>() {
            @Override
            public void resourceChanged(BooleanResource arg0) {
                if (arg0.getValue() && !actionActive.getAndSet(true)) {
                    stateFeedback.setValue(true);
                    try {
                        performAction();
                    } catch (Exception e) {
                        if (log != null) {
                            log.error("Exception in action:", e);
                        } else {
                            LoggerFactory.getLogger(getClass()).error("Exception in action:", e);
                        }
                    } finally {
                        stateFeedback.setValue(false);
                        arg0.setValue(false);
                        actionActive.set(false);
                    }
                }
            }
        };
        stateControl.addValueListener(controlListener, true);
	}
	
	public void close() {
		stateControl.removeValueListener(controlListener);
	}
    
    public static void registerAction(Action model, Callable<Void> action, String controllingApplication, OgemaLogger logger) {
		if(ValueResourceHelper.setIfNew(model.stateControl(), false) |
				ValueResourceHelper.setIfNew(model.stateFeedback(), false) |
				ValueResourceHelper.setIfNew(model.controllingApplication(), controllingApplication)) {
			model.activate(true);
		}
        final BooleanResource stateControl = model.stateControl();
        final BooleanResource stateFeedback = model.stateFeedback();
        final AtomicBoolean actionActive = new AtomicBoolean();
        final ResourceValueListener<BooleanResource> controlListener = new ResourceValueListener<BooleanResource>() {
            @Override
            public void resourceChanged(BooleanResource arg0) {
                if (arg0.getValue() && !actionActive.getAndSet(true)) {
                    stateFeedback.setValue(true);
                    try {
                        action.call();
                    } catch (Exception e) {
                        if (logger != null) {
                            logger.error("Exception in action:", e);
                        } else {
                            LoggerFactory.getLogger(getClass()).error("Exception in action:", e);
                        }
                    } finally {
                        stateFeedback.setValue(false);
                        arg0.setValue(false);
                        actionActive.set(false);
                    }
                }
            }
        };
        stateControl.addValueListener(controlListener, true);
    }
    
}
