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

package org.ogema.apps.message.reader.dictionary;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class MessagesDictionary_de extends MessagesDictionary {

	@Override
	public OgemaLocale getLocale() {
		return OgemaLocale.GERMAN;
	}
	
	@Override
	public String getTitle() {
		return "OGEMA Nachrichten";
	}
	
	@Override
	public String getColTitleAbstract() {
		return "Nachricht";
	}
	
	@Override
	public String getColTitleFull() {
		return "Text";
	}
	
	@Override
	public String getColTitlePrio() {
		return "Priorit�t";
	}
	
	@Override
	public String getColTitleApp() {
		return "Sendende App";
	}

	@Override
	public String getColTitleTime() {
		return "Zeit";
	}

}
