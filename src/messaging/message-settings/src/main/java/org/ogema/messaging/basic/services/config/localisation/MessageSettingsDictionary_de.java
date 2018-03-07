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

package org.ogema.messaging.basic.services.config.localisation;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class MessageSettingsDictionary_de implements MessageSettingsDictionary {

	@Override
	public OgemaLocale getLocale() {
		return OgemaLocale.GERMAN;
	}

	@Override
	public String headerSenders() {
		return "Sender-Konfiguration der Basis-Messengerdienste";
	}
	
	@Override
	public String headerReceivers() {
		return "Empf�nger-Konfiguration der Basis-Messengerdienste";
	}

	@Override
	public String descriptionReceivers() {
		return "Auf dieser Seite kannst Du Empf�nger f�r Nachrichten von OGEMA-Apps einrichten, f�r die drei"
				+ " Basisdienste Email, SMS und XMPP. Das Anlegen der Empf�nger auf dieser Seite f�hrt noch"
				+ " nicht dazu, dass Nachrichten verschickt werden. Dazu gibt es eine weitere Seite, auf der "
				+ "zu jeder App einzeln festgelegt werden kann, welcher Empf�nger die gesendeten Nachrichten"
				+ " erhalten soll. Du findest die Seite <a href=\"" + SELECT_CONNECTOR_LINK + "\"><b>hier</b></a>.\n"   
				+ "Au�erdem muss f�r die drei Basisdienste jeweils ein Absenderkonto angegeben werden, bevor"
				+ " sie Nachrichten verschicken k�nnen. Die entsprechende Seite findest Du <a href=\""
				+  SENDER_LINK + "\"><b>hier</b></a>.\n"
				+ "Die eingegebenen Daten werden nur lokal auf dem Gateway gespeichert.\n"
				+ "Alle Nachrichten die von OGEMA Apps verschickt wurden k�nnen auch im Browser angesehen werden: "
				+ "<a href=\"" + MESSAGE_READER_LINK + "\"><b>OGEMA Message Reader</b></a>.";
	}

	@Override
	public String descriptionSenders() {
		return "Hier kannst Du Absenderkonten f�r die drei Basis-Messengerdienste Email, SMS und XMPP angeben. "
				+ "Bevor kein Absenderkonto konfiguriert ist, k�nnen keine Nachrichten �ber den jeweiligen Dienst "
				+ "versendet werden. Um Nachrichten �ber einen Dienst verschicken k�nnen, muss das Passwort f�r "
				+ "das Konto angegeben werden. Es ist deshalb empfehlenswert, einen eigenen Account nur zu diesem "
				+ "Zweck anzulegen, und nicht bspw. einen pers�nlichen Email-Account zu verwenden.\n"
				+ "Empf�ngeradressen f�r die jeweiligen Dienste k�nnen auf <a href=\"" + RECEIVER_LINK +"\"><b>dieser Seite</b></a> "
				+ "konfiguriert werden.";
	}

	
}
