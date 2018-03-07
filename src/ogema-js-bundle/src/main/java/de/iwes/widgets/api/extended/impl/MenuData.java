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
