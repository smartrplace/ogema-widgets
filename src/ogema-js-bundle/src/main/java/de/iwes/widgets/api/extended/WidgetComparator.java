/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package de.iwes.widgets.api.extended;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.xxx.ConfiguredWidget;
import de.iwes.widgets.api.extended.xxx.WidgetGroupDerived;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;

// TODO group dependencies
public class WidgetComparator implements Comparator<ConfiguredWidget<?>> {
	
	private final Map<String, ConfiguredWidget<?>> widgets;
	// may be null
	private final Map<String,WidgetGroupDerived> groups; 
	// cache dependencies // Map<widgetId, List<Map<dependentWidget, triggered action>>
	private final Map<String, Map<ConfiguredWidget<?>,TriggeredAction>> GETDependencies = new HashMap<>();  
	private final Map<String, Map<ConfiguredWidget<?>,TriggeredAction>> POSTDependencies = new HashMap<>();  
	
	public WidgetComparator(Map<String, ConfiguredWidget<?>> widgets,Map<String,WidgetGroupDerived> groups) {
		this.widgets = widgets;
		this.groups = groups;
	}

	@Override
	public int compare(ConfiguredWidget<?> c1, ConfiguredWidget<?> c2) {		
		DependencyStatus status1 = compareWidgets(c1, c2);
		DependencyStatus status2 = compareWidgets(c2, c1);
		return status2.relevance - status1.relevance;
	}
	
	/**
	 * Try to determine whether there is an ordering dependency between the widgets.
	 * Check whether w2 is a dependency of w1.
	 * 
	 * @param w1
	 * @param w2
	 * @return
	 */
	public final DependencyStatus compareWidgets(ConfiguredWidget<?> c1, ConfiguredWidget<?> c2) {
		OgemaWidgetBase<?> w1 = c1.widget;
		OgemaWidgetBase<?> w2 = c2.widget;
		
		if (w1 == w2 || w1.equals(w2)) return DependencyStatus.NONE;
/*		List<Map<String,Object>> dependencies1a = w1.globalConnectElements;
		List<Map<String,Object>> dependencies1b = w1.globalConnectGroups;
		List<Map<String,Object>> dependencies2a = w2.globalConnectElements;
		List<Map<String,Object>> dependencies2b = w2.globalConnectGroups;
		boolean e1a = dependencies1a.isEmpty();
		boolean e1b = dependencies1b.isEmpty();
		if (e1a && e1b) // w1 has no dependencies 
			return DependencyStatus.NONE;
		boolean e2a = dependencies2a.isEmpty();
		boolean e2b = dependencies2b.isEmpty();
		if (e2a && e2b) // w2 has no dependencies, but w1 does
			return DependencyStatus.HIGHEST; */
		// now both have dependencies
//		DependencyStatus directDep1 = getDirectDependencyStatus(w1, c2, dependencies1a);
		List<DependencyStatus> statuses = new ArrayList<DependencyStatus>();
		WidgetProxy proxy = new WidgetProxy(c1);
		WidgetVisitor visitor = new WidgetVisitor(c2);
		proxy.depthFirstSearch(visitor);
		statuses.addAll(visitor.status);
		DependencyStatus status = DependencyStatus.NONE;
		int value = 0;
		for (DependencyStatus stat:statuses) {
			if (stat.getRelevance() > value) {
				status = stat;
				value = stat.relevance;
			}
		}
		return status;
	}

	
	/*	private DependencyStatus getDirectDependencyStatus(OgemaWidget<?> source, ConfiguredWidget<?> target, List<Map<String,Object>> dependencies) {
		setDependencyStatus(source, dependencies);
		Map<ConfiguredWidget<?>,TriggeredAction> get = GETDependencies.get(source.getId());
		Map<ConfiguredWidget<?>,TriggeredAction> post = GETDependencies.get(source.getId());
		TriggeredAction taGet = get.get(target);				
		TriggeredAction taPost = post.get(target);

		if (taGet == null && taPost != null) {
			if (taPost == TriggeredAction.POST_REQUEST)
				return DependencyStatus.POST_POST;
			else if (taPost == TriggeredAction.GET_REQUEST) 
				return DependencyStatus.POST_GET;
		}
		if (taGet != null && taPost == null) {
			if (taGet == TriggeredAction.POST_REQUEST)
				return DependencyStatus.GET_POST;
			else if (taGet == TriggeredAction.GET_REQUEST)
				return DependencyStatus.GET_GET;
		}
		if (taGet != null && taPost != null) {
			if (taGet == TriggeredAction.GET_REQUEST)
				return DependencyStatus.GET_GET;
			else if (taPost == TriggeredAction.GET_REQUEST)
				return DependencyStatus.POST_GET;
			else if (taGet == TriggeredAction.POST_REQUEST)
				return DependencyStatus.GET_POST;
			else if (taPost == TriggeredAction.POST_REQUEST)
				return DependencyStatus.POST_POST;
		}
		return null;
	}
	
	private DependencyStatus getDirectDependencyStatus(OgemaWidget<?> source, ConfiguredWidget<?> target, List<Map<String,Object>> dependencies, TriggeringAction initialAction) {
		setDependencyStatus(source, dependencies);
		Map<ConfiguredWidget<?>,TriggeredAction> deps;
		if (initialAction == TriggeringAction.GET_REQUEST)
			deps = GETDependencies.get(source.getId());
		else 
			deps = POSTDependencies.get(source.getId());
		TriggeredAction triggered = deps.get(target);
		if (initialAction == TriggeringAction.GET_REQUEST) {
			if (triggered == TriggeredAction.GET_REQUEST) 
				return DependencyStatus.GET_GET;
			if (triggered == TriggeredAction.POST_REQUEST) 
				return DependencyStatus.GET_POST;
		}
		else if (initialAction == TriggeringAction.POST_REQUEST) {
			if (triggered == TriggeredAction.GET_REQUEST) 
				return DependencyStatus.POST_GET;
			if (triggered == TriggeredAction.POST_REQUEST) 
				return DependencyStatus.POST_POST;
		}
		return null;
	} 
	
		
	// synchronize?
	private static boolean isGroupDependency(OgemaWidget<?> widget, List<Map<String,Object>> dependencies) {
		if (dependencies.isEmpty()) return false;
		Set<String> groups = widget.groups;
		if (groups.isEmpty()) return false;
		for (Map<String,Object> depMap: dependencies) {
			Object obj = depMap.get("widgetID2");
			if (obj == null || !(obj instanceof String)) continue;
			if (groups.contains(obj)) return true;
		}
		return false;
	}
*/	

