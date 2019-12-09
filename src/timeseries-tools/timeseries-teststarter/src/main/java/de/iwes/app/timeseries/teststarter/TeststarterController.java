package de.iwes.app.timeseries.teststarter;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.app.timeseries.teststarter.config.TeststarterConfig;
import de.iwes.app.timeseries.teststarter.gui.MainPage;
import de.iwes.sema.analysis.export.GatewayDataExport;

// here the controller logic is implemented
public class TeststarterController {

	public OgemaLogger log;
    public ApplicationManager appMan;
    private ResourcePatternAccess advAcc;

	public TeststarterConfig appConfigData;
	
	public final TeststarterApp serviceAccess;
    
    public final GatewayDataExport gde;
	
    public TeststarterController(ApplicationManager appMan, TeststarterApp serviceAcccess, GatewayDataExport gde) {
		this.appMan = appMan;
		this.log = appMan.getLogger();
		this.advAcc = appMan.getResourcePatternAccess();
		this.serviceAccess = serviceAcccess;
        this.gde = gde;
		
        initConfigurationResource();
        initDemands();
	}

    public void startInitialEvaluation(MainPage page) {
    	page.gaRoButton4ct_rt.onPrePOST(null, null);
    }
    /*
     * This app uses a central configuration resource, which is accessed here
     */
    private void initConfigurationResource() {
		String configResourceDefaultName = TeststarterConfig.class.getSimpleName().substring(0, 1).toLowerCase()+TeststarterConfig.class.getSimpleName().substring(1);
		appConfigData = appMan.getResourceAccess().getResource(configResourceDefaultName);
		if (appConfigData != null) { // resource already exists (appears in case of non-clean start)
			appMan.getLogger().debug("{} started with previously-existing config resource", getClass().getName());
		}
		else {
			appConfigData = (TeststarterConfig) appMan.getResourceManagement().createResource(configResourceDefaultName, TeststarterConfig.class);
			appConfigData.sampleElement().create();
			appConfigData.sampleElement().setValue("Example");
			appConfigData.activate(true);
			appMan.getLogger().debug("{} started with new config resource", getClass().getName());
		}
    }
    
    /*
     * register ResourcePatternDemands. The listeners will be informed about new and disappearing
     * patterns in the OGEMA resource tree
     */
    public void initDemands() {
    }

	public void close() {
    }

	/*
	 * if the app needs to consider dependencies between different pattern types,
	 * they can be processed here.
	 */
	public void processInterdependies() {
		// TODO Auto-generated method stub
		
	}
}
