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

package org.ogema.tools.widget.test.tools;

import org.ogema.exam.OsgiAppTestBase;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;

public abstract class WidgetsTestBase extends OsgiAppTestBase {
	
	public final String widgetsVersion = MavenUtils.asInProject().getVersion("de.iwes.widgets", "ogema-js-bundle");
	
	public WidgetsTestBase() {
		super(true);
	}

	@Override
	public Option[] frameworkBundles() {
		Option[] opt = super.frameworkBundles();
		Option[] options = new Option[opt.length + 3];
		for (int i =0;i<opt.length;i++) {
			options[i] = opt[i];
		}
		options[opt.length] = widgets(); // this is added anyways
		options[opt.length+1] = webConsoleOption(); // FIXME org.json version problems?
		options[opt.length+2] = felixGogoShellOption();
		return options;
	}
	
	public Option widgets() {
//		return CoreOptions.composite(CoreOptions.mavenBundle("org.ogema.widgets", "ogema-js-bundle", widgetsVersion));
		return CoreOptions.composite(CoreOptions.mavenBundle("de.iwes.widgets", "ogema-gui-api", widgetsVersion));
	}
	
}
