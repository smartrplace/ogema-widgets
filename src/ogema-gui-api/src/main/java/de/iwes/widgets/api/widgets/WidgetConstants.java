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
package de.iwes.widgets.api.widgets;

public class WidgetConstants {

	/**
	 * Namespace for widget capabilities.
	 */
	public static final String PAGES_WHITEBOARD_NAMESPACE = "ogema.widgets.extender";
	public static final String LAZY_PAGES_WHITEBOARD_CAPABILITY = "pages.whiteboard";
	public static final String LAZY_PAGES_WHITEBOARD_VERSION = "1.0.0";
	
	/**
	 * Get a requirement statement for this lazy pages whiteboard version.
	 * Example pom entry for maven-bundle-plugin:
	 * <code>
	 * 		&lt;Require-Capability&gt;
            		ogema.widgets.extender; filter:="(&amp;(ogema.widgets.extender=pages.whiteboard)(version=1.0))"
            &lt;/Require-Capability&gt;
	 * </code>
	 * or
	 * <code>
	 * 		&lt;Require-Capability&gt;
            		ogema.widgets.extender; filter:="(&amp;(ogema.widgets.extender=pages.whiteboard)(version&gt;=1.0)(!(version&gt;=2.0)))"
            &lt;/Require-Capability&gt;
	 * </code>
	 * @return
	 */
	public static final String getLazyPagesWhiteboardFilter() {
		return PAGES_WHITEBOARD_NAMESPACE + "; filter:=\"" + 
				"(&(" + PAGES_WHITEBOARD_NAMESPACE + "=" + LAZY_PAGES_WHITEBOARD_CAPABILITY + ")" + 
				"(version=" + LAZY_PAGES_WHITEBOARD_VERSION + ")";
	}
	
	
}
