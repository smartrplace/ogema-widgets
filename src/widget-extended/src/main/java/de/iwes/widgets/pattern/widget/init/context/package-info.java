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

/**
 * This package contains a set of widgets for 
 * {@link org.ogema.core.resourcemanager.pattern.ContextSensitivePattern ContextSensitivePatterns},
 * whose context can be initialized via url parameters of the form
 * <code>?key1=value1a,value1b&key2=value2&key3=...</code>
 * The parameters can be set for instance via a 
 * {@link de.iwes.widgets.html.form.button.RedirectButton RedirectButton}.
 * Hence, the context of the widget can be set from another page, which links
 * to the page containing this widget.
 * 
 * The context class must have a default constructor for this to work. 
 */
package de.iwes.widgets.pattern.widget.init.context;