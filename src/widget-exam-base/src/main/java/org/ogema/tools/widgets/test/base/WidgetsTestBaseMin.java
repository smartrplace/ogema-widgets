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

package org.ogema.tools.widgets.test.base;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.junit.Before;
import org.ogema.exam.OsgiAppTestBase;
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

public abstract class WidgetsTestBaseMin extends OsgiAppTestBase {

	public final String widgetsVersion = MavenUtils.asInProject().getVersion("de.iwes.widgets", "ogema-js-bundle");
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

	@Override
	public Option[] frameworkBundles() {
		Option[] opt = super.frameworkBundles();
		Option[] options = new Option[opt.length + 3];
		for (int i =0;i<opt.length;i++) {
			options[i] = opt[i];
		}
		options[opt.length] = widgets();
		options[opt.length+1] = webConsoleOption();
		options[opt.length+2] = felixGogoShellOption();
		return options;
	}

	// TODO options for name and icon service, and messaging
	public Option widgets() {
		return CoreOptions.composite(CoreOptions.mavenBundle("de.iwes.widgets", "ogema-gui-api", widgetsVersion),
									 CoreOptions.mavenBundle("de.iwes.widgets", "ogema-js-bundle", widgetsVersion),
									 CoreOptions.mavenBundle("de.iwes.widgets", "widget-collection", widgetsVersion),
									 CoreOptions.mavenBundle("org.ogema.tools", "resource-manipulators", ogemaVersion), // dependency of util-extended
									 CoreOptions.mavenBundle("de.iwes.widgets", "widget-extended", widgetsVersion),
									 CoreOptions.mavenBundle("de.iwes.widgets", "widget-experimental", widgetsVersion),
									 CoreOptions.mavenBundle("de.iwes.widgets", "widget-exam-base", widgetsVersion),
//									 CoreOptions.mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpcore-osgi").versionAsInProject(), // fails
//									 CoreOptions.mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpclient-osgi").versionAsInProject(),
//									 CoreOptions.mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpasyncclient-osgi").versionAsInProject(),
									 CoreOptions.mavenBundle().groupId("org.apache.commons").artifactId("commons-lang3").version("3.5"),
									 CoreOptions.mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpcore-osgi").version("4.4.5"),
									 CoreOptions.mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpclient-osgi").version("4.5.2"),
									 CoreOptions.mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpasyncclient-osgi").version("4.1.2"),
									 CoreOptions.mavenBundle("commons-logging", "commons-logging", "1.1.3"), // needed by apache components
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
