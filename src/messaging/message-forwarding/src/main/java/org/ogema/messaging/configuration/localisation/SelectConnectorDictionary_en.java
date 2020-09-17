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
		if(Boolean.getBoolean("org.ogema.messaging.basic.services.config.fixconfigenglish"))
			return "When priority 'LOW' is chosen for a certain service on an app all messages "
			+ "of this app will be sent via the service; when 'HIGH' is chosen only messages "
			+ "with highest priority are sent etc."	;
		return "This page allows you to configure which messages sent by which app are forwarded "
				+ "to which receivers, via Email, SMS, XMPP, etc. Further messaging service providers can "
				+ "be installed later on.\n"
				+ "In order that a receiver can be selected here, it first has to be configured for "
				+ "the respecting messaging service, for which purpose the latter must provide its own "
				+ "user page. For the basic services the page can be found <a href=\"" + MESSAGE_SETTINGS_LINK + "\"><b>here</b></a>.\n"
				+ "Messages can also be viewed in the browser, via the <a href=\"" + MESSAGE_READER_LINK + "\">"
				+ "<b>Message Reader</b></a><br>"
				+ "When priority 'LOW' is chosen for a certain service on an app all messages "
				+ "of this app will be sent via the service; when 'HIGH' is chosen only messages "
				+ "with highest priority are sent etc."	;
	}

	
}
