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
package de.iwes.widgets.reveal.base;

import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.osgi.service.component.annotations.Component;

@Component(service=Application.class)
public class ResourceRegistration implements Application {

	private static final String RESOURCE_PATH = "reveal.js";
	public static final String BROWSER_PATH = "/ogema/widgets/reveal";
	
	private ApplicationManager appMan;
	
	@Override
	public void start(ApplicationManager appManager) {
		this.appMan = appManager;
		try {
			appManager.getWebAccessManager().registerWebResource(BROWSER_PATH, RESOURCE_PATH);
		} catch (Exception e) {
			appManager.getWebAccessManager().unregisterWebResource(BROWSER_PATH);
			appManager.getWebAccessManager().registerWebResource(BROWSER_PATH, RESOURCE_PATH);
		} finally {
			appManager.getWebAccessManager().registerStartUrl(null);
		}
	}

	@Override
	public void stop(AppStopReason reason) {
		final ApplicationManager appMan = this.appMan;
		this.appMan = null;
		appMan.getWebAccessManager().unregisterWebResource(BROWSER_PATH);
	}

}
