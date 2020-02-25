package de.iwes.timeseries.eval.garo.api.helper.base;

import java.util.Collection;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.widgets.configuration.service.OGEMAConfigurationProvider;

import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/** Finds a GaRoDataType for a recId. Uses recId as property name. For this reason no list of properties
 * can be provided
 * @author dnestle
 *
 */
@Service(OGEMAConfigurationProvider.class)
@Component
public class GaRoTypeStringConfigProvider implements OGEMAConfigurationProvider {

	@Override
	public String className() {
		return GaRoDataType.class.getName();
	}

	@Override
	public int priority() {
		return 1000;
	}

	@Override
	public List<OGEMAConfigurationProvider> additionalProviders() {
		return null;
	}

	@Override
	public Collection<String> propertiesProvided() {
		return null;
	}

	@Override
	public String getProperty(String property, OgemaLocale locale, OgemaHttpRequest req, Object context) {
		return null;
	}

	@Override
	public Object getObject(String property, OgemaLocale locale, OgemaHttpRequest req, Object context) {
		if(property.equals("%recSnippets")) {
			return GaRoEvalHelper.recIdSnippets;
		}
		//if(property.equals("%plotOptions")) {
		//	return GaRoEvalHelper.userPlotOptions;
		//}
		return GaRoEvalHelper.getDataType(property);
	}

}
