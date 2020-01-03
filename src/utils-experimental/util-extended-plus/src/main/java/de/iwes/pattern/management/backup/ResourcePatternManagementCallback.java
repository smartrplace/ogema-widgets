/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS
 * Fraunhofer ISE
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.pattern.management.backup;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.ResourceStructureEvent;
import org.ogema.core.resourcemanager.ResourceStructureListener;
import org.ogema.core.resourcemanager.pattern.PatternListener;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

/** An object of type ResourcePatternManagement performs the management of ResourcePattern results
 * for an application
 * 
 * @author dnestle
 *
 * @param <DemandedPattern> pattern class to be managed by the container
 */
@Deprecated
public class ResourcePatternManagementCallback<DemandedPattern extends ManagedResourcePatternCallbackStyle<?>> {

	/**Override this*/
	public void patternAvailableNotification(DemandedPattern pattern) {}
	public void patternUnavailableNotification(DemandedPattern pattern) {}
	
	OgemaLogger log;
    //public final List<CoolspacePattern> coolSpaces = new ArrayList<>();
    private List<DemandedPattern> elements = new ArrayList<DemandedPattern>();
	PatternListener<DemandedPattern> patternListener;
    @SuppressWarnings("rawtypes")
	Class clazz;
    Object patternContainer;

    //Elements that the application did not want to use
    public List<ResourcePatternStructureListener> rejectedElements = new ArrayList<ResourcePatternStructureListener>();

	/**
	 * @param appMan application manager to be used by the container
	 * @param clazzIn pattern class to be managed
	 * @param patternContainerIn object to be given to all pattern result objects in their init
	 * method. This should give the pattern object access to all application data it needs.
	 */
	@SuppressWarnings("unchecked")
	public ResourcePatternManagementCallback(ApplicationManager appMan,	Class<? extends ManagedResourcePatternCallbackStyle<?>> clazzIn, Object patternContainerIn) {
		
		clazz = clazzIn;
        patternContainer = patternContainerIn;
        log = appMan.getLogger();
        ResourcePatternAccess m_advAcc = appMan.getResourcePatternAccess();
        patternListener = new PatternListener<DemandedPattern>() {

			@Override
			public void patternAvailable(DemandedPattern pattern) {
				//if(pattern.init(patternContainer)) {
					elements.add(pattern);
					patternAvailableNotification(pattern);
				//} else {
					//TODO: Test this
					//rejectedElements.add(new ResourcePatternStructureListener(pattern));		
				//}
				if(log != null) {
					log.info("Found pattern of type: "+clazz.getName()+" :"+pattern.model.getLocation());
				}
			}
			@Override
			public void patternUnavailable(
					DemandedPattern pattern) {
				pattern.disconnect();
				elements.remove(pattern);
				patternUnavailableNotification(pattern);
			}
        };
        m_advAcc.addPatternDemand(clazz, patternListener, AccessPriority.PRIO_LOWEST, patternContainer);
        System.out.printf("Registererd demand on: %s/%s\n", clazz.getName(), patternListener.getClass().getName());
	}
	
	public class ResourcePatternStructureListener implements ResourceStructureListener {
		public DemandedPattern rejectedPattern;
		
		public ResourcePatternStructureListener(DemandedPattern pattern) {
			rejectedPattern = pattern;
			pattern.model.addStructureListener(this);
			log.debug("Added PatternStructureListener for "+pattern.model.getLocation());
		}

		@Override
		public void resourceStructureChanged(ResourceStructureEvent event) {
			log.debug("Received PatternStructureListener callback for "+rejectedPattern.model.getLocation());
			if(rejectedPattern.accept()) {//init(patternContainer)) {
				log.debug("Accpeted pattern in PatternStructureListener callback for "+rejectedPattern.model.getLocation());
				elements.add(rejectedPattern);
				rejectedPattern.model.removeStructureListener(this);
				//finally mark yourself for the garbage collection
				rejectedElements.remove(this);
			}			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void removeDemand(ResourcePatternAccess m_advAcc) {
		m_advAcc.removePatternDemand(clazz, patternListener);
		System.out.printf("Remove demand on: %s/%s\n", clazz.getName(), patternListener.getClass().getName());
	}
	
	/** Get list of accepted pattern results*/
	public List<DemandedPattern> getElements() {
		/**We have to return a reference to the list itself here as several applications require a reference to the
		 * list that changes when new elements come up*/
		//return new ArrayList<DemandedPattern>(elements);
		return elements;
	}
}