	private void setDependencyStatus(OgemaWidgetBase<?> source, List<Map<String,Object>> dependencies, List<Map<String,Object>> grpDependencies) {
		Map<ConfiguredWidget<?>,TriggeredAction> get = GETDependencies.get(source.getId());
		Map<ConfiguredWidget<?>,TriggeredAction> post = GETDependencies.get(source.getId()); // is this correct?
		if (get != null) return; // already set
		get = new HashMap<ConfiguredWidget<?>,TriggeredAction>();
		post = new HashMap<ConfiguredWidget<?>,TriggeredAction>();
		GETDependencies.put(source.getId(), get);
		POSTDependencies.put(source.getId(), post);
		for (Map<String,Object> depMap: dependencies) {
			Object obj = depMap.get("widgetID2");
			if (obj == null || !(obj instanceof String)) continue;
			String targetId = (String) obj;
			ConfiguredWidget<?> target = widgets.get(targetId);
			String triggering = (String) depMap.get("triggeringAction");
			String triggered = (String) depMap.get("triggeredAction");
			boolean triggeringGET = triggering.equals(TriggeringAction.GET_REQUEST.getAction());
			boolean triggeringPOST = triggeringGET ? false : (triggering.equals(TriggeringAction.POST_REQUEST.getAction()) || triggering.equals(TriggeringAction.PRE_POST_REQUEST.getAction()));
			if (!triggeringGET && !triggeringPOST) continue; // could be "onclick", for instance
			boolean triggeredGET = triggered.equals(TriggeredAction.GET_REQUEST.getAction());
			boolean triggeredPOST = triggeredGET ? false : triggered.equals(TriggeredAction.POST_REQUEST.getAction());
			if (!triggeredGET && !triggeredPOST) continue;
			TriggeredAction taGet = null;
			TriggeredAction taPost = null;
			
			if (triggeringGET) {
				if (triggeredGET) 
					taGet = TriggeredAction.GET_REQUEST;
				else 
					taGet = TriggeredAction.POST_REQUEST;
				get.put(target, taGet);
			}
			else {
				if (triggeredGET) 
					taPost = TriggeredAction.GET_REQUEST;
				else 
					taPost = TriggeredAction.POST_REQUEST;
				post.put(target, taPost);
			}
		}
		if (groups == null)
			return;
		for (Map<String,Object> depMap: grpDependencies) {
			Object obj = depMap.get("groupID2");
			if (obj == null || !(obj instanceof String)) continue;
			String targetId = (String) obj;
			WidgetGroupDerived grp = groups.get(targetId); 
			if (grp == null) {
				LoggerFactory.getLogger(getClass()).error("Widget group " + targetId + " not found");
				continue;
			}
			String triggering = (String) depMap.get("triggeringAction");
			String triggered = (String) depMap.get("triggeredAction");
			boolean triggeringGET = triggering.equals(TriggeringAction.GET_REQUEST.getAction());
			boolean triggeringPOST = triggeringGET ? false : (triggering.equals(TriggeringAction.POST_REQUEST.getAction()) ||  triggering.equals(TriggeringAction.PRE_POST_REQUEST.getAction()));
			if (!triggeringGET && !triggeringPOST) continue; // could be "onclick", for instance
			boolean triggeredGET = triggered.equals(TriggeredAction.GET_REQUEST.getAction());
			boolean triggeredPOST = triggeredGET ? false : triggered.equals(TriggeredAction.POST_REQUEST.getAction());	
			if (!triggeredGET && !triggeredPOST) continue;
			TriggeredAction taGet = null;
			TriggeredAction taPost = null;
			
			if (triggeringGET) {
				if (triggeredGET) 
					taGet = TriggeredAction.GET_REQUEST;
				else 
					taGet = TriggeredAction.POST_REQUEST;
				Set<OgemaWidgetBase<?>> widgets = grp.getWidgetsImpl();
				for (OgemaWidgetBase<?> widget: widgets) {
					ConfiguredWidget<?> cw = this.widgets.get(widget.getId());
					if (cw == null) {
						LoggerFactory.getLogger(getClass()).error("Configured widget not found " + widget.getId());
						continue;
					}
					// we simply assume that a Widget is not GET-triggered both via groups AND directly... otherwise a check would be necessary
					get.put(cw, taGet);  
				}
			}
			else {
				if (triggeredGET) 
					taPost = TriggeredAction.GET_REQUEST;
				else 
					taPost = TriggeredAction.POST_REQUEST;
				Set<OgemaWidgetBase<?>> widgets = grp.getWidgetsImpl();
				for (OgemaWidgetBase<?> widget: widgets) {
					ConfiguredWidget<?> cw = this.widgets.get(widget.getId());
					if (cw == null) {
						LoggerFactory.getLogger(getClass()).error("Configured widget not found " + widget.getId());
						continue;
					}
					// we simply assume that a Widget is not POST-triggered both via groups AND directly... otherwise a check would be necessary
					post.put(cw, taPost);
				}
				
			}
		}
	}
	
