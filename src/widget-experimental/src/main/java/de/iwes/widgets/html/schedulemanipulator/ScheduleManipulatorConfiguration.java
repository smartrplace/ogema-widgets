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
package de.iwes.widgets.html.schedulemanipulator;

import de.iwes.widgets.html.alert.Alert;

public class ScheduleManipulatorConfiguration {

	private final Alert alert;
	private final boolean showInterpolationMode, showQuality;
	
	public ScheduleManipulatorConfiguration() {
		this(null);
	}
	
	public ScheduleManipulatorConfiguration(Alert alert) {
		this(alert, true, false);
	}
	
	public ScheduleManipulatorConfiguration(Alert alert, boolean showInterpolationMode, boolean showQuality) {
		this.alert = alert;
		this.showInterpolationMode = showInterpolationMode;
		this.showQuality = showQuality;
	}
	
	public Alert getAlert() {
		return alert;
	}

	public boolean isShowInterpolationMode() {
		return showInterpolationMode;
	}

	public boolean isShowQuality() {
		return showQuality;
	}
	
}
