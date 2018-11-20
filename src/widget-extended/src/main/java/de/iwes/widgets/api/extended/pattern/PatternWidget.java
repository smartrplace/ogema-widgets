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