	private enum DependencyStatus {
		
		NONE(0), POST_POST(1), GET_POST(2), POST_GET(3), GET_GET(4), HIGHEST(5);
		
		private final int relevance;
		
		private DependencyStatus(int relevance) {
			this.relevance = relevance;
		}
		
		public int getRelevance() {
			return relevance;
		}
	
	}
	
	private static class WidgetVisitor {
		
		private final ConfiguredWidget<?> targetWidget; // tries to find this widget in the dependecy chain of sourceWidget
		private List<DependencyStatus> status = new ArrayList<DependencyStatus>();
		
		public WidgetVisitor(ConfiguredWidget<?> targetWidget) {
			this.targetWidget = targetWidget;
		}
		
		public boolean visit(ConfiguredWidget<?> cwidget, TriggeringAction initAction, TriggeringAction followAction) { // returns true if targetWidget has been found
			if (cwidget.equals(targetWidget)) {
				status.add(getStatus(initAction, followAction));
//				return false; // same target widget might occur more than once in the tree
			}
			return true;
		}
		
		private static DependencyStatus getStatus(TriggeringAction initAction, TriggeringAction followAction) {
			if (initAction == TriggeringAction.GET_REQUEST) {
				if (followAction == TriggeringAction.GET_REQUEST)
					return DependencyStatus.GET_GET;
				else
					return DependencyStatus.GET_POST;
			}
			else if (initAction == TriggeringAction.POST_REQUEST) {
				if (followAction == TriggeringAction.GET_REQUEST)
					return DependencyStatus.POST_GET;
				else
					return DependencyStatus.POST_POST;
			}
			return null;
				
		}
		
	}
	
