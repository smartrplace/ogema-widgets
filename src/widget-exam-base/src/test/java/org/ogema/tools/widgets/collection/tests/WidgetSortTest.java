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

package org.ogema.tools.widgets.collection.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.ogema.tools.widgets.test.base.DependencyWidget;
import org.ogema.tools.widgets.test.base.WidgetLoader;
import org.ogema.tools.widgets.test.base.WidgetsTestBaseMin;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.html.form.textfield.TextField;

@ExamReactorStrategy(PerClass.class)
public class WidgetSortTest extends WidgetsTestBaseMin {
	
	private static final Random rand = new Random();
	
	public WidgetSortTest() {
		super(false);
	}
	
	private static final void addRandomDependencyType(DependencyWidget<?> base, DependencyWidget<?> target) {
		TriggeringAction a;
		TriggeredAction b;
		switch (rand.nextInt(4)) {
		case 0:
			a = TriggeringAction.POST_REQUEST;
			b = TriggeredAction.GET_REQUEST;
			break;
		case 1:
			a = TriggeringAction.PRE_POST_REQUEST;
			b = TriggeredAction.POST_REQUEST;
			break;
		case 2:
			a = TriggeringAction.GET_REQUEST;
			b = TriggeredAction.GET_REQUEST;
			break;
		case 3:
			a = TriggeringAction.POST_REQUEST;
			b = TriggeredAction.POST_REQUEST;
			break;
		default:
			throw new RuntimeException();
		}
		base.addDependency(target, a, b);
	}
	
	// dependecy between levels
	private static final void createRandomDependency(List<List<DependencyWidget<?>>> widgets, DependencyWidget<?> base, int levelBase) {
		int level = rand.nextInt(levelBase);
		List<DependencyWidget<?>> targets = widgets.get(level);
		DependencyWidget<?> target = targets.get(rand.nextInt(targets.size()));
		addRandomDependencyType(base, target);
	}
	
	// dependency within level
	private static final void addSameLevelDependencies(List<DependencyWidget<?>> widgets) {
		for (int k=1; k<widgets.size(); k++) {
			if (Math.random() < 0.7) 
				continue;
			int m = rand.nextInt(k);
			addRandomDependencyType(widgets.get(k), widgets.get(m));
		}
	}
	
	
	/**
	 * Creates a set of widgets, grouped into dependency levels, and introduces random dependencies between them. 
	 * @param page
	 * @param nrWidgets
	 * @param nrDependencyLevels
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final List<List<DependencyWidget<TextField>>> setupRandomWidgetsDependencyChain(WidgetPage<?> page, int nrWidgets, int nrDependencyLevels) {
		List<List<DependencyWidget<TextField>>> widgets = new ArrayList<>();
		int avNrWidgetsPerLevel = Math.max(1, nrWidgets/nrDependencyLevels);
		for (int i=0; i<nrDependencyLevels; i++) {
			List<DependencyWidget<TextField>> levelWidgets = new ArrayList<>();
			for (int j=0; j<avNrWidgetsPerLevel; j++) {
				String id =  "_" + i + "_"+ j;
				TextField tf = new TextField(page, id, id);
				page.append(tf);
				DependencyWidget<TextField> dw = new DependencyWidget<TextField>(tf);
				levelWidgets.add(dw);
				if (i > 0 && rand.nextBoolean())
					createRandomDependency((List) widgets, dw, i);
			}
			widgets.add(levelWidgets);
			addSameLevelDependencies((List) levelWidgets); 
		}
		return widgets;
	}
	
	private static final List<DependencyWidget<TextField>> createSimpleTextFields(WidgetPage<?> page, int nr) {
		List<DependencyWidget<TextField>> list = new ArrayList<>();
		for (int i=0; i<nr; i++) {
			TextField tf = new TextField(page, "_" + i);
			page.append(tf);
			list.add(new DependencyWidget<TextField>(tf));
		}
		return list;
	}
	
	private void advancedSortWorks(int nrWidgets, int nrDependencyLevels) throws ClientProtocolException, IOException, InterruptedException, ExecutionException, TimeoutException, ParseException, URISyntaxException {
		WidgetPage<?> page = widgetApp.createWidgetPage(nextUrl());
		List<List<DependencyWidget<TextField>>> widgets = setupRandomWidgetsDependencyChain(page, nrWidgets, nrDependencyLevels);
		WidgetLoader client = new WidgetLoader(page, HTTP_PORT);
		client.connect(); // important, otherwise widgets are not sorted
		List<OgemaWidget> sortedWidgets = adminService.getPageWidgets(page);
		for (List<DependencyWidget<TextField>> list: widgets) {
			for (DependencyWidget<TextField> widget: list) {
				widget.verifyOrdering(sortedWidgets);
			}
		}
		Future<?> f = client.close();
		for (List<DependencyWidget<TextField>> list: widgets) {
			for (DependencyWidget<TextField> widget: list) {
				widget.getWidget().destroyWidget();
			}
		}
		f.get(5,TimeUnit.SECONDS);
	}
	
	private void basicSortWorks(int nr) throws ClientProtocolException, IOException, InterruptedException, ExecutionException, TimeoutException, ParseException, URISyntaxException {
		WidgetPage<?> page = widgetApp.createWidgetPage(nextUrl());
		List<DependencyWidget<TextField>> widgets = createSimpleTextFields(page, nr);
		for (int i= 1; i<nr; i++) {
			if (i==1 || rand.nextBoolean()) 
				addRandomDependencyType(widgets.get(i), widgets.get(rand.nextInt(i)));
		}
		WidgetLoader client = new WidgetLoader(page, HTTP_PORT);
		client.connect(); // important, otherwise widgets are not sorted
		List<OgemaWidget> sortedWidgets = adminService.getPageWidgets(page);
		for (DependencyWidget<TextField> widget: widgets) {
			widget.verifyOrdering(sortedWidgets);
		}
		Future<?> f = client.close();
		for (DependencyWidget<?> dw: widgets) {
			dw.getWidget().destroyWidget();
		}
		f.get(5,TimeUnit.SECONDS);
	}
	
	@Test
	public void basicSortWorks() throws ClientProtocolException, IOException, InterruptedException, ExecutionException, TimeoutException, ParseException, URISyntaxException {
		basicSortWorks(2);
		basicSortWorks(20);
		basicSortWorks(100);
	}

	@Test
	public void sortWorks() throws ClientProtocolException, IOException, InterruptedException, ExecutionException, TimeoutException, ParseException, URISyntaxException {
		advancedSortWorks(10,2);
		advancedSortWorks(50,5);
		advancedSortWorks(50,2);
		advancedSortWorks(100, 5);
		advancedSortWorks(20,10);
		advancedSortWorks(10,10);
	}
	
}
