package org.ogema.widgets.configuration.service;

import java.util.Collection;
import java.util.List;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/** Implement and publish this service to provide configurations that can be accessed easily by all
 * OGEMA apps that have a dependency to ogema-gui-api. The properties and configurations can be 
 * accessed via static methods of {@link OGEMAConfigurations}.
 */
public interface OGEMAConfigurationProvider {
	/** Class name for which configurations are provided
	 * @return null for gobal properties, otherwise the full class name of the class using the property*/
	public String className();
	
	/** Priority of the configuration provider. The smaller the number the higher the priority.
	 * Default service is 1000*/
	public int priority();
	
	/** Additional providers that may defined different className and priority*/
	public List<OGEMAConfigurationProvider> additionalProviders();
	
	/** All property names provided by the service. For now we just support String properties
	 * and Object properties here. Other simple values shall be converted by the collecting service.
	 * The methods {@link #getProperty(String, OgemaLocale, OgemaHttpRequest, Object)} and
	 * {@link #getObject(String, OgemaLocale, OgemaHttpRequest, Object)} will only be called for
	 * properties defined here. If the service returns null for the parameter configuration then the
	 * service with the next priortiy level will be checked.*/
	public Collection<String> propertiesProvided();
	
	/** Provide actual property value*/
	public String getProperty(String property, OgemaLocale locale, OgemaHttpRequest req, Object context);
	
	/**Provide configuration/property objects
	 * 
	 * @param property may be null if the className is not null. In this case a general configuration object
	 * 		for the class is requested
	 * @param locale
	 * @param req
	 * @param context
	 * @return
	 */
	public Object getObject(String property, OgemaLocale locale, OgemaHttpRequest req, Object context);
}
