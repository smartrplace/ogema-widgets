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
package de.iwes.timeseries.eval.viewer.impl.profile;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileSchedulePresentationData;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;

public class PresentationDataImpl extends DefaultSchedulePresentationData implements ProfileSchedulePresentationData {

	private final Profile profile;
	private final boolean online;
	
	public PresentationDataImpl(ReadOnlyTimeSeries schedule, Class<?> type, String label, InterpolationMode mode, Profile profile) {
		super(schedule, type, label, mode);
		this.profile = profile;
		this.online = rots instanceof OnlineTimeSeries;
	}
	
	public PresentationDataImpl(ReadOnlyTimeSeries schedule, Class<?> type, String label, InterpolationMode mode, Profile profile, boolean online) {
		super(schedule, type, label, mode);
		this.profile = profile;
		this.online = online;
	}
	
	/**
	 * May be null
	 */
	@Override
	public Profile getProfile() {
		return profile;
	}
	
	@Override
	public boolean isOnlineTimeSeries() {
		return online;
	}

}
