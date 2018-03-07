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

package de.iwes.widgets.api.extended.pattern;

import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A widget that displays all patterns of type <code>P</code>; the pattern type is the same 
 * for all users, but the context may differ, so that different users may see different pattern matches.
 * @see ContextSensitivePattern
 *
 * @param <P> pattern type
 * @param <C> context type
 */
public interface ContextPatternWidget<P extends ContextSensitivePattern<?, C>,C> extends PatternWidget<P> {
	
	/**
	 * Get the pattern context for the specified session/request.
	 * @see ContextSensitivePattern
	 * @param req
	 * @return
	 */
	C getContext(OgemaHttpRequest req);
	
	/**
	 * @see #getContext(OgemaHttpRequest)
	 * @param context
	 * @param req
	 */
	void setContext(C context, OgemaHttpRequest req);
	
	/**
	 * @see #getContext(OgemaHttpRequest)
	 * @param context
	 */
	void setDefaultContext(C context);

}
