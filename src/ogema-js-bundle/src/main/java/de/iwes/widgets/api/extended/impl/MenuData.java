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
package de.iwes.widgets.api.extended.impl;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class MenuData {
	
	public static final JSONObject getMenuData(OgemaHttpRequest req) {
		OgemaLocale locale = req.getLocale();
		JSONObject result = new JSONObject();
		result.put("logout", getLogoutModalProps(locale));
		result.put("messages", getMessagesText(locale));
		return result;
	}
	
	private static final JSONObject getLogoutModalProps(OgemaLocale locale) {
		JSONObject result = new JSONObject();
		if (locale.equals(OgemaLocale.GERMAN)) {
			result.put("title", "Abmelden");
			result.put("logoutBtn", "Abmelden");
			result.put("cancelBtn", "Schlie�en");
			result.put("msg", "Willst Du Dich von diesem System abmelden?");
		}
		else if (locale.equals(OgemaLocale.FRENCH)) {
			result.put("title", "Terminer la session");
			result.put("logoutBtn", "Terminer la session");
			result.put("cancelBtn", "Fermer");
			result.put("msg", "Est-ce que tu veux te d�connecter?");
		}
		else {
			result.put("title", "Logout");
			result.put("logoutBtn", "Logout");
			result.put("cancelBtn", "Close");
			result.put("msg", "Do you want to logout from this system?");
		}
		result.put("logoutHtml", "<span class=\"glyphicon glyphicon-log-out\"></span> " + result.getString("title")); 
		return result;
	}
	
	private final static String getMessagesText(OgemaLocale locale) {
		if (locale.equals(OgemaLocale.GERMAN))
			return "Nachrichten";
		else
			return "Messages"; // English, French
	}

}
