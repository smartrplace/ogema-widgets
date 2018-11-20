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
package de.iwes.timeseries.eval.viz.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.EvaluationManager;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.viz.TimeSeriesEvaluationApp;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.LabelledItemProvider;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.accordion.TemplateAccordion;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.template.PageSnippetTemplate;

public class SelectionTreePage {
	
	private final WidgetPage<?> page;
	private final Header header;
	private final Alert alert;
//	private final Header dataSourceHeader;
//	private final DynamicTable<DataProvider<?>> dataProviderTable;
	private final Header evalProviderHeader;
	private final TemplateAccordion<EvaluationProvider> evalProviderAccordion;
	private final EvaluationManager evalMan;
//	private final DynamicTable<EvaluationProvider> evalProviderTable;
	
	public SelectionTreePage(
			final WidgetPage<?> page, 
			final EvaluationManager evalMan,
			final LabelledItemProvider<DataProvider<?>> sourcesProvider,
			final LabelledItemProvider<EvaluationProvider> evalProvider,
			final WidgetPage<?> resultsPage) {
		this.page = page;
		this.evalMan = evalMan;
		this.header = new Header(page, "header");
		header.setDefaultText("OGEMA time series analysis");
		header.addDefaultStyle(HeaderData.CENTERED);
		header.setDefaultColor("blue");

		this.alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);
		
/*		this.dataSourceHeader = new Header(page, "dataSourceHeader");
		dataSourceHeader.setDefaultHeaderType(3);
		dataSourceHeader.setDefaultText("Select data sources");
		dataSourceHeader.addDefaultStyle(HeaderData.CENTERED);
		dataSourceHeader.setDefaultColor("blue");
		
		this.dataProviderTable = new DynamicTable<DataProvider<?>>(page, "providerTable", true) {

			private static final long serialVersionUID = 1L;
			private volatile int revision = -1;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (revision == sourcesProvider.getRevision())
					return;
				synchronized (this) {
					if (revision == sourcesProvider.getRevision())
						return;
					revision = sourcesProvider.getRevision();
					updateRows(sourcesProvider.getItems(), req);
				}
			}
			
		};
		RowTemplate<DataProvider<?>> providerTemplate = new RowTemplate<DataProvider<?>>() {

			@Override
			public Row addRow(final DataProvider<?> object, final OgemaHttpRequest req) {
				final Row row = new Row();
				final String id = getLineId(object);
				final Label lab = new ProviderLabel(page, id + "_sourceLabel", object, true);
				final Label description = new ProviderLabel(page, id + "_sourceDescription", object, false);
				final SelectionTree tree = new SelectionTree(page, id + "_selectionTree", true);
				tree.setSelectionOptions(Arrays.asList(object.selectionOptions()), null);
				row.addCell("label", lab, 1);
				row.addCell("description", description, 2);
				row.addCell("tree", tree, 9);
				return row;
			}

			@Override
			public String getLineId(DataProvider<?> object) {
				return object.id();
			}

			@Override
			public Map<String, Object> getHeader() {
				return null;
			}
			
		};
		dataProviderTable.setRowTemplate(providerTemplate);
		*/
		
		this.evalProviderHeader = new Header(page, "evalProviderHeader");
		evalProviderHeader.setDefaultHeaderType(3);
		evalProviderHeader.setDefaultText("Evaluation providers");
		evalProviderHeader.addDefaultStyle(HeaderData.CENTERED);
		evalProviderHeader.setDefaultColor("blue");
		
//		this.evalProviderTable = new DynamicTable<>(page, "evalProviderTable", true);
//		RowTemplate<EvaluationProvider> evalTemplate = new RowTemplate<EvaluationProvider>() {
//
//			@Override
//			public Row addRow(final EvaluationProvider object, final OgemaHttpRequest req) {
//				final Row row = new Row();
//				final String id = getLineId(object);
//				final Label lab = new ProviderLabel(page, id + "_evalLabel", object, true);
//				final Label description = new ProviderLabel(page, id + "_evalDescription", object, false);
//				
//				object.inputDataTypes()
//				
//				return row;
//			}
//
//			@Override
//			public String getLineId(EvaluationProvider object) {
//				return object.id();
//			}
//
//			@Override
//			public Map<String, Object> getHeader() {
//				return null;
//			}
//			
//		};
//		evalProviderTable.setRowTemplate(evalTemplate);
		final PageSnippetTemplate<EvaluationProvider> evalTemplate = new PageSnippetTemplate<EvaluationProvider>() {

			@Override
			public String getId(EvaluationProvider object) {
				return object.id();
			}

			@Override
			public String getLabel(EvaluationProvider object, OgemaLocale locale) {
				return object.label(locale);
			}

			@Override
			public PageSnippetI getSnippet(EvaluationProvider item, OgemaHttpRequest req) {
				try {
					return new EvalProviderPageSnippet(page, getId(item) + "_snippet", evalMan, item, sourcesProvider, alert, resultsPage);
				} catch (Exception e) {
					TimeSeriesEvaluationApp.logger.debug("Error creating eval snippet",e);;
					return null;
				}
			}
		};
		this.evalProviderAccordion = new TemplateAccordion<EvaluationProvider>(page, "evalProviderAccordion", true, evalTemplate) {
			
			private static final long serialVersionUID = 1L;
			private volatile int revision = -1;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (revision == evalProvider.getRevision())
					return;
				synchronized (this) {
					if (revision == evalProvider.getRevision())
						return;
					revision = evalProvider.getRevision();
					final List<EvaluationProvider> newProviders = evalProvider.getItems();
					final List<EvaluationProvider> copy =new ArrayList<>(newProviders.size());
					for (EvaluationProvider provider : newProviders) {
						if (providerAdmissible(provider))
							copy.add(provider);
					}
					update(copy, req);
				}
			}
			
		};
		
		buildPage();
		setDependencies();
		
	}
	
	private final void buildPage() {
		page.append(header).linebreak().append(alert).linebreak()
//			.append(dataSourceHeader).linebreak().append(dataProviderTable)
			.append(evalProviderHeader).linebreak().append(evalProviderAccordion);
		
	}
	
	private final void setDependencies() {
		// GET is triggered by posts of other select widgets
//		resourceTree.getTerminalSelectWidget(null).triggerAction(schedulePlot, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
//		resourceTree.getTerminalSelectWidget(null).triggerAction(resourceTree.getTerminalSelectWidget(null), 
//				TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	
	}
	
	private final static boolean providerAdmissible(final EvaluationProvider provider) {
		try {
			final Set<String> ids = new HashSet<>(8);
			for (RequiredInputData in : provider.inputDataTypes()) {
				if (!ids.add(in.id())) {
					TimeSeriesEvaluationApp.logger.warn("Provider {} requests duplicate input data id {}; will be filtered out", provider.id(), in.id());
					return false;
				}
			}
			ids.clear();
			for (ResultType result : provider.resultTypes()) {
				if (!ids.add(result.id())) {
					TimeSeriesEvaluationApp.logger.warn("Provider {} offers duplicate result id {}; will be filtered out", provider.id(), result.id());
					return false;
				}
			}
			return true;
		} catch (Exception | AbstractMethodError e) {
			LoggerFactory.getLogger(SelectionTreePage.class).warn("Evaluation provider {} throws exceptions: {}", provider, e.toString());
			return false;
		}
	}
	
}
