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
/**
 * This package contains a set of widgets for 
 * {@link org.ogema.core.resourcemanager.pattern.ContextSensitivePattern ContextSensitivePatterns},
 * whose context can be initialized via url parameters of the form
 * <code>?key1=value1a,value1b&amp;key2=value2&amp;key3=...</code>
 * The parameters can be set for instance via a 
 * {@link de.iwes.widgets.html.form.button.RedirectButton RedirectButton}.
 * Hence, the context of the widget can be set from another page, which links
 * to the page containing this widget.
 * 
 * The context class must have a default constructor for this to work. 
 */
package de.iwes.widgets.pattern.widget.init.context;