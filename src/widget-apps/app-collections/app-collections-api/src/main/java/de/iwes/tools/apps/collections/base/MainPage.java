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
package de.iwes.tools.apps.collections.base;

import org.ogema.core.administration.AdminApplication;

import de.iwes.tools.apps.collections.api.DisplayableApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.appbox.AppBox;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;

public class MainPage<A extends DisplayableApp> {

	private final WidgetPage<?> page;
	private final AppCollection<A> app;
	private final Header header;
	private final AppBox apps;
	
	public MainPage(final WidgetPage<?> page, final AppCollection<A> app) {
		this.page = page;
		this.app = app;
		this.header = new Header(page, "header", true);
		header.setDefaultText(app.pageTitle());
		header.addDefaultStyle(HeaderData.CENTERED);
		header.setDefaultColor("blue");
		
		this.apps = new AppBox(page, "appBox", true, null) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				// TODO caching... do not update on every request
				apps.setAdminApps(app.getApps(), req);
			}
			
			@Override
			protected String getDescription(AdminApplication app) {
				final String sn = AppCollection.getSymbolicName(app);
				if (sn == null)
					return null;
				final String descr = MainPage.this.app.staticApps().get(sn);
				if (descr != null)
					return descr;
				final A aa = MainPage.this.app.registeredApps.get(sn);
				if (aa != null)
					return aa.description();
				return null;
			}
			
		};
		apps.setDefaultAdminApps(app.getApps());
		buildPage();
	}
	
	private final void buildPage() {
		page.append(header).linebreak().append(apps);
		
	}
	
}
