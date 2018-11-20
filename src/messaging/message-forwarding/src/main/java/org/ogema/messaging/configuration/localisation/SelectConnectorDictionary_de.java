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

public class SelectConnectorDictionary_de implements SelectConnectorDictionary {

	@Override
	public OgemaLocale getLocale() {
		return OgemaLocale.GERMAN;
	}

	@Override
	public String header() {
		return "Konfiguration der Nachrichtenübertragung";
	}

	@Override
	public String description() {
		return "Auf dieser Seite kannst Du einstellen welche Nachrichten von welchen OGEMA-Apps an "
				+ "welche Empfänger weitergeleitet werden, z.B. per Email, SMS oder XMPP. Weitere "
				+ "Nachrichtentypen können über entsprechende Apps nachinstalliert werden. "
				+ "Damit ein Empfänger ausgewählt werden kann, muss er zunächst für den entsprechenden "
				+ "Nachrichtendienst konfiguriert werden. Dafür sollte dieser eine entsprechende Seite "
				+ "bereitstellen, siehe z.B. die <a href=\"" + MESSAGE_SETTINGS_LINK + "\">"
				+ "<b>Konfigurationsseite für die Basisdienste</b></a>.\n"
				+ "Alle Nachrichten die von OGEMA Apps verschickt wurden können auch im Browser angesehen werden: "
				+ "<a href=\"" + MESSAGE_READER_LINK + "\"><b>OGEMA Message Reader</b></a>.<br>"
				+ "Wenn für einen Dienst die Priorität 'LOW' gewählt ist, werden alle Nachrichten "
				+ "der entsprechenden App gesendet, wenn 'HIGH' gewählt wird, nur die Nachrichten "
				+ "mit höchster Priorität etc.";
	}

	
}