	private class WidgetProxy {
		
		private final ConfiguredWidget<?> widget;
		private final String id;
		private TriggeringAction initAction;
		
		public WidgetProxy(ConfiguredWidget<?> widget) {
			this.widget = widget;
			this.id = widget.getWidget().getId();
		}
		
		public WidgetProxy(ConfiguredWidget<?> widget, TriggeringAction initAction) {
			this.widget = widget;
			this.id = widget.getWidget().getId();
			this.initAction = initAction;
		}
		
		// if it returns false, the subresources of this resource will not be visited
		public boolean accept(WidgetVisitor visitor, TriggeringAction followAction) {
			return visitor.visit(widget, initAction, followAction); // if it has been found, do not continue 
		}
		
		public void depthFirstSearch(WidgetVisitor visitor) { // go for two searches, starting with GET requests and POST requests, respectively; POST includes PRE_POST
			initAction = TriggeringAction.GET_REQUEST;
			depthFirstSearch(visitor, getVisitedMap(), initAction);
			initAction = TriggeringAction.POST_REQUEST;
			depthFirstSearch(visitor, getVisitedMap(), initAction);
		}
		
		private void depthFirstSearch(WidgetVisitor visitor, Map<TriggeringAction,List<String>> visitedWidgets, TriggeringAction followAction) {
			if (visitedWidgets.get(followAction).contains(id))
				return;
			visitedWidgets.get(followAction).add(id);
			if (!accept(visitor, followAction))
				return;
			List<Map<String,Object>> dependencies1a = widget.widget.globalConnectElements;
			List<Map<String,Object>> dependencies1b = widget.widget.globalConnectGroups;
			// TODO group dependencies // -> convert group dependencies to normal dependencies, to facilitate comparison?
			setDependencyStatus(widget.widget, dependencies1a, dependencies1b); 
			
			Map<ConfiguredWidget<?>, TriggeredAction> deps; 
			if (followAction == TriggeringAction.GET_REQUEST)
				deps = GETDependencies.get(id);
			else
				deps = POSTDependencies.get(id);
			for (Map.Entry<ConfiguredWidget<?>, TriggeredAction> entry : deps.entrySet()) {
				ConfiguredWidget<?> w2 = entry.getKey();
				TriggeredAction ta = entry.getValue();
				TriggeringAction ta2 = convert(ta);
				if (ta2 != null) {
					if (w2 == null || w2.getWidget() == null) {
						LoggerFactory.getLogger(getClass()).error("Widget comparator got null widget: {}",w2);
						continue;
					}
					WidgetProxy proxy = new WidgetProxy(w2,initAction);
					proxy.depthFirstSearch(visitor, visitedWidgets, ta2);
				}
			}
			
		}
	}
	
	private static Map<TriggeringAction,List<String>> getVisitedMap() {
		Map<TriggeringAction,List<String>> map = new HashMap<TriggeringAction,List<String>>();
		map.put(TriggeringAction.GET_REQUEST, new ArrayList<String>());
		map.put(TriggeringAction.POST_REQUEST, new ArrayList<String>());
		return map;
	}
	
	private static TriggeringAction convert (TriggeredAction ta) {
		if (ta == TriggeredAction.GET_REQUEST) 
			return TriggeringAction.GET_REQUEST;
		else if (ta == TriggeredAction.POST_REQUEST) 
			return TriggeringAction.POST_REQUEST;
		else return null;
	}
	
	private static TriggeredAction convert (TriggeringAction ta) {
		if (ta == TriggeringAction.GET_REQUEST) 
			return TriggeredAction.GET_REQUEST;
		else if (ta == TriggeringAction.POST_REQUEST) 
			return TriggeredAction.POST_REQUEST;
		else return null;
	}
}
