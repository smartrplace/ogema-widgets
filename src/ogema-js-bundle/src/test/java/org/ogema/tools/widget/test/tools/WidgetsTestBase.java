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
package org.ogema.tools.widget.test.tools;

import org.ogema.exam.OsgiAppTestBase;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;

public abstract class WidgetsTestBase extends OsgiAppTestBase {
	
	public final String widgetsVersion = MavenUtils.asInProject().getVersion("org.ogema.widgets", "ogema-js-bundle");
	
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
		return CoreOptions.composite(CoreOptions.mavenBundle("org.ogema.widgets", "ogema-gui-api", widgetsVersion));
	}
	
}
