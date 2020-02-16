package org.ogema.widgets.configuration.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;

@References({
	@Reference(
		name="configProviders",
		referenceInterface=OGEMAConfigurationProvider.class,
		cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
		policy=ReferencePolicy.DYNAMIC,
		bind="addConfigProvider",
		unbind="removeConfigProvider")
	})
@Component(specVersion = "1.2", immediate = true)
@Service(Application.class)
public class ConfigurationCollector implements Application {
	static ConfigurationCollector instance = null;
	
	/** List of all services registered, sub-services are not listed here*/
	public List<OGEMAConfigurationProvider> services = new ArrayList<OGEMAConfigurationProvider>();
	
	/** className -> relevantProviders. Sub-providers are listed separately here*/
	public Map<String, List<OGEMAConfigurationProvider>> providers = new HashMap<>();
	
	public ConfigurationCollector() {
		if(instance == null)
			instance = this;
	}

	@Override
	public void start(ApplicationManager appManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop(AppStopReason reason) {
		// TODO Auto-generated method stub
		
	}
	
    protected void addConfigProvider(OGEMAConfigurationProvider provider) {
    	services.add(provider);
    	addProvider(provider);
    }
    protected void removeConfigProvider(OGEMAConfigurationProvider provider) {
    	services.remove(provider);
    	//TODO: Implement sub-registration
    }

    private void addProvider(OGEMAConfigurationProvider provider) {
    	List<OGEMAConfigurationProvider> provList = providers.get(provider.className());
    	if(provList == null) {
    		provList = new ArrayList<>();
    		providers.put(provider.className(), provList);
    	}
    	provList.add(provider);
    	provList.sort(new Comparator<OGEMAConfigurationProvider>() {

			@Override
			public int compare(OGEMAConfigurationProvider o1, OGEMAConfigurationProvider o2) {
				return Integer.compare(o2.priority(), o1.priority());
			}
		});
    	if(provider.additionalProviders() != null) for(OGEMAConfigurationProvider p: provider.additionalProviders()) {
    		addProvider(p);
    	}
    }
}
