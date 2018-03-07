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

package org.ogema.messaging.configuration.localisation;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class SelectConnectorDictionary_en implements SelectConnectorDictionary {

	@Override
	public OgemaLocale getLocale() {
		return OgemaLocale.ENGLISH;
	}

	@Override
	public String header() {
		return "Message forwarding configuration";
	}

	@Override
	public String description() {
		return "This page allows you to configure which messages sent by which OGEMA apps are forwarded "
				+ "to which receivers, via Email, SMS, XMPP, etc. Further messaging service providers can "
				+ "be installed later on.\n"
				+ "In order that a receiver can be selected here, it first has to be configured for "
				+ "the respecting messaging service, for which purpose the latter must provide its own "
				+ "user page. For the basic services the page can be found <a href=\"" + MESSAGE_SETTINGS_LINK + "\"><b>here</b></a>.\n"
				+ "Messages can also be viewed in the browser, via the <a href=\"" + MESSAGE_READER_LINK + "\">"
				+ "<b>OGEMA message reader</b></a><br>"
				+ "When priority 'LOW' is chosen for a certain service on an app all messages "
				+ "of this app will be sent via the service; when 'HIGH' is chosen only messages "
				+ "with highest priority are sent etc."	;
	}

	
}
