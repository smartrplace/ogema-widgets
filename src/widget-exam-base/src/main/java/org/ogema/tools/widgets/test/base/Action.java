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
package org.ogema.tools.widgets.test.base;

import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;

public class Action {

	public final String widgetId1;
	public final String widgetId2; // could be a groupId as well
	public final TriggeringAction trigger;
	public final TriggeredAction triggered;
	
	public Action(String widgetId1,String widgetId2, TriggeringAction trigger,TriggeredAction triggered) {
		this.widgetId1 = widgetId1;
		this.widgetId2 = widgetId2;
		this.trigger = trigger;
		this.triggered = triggered;
	}
	
	
}
