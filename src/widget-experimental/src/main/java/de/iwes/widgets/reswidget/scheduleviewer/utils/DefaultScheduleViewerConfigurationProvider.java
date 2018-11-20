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
package de.iwes.widgets.reswidget.scheduleviewer.utils;

import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationProvider;

public class DefaultScheduleViewerConfigurationProvider implements ScheduleViewerConfigurationProvider{
	public final static DefaultScheduleViewerConfigurationProvider DEFAULT_SCHEDULEVIEWER_CONFIGURATION_PROVIDER = new DefaultScheduleViewerConfigurationProvider();
	
	@Override
	public String getConfigurationProviderId() {
		return "DefaultSessionConfiguration"; 
	}

	@Override
	public SessionConfiguration getSessionConfiguration(String configurationId) {
		return DefaultSessionConfiguration.DEFAULT_SESSION_CONFIGURATION;
	}

	@Override
	public void saveCurrentConfiguration(SelectionConfiguration currentConfiguration, String configurationId) {

	}

}
