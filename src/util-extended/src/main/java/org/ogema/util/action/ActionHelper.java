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
package org.ogema.util.action;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.ResourceList;
import org.ogema.model.action.Action;

public class ActionHelper {
	public static <T extends Action> T runExtendedActionNonBlocking(Class<T> actionType, String controllingApplication,
			ApplicationManager appMan, int multiReactMode, String warningIfNotAvailable) {
		T result = findSingleExtendedAction(actionType, controllingApplication, appMan, multiReactMode);
		if(result!=null) {
			result.stateControl().setValue(true);
		} else {
			if(warningIfNotAvailable != null) appMan.getLogger().warn(warningIfNotAvailable);
		}
		return result;
	}
	/** Find an Action of the 'extended action type' meaning that it extends Action and does not contain 
	 * Action just as a sub element. TODO: Provide such a util for this also
	 * 
	 * @param actionType action class to search for
	 * @param controllingApplication if null all actions of the type will be considered
	 * @param appMan
	 * @param multiReactMode if more than one action fits actionType and controllingApplication (if
	 * 		specified) just the first action is used if no exception is thrown
	 * 		0: just use first action found, 1: log wanring, 2: throw IllegalStateException
	 * @return
	 */
	public static <T extends Action> T findSingleExtendedAction(Class<T> actionType, String controllingApplication,
			ApplicationManager appMan, int multiReactMode) {
		List<T> actList = findExtendedActions(actionType, controllingApplication, appMan);
		if(actList.isEmpty()) return null;
		if(actList.size() > 1) {
			switch(multiReactMode) {
			case 1:
				appMan.getLogger().warn("Found several results for single extended action!");
			case 2:
				throw new IllegalStateException("Found several results for single extended action!");
			}
		}
		return actList.get(0);
	}
	public static <T extends Action> List<T> findExtendedActions(Class<T> actionType, String controllingApplication,
			ApplicationManager appMan) {
		List<T> actList1 = appMan.getResourceAccess().getResources(actionType);
		List<T> actListResult;
		if(controllingApplication == null) {
			actListResult = actList1;
		} else {
			actListResult = new ArrayList<>();
			for(T a: actList1) {
				if(a.controllingApplication().getValue().equals(controllingApplication)) {
					actListResult.add(a);
				}
			}
		}
		return actListResult;
	}
	
	/**Perform list of {@link Action} and return only when all actions are done or maxDuration is expired.
	 * Note that maxDuration will only be used as an approximate value.*/
	public static void performActionListBlocking(ResourceList<Action> collectionActions, long maxDuration) {
		if(collectionActions.isActive()) {
			List<Action> actionList = new ArrayList<>();
			for(Action ac: collectionActions.getAllElements()) {
				if(ac.isActive()) {
					ac.stateControl().setValue(true);
					actionList.add(ac);
				}
			};
			List<Action> done = new ArrayList<>();
			long maxEnd = System.currentTimeMillis()+maxDuration;
			while((!actionList.isEmpty())&&(System.currentTimeMillis() < maxEnd)) {
				for(Action ac: actionList) {
					if(!ac.stateControl().getValue()) {
						done.add(ac);
					}
				}
				actionList.removeAll(done);
				done.clear();
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}		
	}
	public static void performActionBlocking(Action ac, long maxDuration) {
		ac.stateControl().setValue(true);
		long maxEnd = System.currentTimeMillis()+maxDuration;
		while((ac.stateControl().getValue())&&(System.currentTimeMillis() < maxEnd)) {
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
}
