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

package de.iwes.widgets.api.widgets.localisation;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
	
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains an ordinary {@link Locale}, plus an icon path and some static
 * methods to retrieve available locales and to register new ones.
 * <br>
 * The supported locales are currently set/retrieved via static methods. This
 * mechanism is likely to change in the future.
 */
public class OgemaLocale {	
	
	public static final OgemaLocale ENGLISH = new OgemaLocale(Locale.ENGLISH);
	public static final OgemaLocale GERMAN = new OgemaLocale(Locale.GERMAN);
	public static final OgemaLocale FRENCH = new OgemaLocale(Locale.FRENCH);
	public static final OgemaLocale CHINESE = new OgemaLocale(Locale.CHINESE);
	
	private static final String BASE_PATH = "/org/ogema/localisation/service";
	private static final ConcurrentMap<String, OgemaLocale> locales = new ConcurrentHashMap<String, OgemaLocale>();
	private static final Logger logger = LoggerFactory.getLogger(OgemaLocale.class);
	
	static {
		locales.put(Locale.ENGLISH.getLanguage(), ENGLISH);
		locales.put(Locale.GERMAN.getLanguage(), GERMAN);
		locales.put(Locale.FRENCH.getLanguage(), FRENCH);
		locales.put(Locale.CHINESE.getLanguage(), CHINESE);
		logger.debug("registered languages {}",locales);
	}
	
	private final Locale locale;
	
	public OgemaLocale(Locale locale) {
		this.locale = locale;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public static Set<OgemaLocale> getAllLocales() {
		return new HashSet<OgemaLocale>(locales.values());
	}
	
	public static OgemaLocale getLocale(String language) {
		// logger.debug("request for locale " + language);
		if (language == null) return null;
		return locales.get(language);
	}
	
	public static void registerLocale(OgemaLocale locale) {
		locales.put(locale.getLanguage(), locale);
	}
	
	public String getLanguage() {
		return getLocale().getLanguage();
	}
	
	/**
	 * Override in derived class if necessary
	 * @return Browser path to Icon
	 */
	public String getIcon() {
		return BASE_PATH + "/" + correctIconCode(getLocale().getLanguage().toUpperCase()) + ".png";
	}
	
	private static String correctIconCode(String in) {
		return in.replace("EN", "GB").replace("ZH", "CN");
	}
	
	@Override
	public String toString() {
		return locale.getLanguage();
	}
	
	public JSONObject getJson(Locale inLocale) {
		JSONObject obj = new JSONObject();
		obj.put("text", locale.getLanguage());
		obj.put("imageSrc", getIcon());
		obj.put("description", locale.getDisplayLanguage(inLocale));
		return obj;
	}
	
	public JSONObject getJson() {
		return getJson(Locale.ENGLISH);
	}
	
}
