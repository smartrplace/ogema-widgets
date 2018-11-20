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

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.junit.Before;
import org.ogema.exam.latest.LatestVersionsTestBase;
import org.ogema.tools.widgets.test.base.widgets.TestButton;
import org.ogema.tools.widgets.test.base.widgets.TestDropdown;
import org.ogema.tools.widgets.test.base.widgets.TestLabel;
import org.ogema.tools.widgets.test.base.widgets.TestTable;
import org.ogema.tools.widgets.test.base.widgets.TestTextField;
import org.ogema.tools.widgets.test.base.widgets.TestWidgetsFactory;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.Bundle;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.extended.WidgetAdminService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;

public abstract class WidgetsTestBaseMin extends LatestVersionsTestBase {

	/**
	 * Use {@link #getWidgetsVersion()} instead.
	 */
	@Deprecated
	public final String widgetsVersion = MavenUtils.asInProject().getVersion("org.ogema.widgets", "ogema-js-bundle");
	@Inject
	protected OgemaGuiService guiService;
	@Inject
	protected WidgetAdminService adminService;
	protected WidgetApp widgetApp;

	public WidgetsTestBaseMin(boolean includeTestBundle) {
		super(includeTestBundle);
	}

	public WidgetsTestBaseMin() {
		this(true);
	}
	
	protected String getWidgetsVersion() {
		return MavenUtils.asInProject().getVersion("org.ogema.widgets", "ogema-js-bundle");
	}

	@Override
	public Option[] frameworkBundles() {
		Option[] opt = super.frameworkBundles();
		Option[] options = new Option[opt.length + 1];
		for (int i =0;i<opt.length;i++) {
			options[i] = opt[i];
		}
		options[opt.length] = widgets();
//		options[opt.length+1] = webConsoleOption();
//		options[opt.length+1] = felixGogoShellOption();
		return options;
	}

	// TODO options for name and icon service, and messaging
	public Option widgets() {
		final String widgetsVersion = getWidgetsVersion();
		return CoreOptions.composite(CoreOptions.mavenBundle("org.ogema.widgets", "ogema-gui-api", widgetsVersion),
									 CoreOptions.mavenBundle("org.ogema.widgets", "ogema-js-bundle", widgetsVersion),
									 CoreOptions.mavenBundle("org.ogema.widgets", "widget-collection", widgetsVersion),
									 CoreOptions.mavenBundle("org.ogema.tools", "resource-manipulators", ogemaVersion), // dependency of util-extended
									 CoreOptions.mavenBundle("org.ogema.widgets", "widget-extended", widgetsVersion),
									 CoreOptions.mavenBundle("org.ogema.widgets", "widget-experimental", widgetsVersion),
									 CoreOptions.mavenBundle("org.ogema.widgets", "widget-exam-base", widgetsVersion),
//									 CoreOptions.mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpcore-osgi").versionAsInProject(), // fails
//									 CoreOptions.mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpclient-osgi").versionAsInProject(),
//									 CoreOptions.mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpasyncclient-osgi").versionAsInProject(),
									 CoreOptions.mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpcore-osgi").version("4.4.5"),
									 CoreOptions.mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpclient-osgi").version("4.5.2"),
									 CoreOptions.mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpasyncclient-osgi").version("4.1.2"),
									 CoreOptions.mavenBundle("commons-logging", "commons-logging", "1.1.3"), // needed by apache components
									 CoreOptions.mavenBundle("commons-fileupload", "commons-fileupload", "1.3.3"), // needed by apache components
									 CoreOptions.mavenBundle("joda-time", "joda-time", "2.9.3"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	@Override
	public void before() throws InterruptedException {
		super.before();
		TestWidgetsFactory.registerType(Button.class, TestButton.class);
		TestWidgetsFactory.registerType((Class) DynamicTable.class, TestTable.class);
		TestWidgetsFactory.registerType(Dropdown.class, TestDropdown.class);
		TestWidgetsFactory.registerType(Label.class, TestLabel.class);
		TestWidgetsFactory.registerType(TextField.class, TestTextField.class);

		widgetApp = guiService.createWidgetApp("/widgets/test/url", getApplicationManager());
	}

	@Override
	public void doAfter() {
		widgetApp.close();
	}


	private final AtomicInteger counter = new AtomicInteger(1);

	protected String nextUrl() {
		String url = counter.getAndIncrement() + ".html";
		return url;
	}

	protected Bundle findBundle(String groupId, String artifactId) {
		final String wanted = groupId + "." + artifactId;
		String symName;
		for (Bundle b : ctx.getBundles()) {
			symName = b.getSymbolicName();
			if (wanted.equals(symName))
				return b;
		}
		return null;
	}

}
