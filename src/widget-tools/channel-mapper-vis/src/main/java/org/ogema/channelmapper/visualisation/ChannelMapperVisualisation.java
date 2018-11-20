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
package org.ogema.channelmapper.visualisation;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.channelmapperv2.config.ChannelMapperConfiguration;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.ResourceList;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.navigation.NavigationMenu;
import de.iwes.widgets.pattern.widget.patternedit.PatternCreatorConfiguration;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageUtil;

@Component(immediate=true)
@Service(Application.class)
public class ChannelMapperVisualisation implements Application {
	
	@Reference
	private OgemaGuiService widgetService;
	
	private WidgetApp wapp;

	@Override
	public void start(ApplicationManager appManager) {
		@SuppressWarnings("unchecked")
		ResourceList<ChannelMapperConfiguration> configs  = appManager.getResourceManagement().createResource("channelMapperConfigurations", ResourceList.class);
		configs.setElementType(ChannelMapperConfiguration.class);
		wapp = widgetService.createWidgetApp("/de/iwes/drivers/channelmapper/visusalisation", appManager);
		
		PatternPageUtil ppu = PatternPageUtil.getInstance(appManager, wapp);
		PatternCreatorConfiguration<ChannelMapperConfigVisPattern, ChannelMapperConfiguration> config = new PatternCreatorConfiguration<>(configs); 
		WidgetPage<?> creator = ppu.newPatternCreatorPage(ChannelMapperConfigVisPattern.class, "configCreator.html", true, config, null).getPage();
		WidgetPage<?> editor = ppu.newPatternEditorPage(ChannelMapperConfigVisPattern.class, "configEditor.html").getPage();
		
		NavigationMenu menu = new NavigationMenu(" Select page");
		menu.addEntry("Mapping creation", creator);
		menu.addEntry("Mapping editing", editor);
		creator.getMenuConfiguration().setCustomNavigation(menu);
		editor.getMenuConfiguration().setCustomNavigation(menu);
	}

	@Override
	public void stop(AppStopReason reason) {
		if (wapp != null)
			wapp.close();
		wapp = null;
	}


	
	
}
