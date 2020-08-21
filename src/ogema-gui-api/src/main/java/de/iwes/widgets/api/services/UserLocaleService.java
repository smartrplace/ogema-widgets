package de.iwes.widgets.api.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Provides non-persistent storage for per-user locales.
 * @author jruckel
 *
 */
public class UserLocaleService {
	private OgemaLocale defaultLocale = OgemaLocale.ENGLISH;
	
	private Map<String, OgemaLocale> userLocales = new HashMap<>();
	
	public OgemaLocale getUserLocale(String userName) {
		return userLocales.getOrDefault(userName, defaultLocale);
	}
	
	public OgemaLocale setUserLocale(String userName, OgemaLocale locale) {
		if (locale == null)
			locale = defaultLocale;
		return userLocales.put(userName, locale);
	}
	
	public void setDefault(OgemaLocale locale) {
		defaultLocale = locale;
	}
	
	public Collection<OgemaLocale> getAvailableLocales() {
		return OgemaLocale.getAllLocales();
	}
}
