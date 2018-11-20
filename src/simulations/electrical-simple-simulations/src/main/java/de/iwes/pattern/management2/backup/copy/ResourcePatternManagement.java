/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
package de.iwes.pattern.management2.backup.copy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.AccessPriority;
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
public class ResourcePatternManagement<DemandedPattern extends ManagedResourcePattern<?, C>, C extends Object> {

	private final OgemaLogger log;
    //public final List<CoolspacePattern> coolSpaces = new ArrayList<>();
    private final List<DemandedPattern> elements = Collections.synchronizedList(new ArrayList<DemandedPattern>());
	private final PatternListener<DemandedPattern> patternListener;
    @SuppressWarnings("rawtypes")
	private final Class clazz;
    private final C patternContainer;

    //Elements that the application did not want to use
//    public List<ResourcePatternStructureListener> rejectedElements = new ArrayList<ResourcePatternStructureListener>();

	/**
	 * @param appMan application manager to be used by the container
	 * @param clazzIn pattern class to be managed
	 * @param patternContainerIn object to be given to all pattern result objects in their init
	 * method. This should give the pattern object access to all application data it needs.
	 */
	@SuppressWarnings("unchecked")
	public ResourcePatternManagement(ApplicationManager appMan,	Class<? extends ManagedResourcePattern<?, C>> clazzIn, C patternContainerIn) {
		
		clazz = clazzIn;
        patternContainer = patternContainerIn;
        log = appMan.getLogger();
        ResourcePatternAccess m_advAcc = appMan.getResourcePatternAccess();
        patternListener = new PatternListener<DemandedPattern>() {

			@Override
			public void patternAvailable(DemandedPattern pattern) {
				//if(pattern.init(patternContainer)) {
					elements.add(pattern);
				//} else {
					//TODO: Test this
				//	rejectedElements.add(new ResourcePatternStructureListener(pattern));		
				//}
				if(log != null && log.isDebugEnabled()) {
					log.debug("Found pattern of type: "+clazz.getName()+" :"+pattern.model.getLocation());
				}
			}
			@Override
			public void patternUnavailable(DemandedPattern pattern) {
				pattern.disconnect();
				elements.remove(pattern);
			}
        };
        m_advAcc.addPatternDemand(clazz, patternListener, AccessPriority.PRIO_LOWEST, patternContainer);
        log.info("Registererd demand on: {} {}", clazz.getName(), patternListener.getClass().getName());
	}
	
//	public class ResourcePatternStructureListener implements ResourceStructureListener { // TODO should be possible now to do this with standard methods 
//		public DemandedPattern rejectedPattern;
//		
//		public ResourcePatternStructureListener(DemandedPattern pattern) {
//			rejectedPattern = pattern;
//			pattern.model.addStructureListener(this);
//			log.debug("Added PatternStructureListener for "+pattern.model.getLocation());
//		}
//
//		@Override
//		public void resourceStructureChanged(ResourceStructureEvent event) {
//			log.debug("Received PatternStructureListener callback for "+rejectedPattern.model.getLocation());
//			if(rejectedPattern.accept()) {//init(patternContainer)) {
//				log.debug("Accpeted pattern in PatternStructureListener callback for "+rejectedPattern.model.getLocation());
//				elements.add(rejectedPattern);
//				rejectedPattern.model.removeStructureListener(this);
//				//finally mark yourself for the garbage collection
//				rejectedElements.remove(this);
//			}			
//		}
//		
//	}
//	
	@SuppressWarnings("unchecked")
	public void removeDemand(ResourcePatternAccess m_advAcc) {
		m_advAcc.removePatternDemand(clazz, patternListener);
		log.debug("Remove demand on: {} {}", clazz.getName(), patternListener.getClass().getName());
	}
	
	/** Get list of accepted pattern results*/
	public List<DemandedPattern> getElements() {
		/**We have to return a reference to the list itself here as several applications require a reference to the
		 * list that changes when new elements come up*/ 		// XXX uff
		// ConcurrentModificationException?
		//return new ArrayList<DemandedPattern>(elements);
		return elements;
	}
}
