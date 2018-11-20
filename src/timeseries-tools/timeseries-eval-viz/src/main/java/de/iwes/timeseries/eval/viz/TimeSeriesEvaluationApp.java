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
package de.iwes.timeseries.eval.viz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.EvaluationManager;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.LabelledItemProvider;
import de.iwes.timeseries.eval.viz.gui.ResultsPage;
import de.iwes.timeseries.eval.viz.gui.SelectionTreePage;
import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.navigation.NavigationMenu;

@References({
	@Reference(
			name="sources",
			referenceInterface=DataProvider.class,
			cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
			policy=ReferencePolicy.DYNAMIC,
			bind="addSource",
			unbind="removeSource"),
	@Reference(
			name="evaluations",
			referenceInterface=EvaluationProvider.class,
			cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
			policy=ReferencePolicy.DYNAMIC,
			bind="addEvalProvider",
			unbind="removeEvalProvider")
})
@Component(specVersion = "1.2")
@Service(Application.class)
public class TimeSeriesEvaluationApp implements Application {


	public final static Logger logger = LoggerFactory.getLogger(TimeSeriesEvaluationApp.class);
//	private ApplicationManager appMan;
	private WidgetApp wapp;
	private final Map<String, DataProvider<?>> sources = Collections.synchronizedMap(new LinkedHashMap<String, DataProvider<?>>());
	private final Map<String,EvaluationProvider> evaluations = Collections.synchronizedMap(new LinkedHashMap<String,EvaluationProvider>());
	private final AtomicInteger sourcesRevision = new AtomicInteger(0);
	private final AtomicInteger evalRevision = new AtomicInteger(0);
	private final LabelledItemProvider<DataProvider<?>> sourceProviders = new LabelledItemProvider<DataProvider<?>>() {

		@Override
		public int getRevision() {
			return sourcesRevision.get();
		}

		@Override
		public List<DataProvider<?>> getItems() {
			synchronized (sources) {
	    		return new ArrayList<>(sources.values());
			}
		}
	};
	
	private final LabelledItemProvider<EvaluationProvider> evalProviders = new LabelledItemProvider<EvaluationProvider>() {

		@Override
		public int getRevision() {
			return evalRevision.get();
		}

		@Override
		public List<EvaluationProvider> getItems() {
			synchronized (evaluations) {
	    		return new ArrayList<>(evaluations.values());
			}
		}
	};
	
	@Reference
	private EvaluationManager evalManager;

    @Reference
    private OgemaGuiService widgetService;
    
    protected void addEvalProvider(EvaluationProvider provider) {
    	evaluations.put(provider.id(), provider);
    	evalRevision.incrementAndGet();
    }
    
    protected void removeEvalProvider(EvaluationProvider provider) {
    	evaluations.remove(provider.id());
    	evalRevision.incrementAndGet();
    }
    
    protected void addSource(DataProvider<?> source) {
    	sources.put(source.id(), source);
    	sourcesRevision.incrementAndGet();
    }
    
    protected void removeSource(DataProvider<?> source) {
    	sources.remove(source.id());
    	sourcesRevision.incrementAndGet();
    }
    
	@Override
	public void start(ApplicationManager appManager) {
//		this.appMan = appManager;
		this.wapp = widgetService.createWidgetApp("/de/iwes/tools/timeseries/analysis", appManager);
		final WidgetPage<?> page = wapp.createStartPage();
		final WidgetPage<?> resultsPage = wapp.createWidgetPage("results.html");
		new ResultsPage(resultsPage, evalProviders, appManager);
		new SelectionTreePage(page, evalManager, sourceProviders, evalProviders, resultsPage);
		logger.info("{} started",getClass().getName());
		
		// menu
		final NavigationMenu menu = new NavigationMenu(" Browse pages");
		menu.addEntry("Main page", page);
		page.getMenuConfiguration().setCustomNavigation(menu);
	}

    @Override
	public void stop(AppStopReason reason) {
    	if (wapp != null)
    		wapp.close();
    	wapp = null;
//    	appMan = null;
		logger.info("{} closing down",getClass().getName());
	}

}