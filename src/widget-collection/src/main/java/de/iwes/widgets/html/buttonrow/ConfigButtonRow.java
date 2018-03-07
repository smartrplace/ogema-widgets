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
