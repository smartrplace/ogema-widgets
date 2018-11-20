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
package de.iwes.widgets.html.buttonrow;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;

/**Widget that presents three buttons:
 * OK: Causes a POST event and closes the Tab/Window
 * Cancel: Only closes the Tab/Window
 * Save: Only sends POST event
 * 
 * TODO: Currently it is not possible to exchange the writing on the buttons
 * @author dnestle
 *
 */
public class ConfigButtonRow extends OgemaWidgetBase<ConfigButtonRowData> {
	private static final long serialVersionUID = -1687042308795879835L;
	
	/*********** Constructors **********/
	
	public ConfigButtonRow(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	/******* Inherited methods ******/

	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return ConfigButtonRow.class;
	}
	
	@Override
	public ConfigButtonRowData createNewSession() {
		return new ConfigButtonRowData(this);
	}
	
	// non-default package name
    protected void registerJsDependencies() {
    	Class<? extends OgemaWidgetBase<?>> clazz = getWidgetClass();
    	String className = clazz.getSimpleName();
    	String guessUrl = "/ogema/widget/buttonrow/" + className + ".js";
    	this.registerLibrary(true, className, guessUrl);
    }
	
	/********** Public methods **********/
	

}
