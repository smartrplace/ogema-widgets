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

import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.plus.TemplateWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A widget that displays all patterns of type <code>P</code>. 
 *
 * @param <P> pattern type
 */
public interface PatternWidget<P extends ResourcePattern<?>> extends TemplateWidget<P> {
	
//	/**
//	 * This method should only be called directly if this widget has update mode {@link UpdateMode#MANUAL}.
//	 * @param resource
//	 * @return
//	 * @throws UnsupportedOperationException if the widget does not have update mode MANUAL.
//	 */
//	boolean addItem(P pattern, OgemaHttpRequest req) throws UnsupportedOperationException;
//	
//	/**
//	 * This method should only be called directly if this widget has update mode {@link UpdateMode#MANUAL}.
//	 * @param resource
//	 * @return
//	 * @throws UnsupportedOperationException if the widget does not have update mode MANUAL.
//	 */
//	boolean removeItem(P pattern, OgemaHttpRequest req) throws UnsupportedOperationException;
//
//	/**
//	 * Get the current patterns for the specified request/user session.
//	 * If P is not a {@see ContextSensitivePattern}, then this should simply return all
//	 * patterns of type P, independently of the request, and the widget should be global 
//	 * (in this case one can pass null as argument). <br>
//	 * If P is context sensitive, then each request can be associated with a specific context,
//	 * and the resulting list really depends on the request. This is usually the case for 
//	 * {@link ContextPatternWidget}s.
//	 * 
//	 * @param req
//	 * @return
//	 */
//	List<P> getItems(OgemaHttpRequest req);
	
	UpdateMode getUpdateMode();
	
	void setType(Class<? extends P> type, OgemaHttpRequest req);

	Class<? extends P> getType(OgemaHttpRequest req);
	

}
