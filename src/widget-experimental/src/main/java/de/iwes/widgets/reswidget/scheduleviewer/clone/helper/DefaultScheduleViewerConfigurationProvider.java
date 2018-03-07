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
package de.iwes.widgets.reswidget.scheduleviewer.clone.helper;

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
