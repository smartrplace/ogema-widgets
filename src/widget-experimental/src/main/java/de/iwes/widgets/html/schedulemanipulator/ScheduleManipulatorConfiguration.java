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
