package de.iwes.app.timeseries.teststarter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.serialization.jaxb.Resource;
import org.smartrplace.analysis.backup.parser.api.GatewayBackupAnalysis;

import de.iwes.app.timeseries.teststarter.gui.MainPage;
import de.iwes.sema.analysis.export.GatewayDataExport;
import de.iwes.timeseries.eval.api.EvaluationManager;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationProvider;
import de.iwes.timeseries.eval.base.provider.BasicEvaluationProvider;
import de.iwes.timeseries.multi.provider.garoBase.GaRoBaseMultiProvider;
import de.iwes.timeseries.multi.provider.garoWinHeat.GaRoWinEvalProvider;
import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;

/**
 * Template OGEMA application class
 */
@References({
	@Reference(
		name="evaluations",
		referenceInterface=EvaluationProvider.class,
		cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
		policy=ReferencePolicy.DYNAMIC,
		bind="addEvalProvider",
		unbind="removeEvalProvider"),
	@Reference(
			name="multievaluations",
			referenceInterface=MultiEvaluationProvider.class,
			cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
			policy=ReferencePolicy.DYNAMIC,
			bind="addMultiEvalProvider",
			unbind="removeMultiEvalProvider")
})
@Component(specVersion = "1.2", immediate = true)
@Service(Application.class)
public class TeststarterApp implements Application {
	public static final String urlPath = "/com/example/app/timeseriesteststarter";

    private OgemaLogger log;
    private ApplicationManager appMan;
    private TeststarterController controller;

	private WidgetApp widgetApp;

	@Reference
	private OgemaGuiService guiService;

	@Reference
	public EvaluationManager evalManager;
    
    @Reference
    protected GatewayDataExport gde;

	@Reference
	public GatewayBackupAnalysis gatewayParser;

	public MainPage mainPage;

	private final Map<String,EvaluationProvider> evaluations = Collections.synchronizedMap(new LinkedHashMap<String,EvaluationProvider>());
	public BasicEvaluationProvider basicEvalProvider = null;
	//public BasicSmartHomeFieldTestEvalProvider smartHomeEval = null;
	
	private final Map<String,MultiEvaluationProvider<?>> multievaluations = Collections.synchronizedMap(new LinkedHashMap<String,MultiEvaluationProvider<?>>());
	public GaRoWinEvalProvider garoWinEval = null;
	public GaRoBaseMultiProvider baseMultiEval = null;
	//public CTGaRoEvalProvider ctMultiEval = null;
	
    /*
     * This is the entry point to the application.
     */
 	@Override
    public void start(ApplicationManager appManager) {

        // Remember framework references for later.
        appMan = appManager;
        log = appManager.getLogger();

        // 
        controller = new TeststarterController(appMan, this, gde);
		
		//register a web page with dynamically generated HTML
		widgetApp = guiService.createWidgetApp(urlPath, appManager);
		WidgetPage<?> page = widgetApp.createStartPage();
		mainPage = new MainPage(page, controller);
		checkComplete();
     }

     /*
     * Callback called when the application is going to be stopped.
     */
    @Override
    public void stop(AppStopReason reason) {
    	if (widgetApp != null) widgetApp.close();
		if (controller != null)
    		controller.close();
        log.info("{} stopped", getClass().getName());
    }
    
    protected void addEvalProvider(EvaluationProvider provider) {
    	evaluations.put(provider.id(), provider);
    	if((provider instanceof BasicEvaluationProvider)&&(basicEvalProvider == null)) {
    		basicEvalProvider = (BasicEvaluationProvider) provider;
    	}
    	//if((provider instanceof BasicSmartHomeFieldTestEvalProvider)&&(smartHomeEval == null)) {
    	//	smartHomeEval = (BasicSmartHomeFieldTestEvalProvider) provider;
    	//	checkComplete();
    	//}
    }
    
    protected void removeEvalProvider(EvaluationProvider provider) {
    	evaluations.remove(provider.id());
    	//TODO: What should we do if electrcityEvalProvider is lost?
    }
    
    protected void addMultiEvalProvider(MultiEvaluationProvider<?> provider) {
    	multievaluations.put(provider.id(), provider);
    	if((provider instanceof GaRoWinEvalProvider)&&(garoWinEval == null)) {
    		garoWinEval = (GaRoWinEvalProvider) provider;
    		checkComplete();
    	} else if((provider instanceof GaRoBaseMultiProvider)&&(baseMultiEval == null)) {
    		baseMultiEval = (GaRoBaseMultiProvider) provider;
    		checkComplete();
    	} //else if((provider instanceof CTGaRoEvalProvider)&&(ctMultiEval == null)) {
    	//	ctMultiEval = (CTGaRoEvalProvider) provider;
    	//	checkComplete();
    	//}
    }
    
    protected void removeMultiEvalProvider(MultiEvaluationProvider<?> provider) {
    	multievaluations.remove(provider.id());
    	//TODO: What should we do if electrcityEvalProvider is lost?
    }

    public void checkComplete() {
    	if(mainPage == null) return;
    	if(mainPage.getSmartHomeProvider() == null) return;
    	if(Boolean.getBoolean("de.iwes.app.timeseries.teststarter.allowAutoStart"))
    		controller.startInitialEvaluation(mainPage);	
    }
}
