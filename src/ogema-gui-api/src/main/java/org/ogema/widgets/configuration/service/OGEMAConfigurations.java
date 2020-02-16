package org.ogema.widgets.configuration.service;

import java.util.List;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class OGEMAConfigurations {
	/** Find property
	 * 
	 * @param className null for global properties
	 * @param property
	 * @return
	 */
	public static String getProperty(String className, String property) {
		for(OGEMAConfigurationProvider prov: relevantProviders(className)) {
			String result = prov.getProperty(property, null, null, null);
			if(result != null)
				return result;
		}
		return null;
	}
	
	public static String getProperty(String className, String property, OgemaLocale locale) {
		//TODO
		return null;
	}

	public static String getProperty(String className, String property, OgemaHttpRequest req) {
		//TODO
		return null;
	}
	
	public static String getProperty(String className, String property, Object context) {
		//TODO
		return null;
	}

	public static String getProperty(String className, String property, OgemaLocale locale, Object context) {
		//TODO
		return null;
	}

	public static String getProperty(String className, String property, OgemaHttpRequest req, Object context) {
		//TODO
		return null;
	}

	public static Object getObject(String className, String property) {
		for(OGEMAConfigurationProvider prov: relevantProviders(className)) {
			Object result = prov.getObject(property, null, null, null);
			if(result != null)
				return result;
		}
		return null;
	}
	
	public static Object getObject(String className, String property, OgemaLocale locale) {
		//TODO
		return null;
	}

	public static Object getObject(String className, String property, OgemaHttpRequest req) {
		//TODO
		return null;
	}
	
	public static Object getObject(String className, String property, Object context) {
		//TODO
		return null;
	}

	public static Object getObject(String className, String property, OgemaLocale locale, Object context) {
		//TODO
		return null;
	}

	public static Object getObject(String className, String property, OgemaHttpRequest req, Object context) {
		//TODO
		return null;
	}

	protected static List<OGEMAConfigurationProvider> relevantProviders(String className) {
		return ConfigurationCollector.instance.providers.get(className);
	}
}
