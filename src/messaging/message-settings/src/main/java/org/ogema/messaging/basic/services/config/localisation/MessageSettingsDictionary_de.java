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
		if(Boolean.getBoolean("org.ogema.messaging.basic.services.config.fixconfigenglish"))
			return "Receiver configuration of the basic messenger services";
		return "Empfänger-Konfiguration der Basis-Messengerdienste";
	}

	@Override
	public String descriptionReceivers() {
		if(Boolean.getBoolean("org.ogema.messaging.basic.services.config.fixconfigenglish"))
			return "For the configuration which applications send mesages to which receiver "+
				"see <a href=\"" + SELECT_CONNECTOR_LINK + "\"><b>here</b></a>.\n";
		return "Auf dieser Seite kannst Du Empfänger für Nachrichten einrichten, für die drei"
				+ " Basisdienste Email, SMS und XMPP. Das Anlegen der Empfänger auf dieser Seite führt noch"
				+ " nicht dazu, dass Nachrichten verschickt werden. Dazu gibt es eine weitere Seite, auf der "
				+ "zu jeder App einzeln festgelegt werden kann, welcher Empfänger die gesendeten Nachrichten"
				+ " erhalten soll. Du findest die Seite <a href=\"" + SELECT_CONNECTOR_LINK + "\"><b>hier</b></a>.\n"   
				+ "Außerdem muss für die drei Basisdienste jeweils ein Absenderkonto angegeben werden, bevor"
				+ " sie Nachrichten verschicken können. Die entsprechende Seite findest Du <a href=\""
				+  SENDER_LINK + "\"><b>hier</b></a>.\n"
				+ "Die eingegebenen Daten werden nur lokal auf dem Gateway gespeichert.\n"
				+ "Alle Nachrichten die erschickt wurden können auch im Browser angesehen werden: "
				+ "<a href=\"" + MESSAGE_READER_LINK + "\"><b>Message Reader</b></a>.";
	}

	@Override
	public String descriptionSenders() {
		return "Hier kannst Du Absenderkonten für die drei Basis-Messengerdienste Email, SMS und XMPP angeben. "
				+ "Bevor kein Absenderkonto konfiguriert ist, können keine Nachrichten über den jeweiligen Dienst "
				+ "versendet werden. Um Nachrichten über einen Dienst verschicken können, muss das Passwort für "
				+ "das Konto angegeben werden. Es ist deshalb empfehlenswert, einen eigenen Account nur zu diesem "
				+ "Zweck anzulegen, und nicht bspw. einen persönlichen Email-Account zu verwenden.\n"
				+ "Empfängeradressen für die jeweiligen Dienste können auf <a href=\"" + RECEIVER_LINK +"\"><b>dieser Seite</b></a> "
				+ "konfiguriert werden.";
	}

	
}
