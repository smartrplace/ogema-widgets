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
package de.iee.sema.remote.user.administration;

import org.ogema.core.application.ApplicationManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;

@Component(
		service = LazyWidgetPage.class, 
		property = {
			LazyWidgetPage.BASE_URL + "=/de/iee/ogema/apps/remoteuseradministration",
			LazyWidgetPage.RELATIVE_URL + "=create.html", 
			LazyWidgetPage.START_PAGE + "=true" 
		}
)
public class RemoteUserAdministration implements LazyWidgetPage {

	@Reference
	private ConditionalPermissionAdmin cpa;

	@Override
	public void init(final ApplicationManager appMan, final WidgetPage<?> page) {
		new CreatePageInit(page, appMan, cpa);
	}

}
