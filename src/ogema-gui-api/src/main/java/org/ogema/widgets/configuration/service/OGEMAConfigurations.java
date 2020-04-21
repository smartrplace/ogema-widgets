package org.ogema.widgets.configuration.service;

import java.util.Collections;
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
		return getObject(className, property, (Object)null);
	}
	
	public static <T> T getObject(String className, String property, Class<T> expectedType) {
		return getObject(className, property, null, expectedType);
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
		for(OGEMAConfigurationProvider prov: relevantProviders(className)) {
			Object result = prov.getObject(property, null, null, context);
			if(result != null)
				return result;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getObject(String className, String property, Object context, Class<T> expectedType) {
		Object objRaw = getObject(className, property, context);
		if(expectedType.isAssignableFrom(objRaw.getClass()))
			return (T) objRaw;
		else
			return null;
	}

	public static <T> T getObject(String className, String property, OgemaLocale locale, Object context, Class<T> expectedType) {
		//TODO
		return null;
	}

	public static <T> T getObject(String className, String property, OgemaHttpRequest req, Object context, Class<T> expectedType) {
		//TODO
		return null;
	}

	protected static List<OGEMAConfigurationProvider> relevantProviders(String className) {
		List<OGEMAConfigurationProvider> provs = ConfigurationCollector.instance.providers.get(className);
		if(provs != null) return provs;
		return Collections.emptyList();
	}
}
