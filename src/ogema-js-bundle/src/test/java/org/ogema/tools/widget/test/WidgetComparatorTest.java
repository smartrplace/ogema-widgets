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
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur F�rderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS
 * Fraunhofer ISE
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.tools.widget.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.resourcemanager.ResourceManagement;
import org.ogema.tools.widget.test.tools.TestWidget;
import org.ogema.tools.widget.test.tools.WidgetGroupImpl;
import org.ogema.tools.widget.test.tools.WidgetsTestBase;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetAppImpl;
import de.iwes.widgets.api.extended.WidgetComparator;
import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.extended.WidgetPageImpl;
import de.iwes.widgets.api.extended.xxx.ConfiguredWidget;
import de.iwes.widgets.api.extended.xxx.WidgetGroupDerived;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;

public class WidgetComparatorTest extends WidgetsTestBase {

	private ResourceManagement rm;
	private ResourceAccess ra;
	private WidgetAppImpl wa;
	private WidgetPageBase<?> page1;
	private WidgetPageImpl<?> simplePage1;
	private static final String WIDGET_ID_PREFIX = "widget";

	@Before
	public void setup() {
		rm = getApplicationManager().getResourceManagement();
		ra = getApplicationManager().getResourceAccess();
		BundleContext ctx =  FrameworkUtil.getBundle(getClass()).getBundleContext();
		OgemaGuiService widgetService = ctx.getService(ctx.getServiceReference(OgemaGuiService.class));
		if (widgetService == null) throw new RuntimeException("Widget service reference null");
		wa = new WidgetAppImpl("/a/b/c", widgetService, getApplicationManager());
		page1 = new WidgetPageBase<LocaleDictionary>(wa, "page1.html");
		simplePage1 = new WidgetPageImpl<LocaleDictionary>(wa, "simplePage1.html");
	}
	
	@After 
	public void cleanUp() {
		wa.close();
	}
	
	@Test
	public void widgetComparatorTestWithoutGroups() {
		widgetComparatorTestWithoutGroupsFixedNr(10);
		widgetComparatorTestWithoutGroupsFixedNr(50);
		widgetComparatorTestWithoutGroupsFixedNr(100);
	}
	
	public void widgetComparatorTestWithoutGroupsFixedNr(int nrWidgets) {
		Map<String,TestWidget> twidgets = createTestWidgets(nrWidgets);
		addDependencies(twidgets);
		Map<String,ConfiguredWidget<?>> widgets = getConfiguredWidgets(twidgets);
		List<ConfiguredWidget<?>> cwidgets = new ArrayList<ConfiguredWidget<?>>(widgets.values());
		long time0 = System.nanoTime();
		WidgetComparator comparator = new WidgetComparator(widgets, Collections.<String, WidgetGroupDerived> emptyMap());
		Collections.sort(cwidgets,comparator);
		long diffMs = (System.nanoTime() - time0)/1000000;
		System.out.println("  ooo Ordering of " + nrWidgets + " widgets with several dependencies took " + diffMs + " ms");
		System.out.println(" Ordered widgets: " + cwidgets);
		for (int i = 0; i< nrWidgets;i++) {
			assert (cwidgets.get(i).getWidget().equals(twidgets.get(WIDGET_ID_PREFIX + (nrWidgets-i-1)))) : "Widget ordering incorrect"; // widget with highest id must come first
		}
		for (TestWidget t: twidgets.values()) {
			t.destroyWidget(); // assure serlvets will be available again
		}
	}
	
	@Test
	public void simpleGroupDependenciesWork() {
		Map<String,TestWidget> twidgets = createTestWidgets(3);
		Map<String,ConfiguredWidget<?>> widgets = getConfiguredWidgets(twidgets);
		TestWidget trigger = new TestWidget(simplePage1, "testTriggerWidget");
		WidgetGroupDerived group = addGroupDependency(trigger, (Collection) twidgets.values(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		Map<String, WidgetGroupDerived> grps = new HashMap<String, WidgetGroupDerived>();
		grps.put(group.getId(), group);
		List<ConfiguredWidget<?>> cwidgets = new ArrayList<ConfiguredWidget<?>>(widgets.values());
		cwidgets.add(new ConfiguredWidget(trigger, "dummyPath", "dummyPath", null));
		WidgetComparator comparator = new WidgetComparator(widgets, grps);
		Collections.sort(cwidgets, comparator);
		Assert.assertEquals("Sorting of widgets according to group dependencies failed",trigger,cwidgets.get(0).getWidget()); // triggering widget must be first
		trigger.destroyWidget();
		for (TestWidget t: twidgets.values()) {
			t.destroyWidget(); // assure serlvets will be available again
		}
	}
	
	private Map<String,TestWidget> createTestWidgets(int nr) {
		Map<String,TestWidget> list = new HashMap<String, TestWidget>();
		for (int i=0;i<nr;i++) {
			TestWidget t = new TestWidget(simplePage1, WIDGET_ID_PREFIX + i);
			list.put(t.getId(),t);
		}
		return list;
	}
	
	private static Map<String,ConfiguredWidget<?>> getConfiguredWidgets(Map<String,? extends OgemaWidgetBase<?>> list) {
		final Map<String,ConfiguredWidget<?>> map = new HashMap<String,ConfiguredWidget<?>>();
		for (OgemaWidgetBase<?> w: list.values()) {
			map.put(w.getId(), new ConfiguredWidget(w, "dummyPath", "dummyPath", null));
		}
		return map;
	}
	
	private static WidgetGroupImpl addGroupDependency(OgemaWidget triggerWidget, Collection<OgemaWidget> dependencies, TriggeringAction trigger, TriggeredAction triggered) {
		final WidgetGroupImpl groupImpl = new WidgetGroupImpl("testGroup", dependencies);
		triggerWidget.triggerAction(groupImpl, trigger, triggered);
		return groupImpl;
	}
	
	/**  dependency: widget (n+1) triggers widget (n) */
	private static void addDependencies(Map<String,? extends OgemaWidgetBase<?>> widgets) {
		for (int i=widgets.size()-1;i>=1;i--) {
			OgemaWidgetBase<?> w1 = widgets.get(WIDGET_ID_PREFIX + i);
			OgemaWidgetBase<?> w2 = widgets.get(WIDGET_ID_PREFIX +(i-1));
			TriggeringAction trigger;
			TriggeredAction triggered;
			int md = i % 4;
			switch(md) {
			case 0: 
				trigger = TriggeringAction.GET_REQUEST;
				triggered = TriggeredAction.POST_REQUEST;
				break;
			case 1:
				trigger = TriggeringAction.GET_REQUEST;
				triggered = TriggeredAction.GET_REQUEST;
				break;
			case 2: 
				trigger = TriggeringAction.POST_REQUEST;
				triggered = TriggeredAction.GET_REQUEST;
				break;
			case 3: 
				trigger = TriggeringAction.POST_REQUEST;
				triggered = TriggeredAction.POST_REQUEST;
				break;
			default:
				throw new RuntimeException("Invalid modulus");
			}
			w1.triggerAction(w2, trigger, triggered);
		}
		
	}
	
}
