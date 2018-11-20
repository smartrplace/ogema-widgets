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
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS
 * Fraunhofer ISE
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.model.action;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

/** Intended to be provided by an application to offer some kind of action */
public interface Action extends Data {
	/** Application that can execute the action*/
	StringResource controllingApplication();
	
	/**Description that may take up to several lines that may be shown in a mouse-over pop-up frame
	 * or similar*/
	StringResource description();
	/**Short name to be used in the user interface*/
	@Override
	StringResource name();
	
	/**Write true to start action and false to stop action (if supported). If not supported
	 * or action already started/stopped the write operation shall be irrelevant. When the action
	 * is finished and not automatically restarted the application shall set stateControl to false.*/
	BooleanResource stateControl();
	/**Feedback from controlling application whether action is active or not. This may not
	 * always be supported*/
	BooleanResource stateFeedback();
	
	/**If true the action can be started from outside by writing into stateControl*/
	BooleanResource starteable();
	/**If true the action can be stopped from outside by writing into stateControl*/
	BooleanResource stopeable();
}
