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

public class SelectConnectorDictionary_de implements SelectConnectorDictionary {

	@Override
	public OgemaLocale getLocale() {
		return OgemaLocale.GERMAN;
	}

	@Override
	public String header() {
		return "Konfiguration der Nachrichten�bertragung";
	}

	@Override
	public String description() {
		return "Auf dieser Seite kannst Du einstellen welche Nachrichten von welchen OGEMA-Apps an "
				+ "welche Empf�nger weitergeleitet werden, z.B. per Email, SMS oder XMPP. Weitere "
				+ "Nachrichtentypen k�nnen �ber entsprechende Apps nachinstalliert werden. "
				+ "Damit ein Empf�nger ausgew�hlt werden kann, muss er zun�chst f�r den entsprechenden "
				+ "Nachrichtendienst konfiguriert werden. Daf�r sollte dieser eine entsprechende Seite "
				+ "bereitstellen, siehe z.B. die <a href=\"" + MESSAGE_SETTINGS_LINK + "\">"
				+ "<b>Konfigurationsseite f�r die Basisdienste</b></a>.\n"
				+ "Alle Nachrichten die von OGEMA Apps verschickt wurden k�nnen auch im Browser angesehen werden: "
				+ "<a href=\"" + MESSAGE_READER_LINK + "\"><b>OGEMA Message Reader</b></a>.<br>"
				+ "Wenn f�r einen Dienst die Priorit�t 'LOW' gew�hlt ist, werden alle Nachrichten "
				+ "der entsprechenden App gesendet, wenn 'HIGH' gew�hlt wird, nur die Nachrichten "
				+ "mit h�chster Priorit�t etc.";
	}

	
}
