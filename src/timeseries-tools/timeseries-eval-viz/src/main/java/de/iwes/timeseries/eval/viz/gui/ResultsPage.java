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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.resource.util.ResourceUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.OnlineEvaluation;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.TimeSeriesResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.ItemsListLabel;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.LabelledItemProvider;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.ProviderLabel;
import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.accordion.Accordion;
import de.iwes.widgets.html.accordion.AccordionData;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.emptywidget.EmptyData;
import de.iwes.widgets.html.emptywidget.EmptyWidget;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.multiselect.extended.MultiSelectExtended;
import de.iwes.widgets.template.DisplayTemplate;

public class ResultsPage {
	
	private final WidgetPage<?> page;
	private final LabelledItemProvider<EvaluationProvider> evalProviders;
	private final Header header;
	private final Alert alert;
	private final ResultInit init;
	private final Label providerLabel;
	private final Label providerDescription;
	private final Label evalInstanceId;
	private final TemplateMultiselect<ResultType> resultTypesSelector;
	private final MultiSelectExtended<ResultType> resultTypesSelectorExt;
	private final TemplateMultiselect<TimeSeriesData> inputSelector;
	private final MultiSelectExtended<TimeSeriesData> inputSelectorExt;
	private final Header resultsHeader;
	private final DynamicTable<SingleEvaluationResult> singleResultsTable;
	private final Accordion plotAccordion;
	private final PlotsSnippet plotSnippet;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResultsPage(WidgetPage<?> page, LabelledItemProvider<EvaluationProvider> evalProviders, ApplicationManager am) {
		this.page = page;
		this.evalProviders = evalProviders;
		
		this.header = new Header(page, "header", true);
		header.setDefaultText("Evaluation result details");
//		header.addDefaultStyle(HeaderData.CENTERED);
		header.setDefaultColor("blue");
		
		this.alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);
		this.init  =new ResultInit(page, "resultInit");
		
		this.providerLabel = new Label(page, "providerLabel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final EvaluationProvider instance = init.getProvider(req);
				if (instance == null) 
					setWidgetVisibility(false, req);
				else
					setText(instance.label(req.getLocale()), req);
			}
			
		};
		this.providerDescription = new Label(page, "providerDescription") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final EvaluationProvider instance = init.getProvider(req);
				if (instance == null) 
					setWidgetVisibility(false, req);
				else
					setText(instance.description(req.getLocale()), req);
			}
			
		};
		this.evalInstanceId = new Label(page, "evalInstanceId") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final EvaluationInstance instance = init.getInstance(req);
				if (instance == null) 
					setWidgetVisibility(false, req);
				else
					setText(instance.id(), req);
			}
			
		};
		this.resultTypesSelector = new TemplateMultiselect<ResultType>(page, "resultTypesSelector") {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				final Collection<ResultType> types = init.getResultTypes(req);
				if (types != null)
					update(types,req);
			}
			
		};
		resultTypesSelector.setTemplate((DisplayTemplate) LabelledItemUtils.LABELLED_ITEM_TEMPLATE);
		resultTypesSelectorExt = new MultiSelectExtended<>(page, "resultTypesSelectorExt", resultTypesSelector);
		this.inputSelector = new TemplateMultiselect<TimeSeriesData>(page, "inputSelect") {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				final Map<ReadOnlyTimeSeries,TimeSeriesData> types = init.getInputData(req);
				if (types == null)
					return;
				update(types.values(),req);
			}
			
		};
		inputSelector.setTemplate((DisplayTemplate) LabelledItemUtils.LABELLED_ITEM_TEMPLATE);
		inputSelectorExt = new MultiSelectExtended<>(page, "inputSelectorExt",inputSelector);
		this.resultsHeader = new Header(page, "resultsHeader", true);
		resultsHeader.setDefaultText("Individual results");
		resultsHeader.setDefaultColor("blue");
		resultsHeader.setDefaultHeaderType(3);
		this.singleResultsTable = new DynamicTable<SingleEvaluationResult>(page, "singleResultsTable") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				final EvaluationInstance instance = init.getInstance(req);
				if (instance == null) {
					setWidgetVisibility(false, req);
					return;
				}
				final List<SingleEvaluationResult> results = new ArrayList<>();
				final Map<ResultType, EvaluationResult> map;
				if (instance.isDone()) 
					map = instance.getResults();
				else if (instance instanceof OnlineEvaluation) 
					map = ((OnlineEvaluation) instance).getIntermediateResults();
				else {
					updateRows(Collections.<SingleEvaluationResult> emptyList(), req);
					return;
				}
				//TODO: Quick hack:
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				final List<ResultType> typesList = resultTypesSelector.getSelectedItems(req);
				final List<TimeSeriesData> inputList = inputSelector.getSelectedItems(req); 
				for (Map.Entry<ResultType, EvaluationResult> entry : map.entrySet()) {
					if (!typesList.isEmpty() && !typesList.contains(entry.getKey()))
						continue;
					for (SingleEvaluationResult sv : entry.getValue().getResults()) {
						if (!inputList.isEmpty() && Collections.disjoint(inputList, sv.getInputData()))
							continue;
						results.add(sv);
					}
				}
				updateRows(results, req);
			}
			
		};
		final RowTemplate<SingleEvaluationResult> template = new RowTemplate<SingleEvaluationResult>() {
			
			private final Cache<SingleEvaluationResult, Integer> ids = CacheBuilder.newBuilder().weakKeys().build();
			private int cnt = 0;
			private final Map<String, Object> header;
			
			{
				final Map<String,Object> header = new LinkedHashMap<>();
				header.put("type", "Result type");
				header.put("input", "Input data");
				header.put("results", "Results");
				this.header = Collections.unmodifiableMap(header);
			}
			
			@Override
			public Row addRow(final SingleEvaluationResult result, final OgemaHttpRequest req) {
				final Row row = new Row();
				final EvaluationProvider provider = init.getProvider(req);
				final EvaluationInstance instance = init.getInstance(req);
				if (provider == null || instance == null)
					return null;
				final String lineId  = getLineId(result);
				final String prefix = ResourceUtils.getValidResourceName(provider.id() + "__" + instance.id() + lineId);
				final ProviderLabel pl = new ProviderLabel(singleResultsTable, prefix + "_labelS", req, 
						result.getResultType(), true);
				final ItemsListLabel inputList = new ItemsListLabel(singleResultsTable, prefix + "_inputS", req,
						result.getInputData());
				final Label resultLabel = new Label(singleResultsTable, prefix + "_resultS", req);
				if (result instanceof SingleValueResult) {
					final Object resultValue = ((SingleValueResult<?>) result).getValue();
					resultLabel.setText(String.valueOf(resultValue), req);
				} else if (result instanceof TimeSeriesResult) {
					resultLabel.setText("Result is a time series...", req); // TODO
				} else if (result instanceof SingleEvaluationResult.ArrayResult) {
					final List<String> resultLabels = ((SingleEvaluationResult.ArrayResult) result).getLabels(req.getLocale());
					final Iterator<String> labelsIt = resultLabels == null ? null : resultLabels.iterator();
					boolean first = true;
					final StringBuilder sb =new StringBuilder();
					for (SingleEvaluationResult sev : ((SingleEvaluationResult.ArrayResult) result).getValues()) {
						if (!first)
							sb.append("<br>");
						first = false;
						if (labelsIt != null && labelsIt.hasNext())
							sb.append(labelsIt.next()).append(':').append(' ');
						if (sev instanceof SingleValueResult<?>) {
							sb.append(StringEscapeUtils.escapeHtml4(((SingleValueResult) sev).getValue().toString()));
						} else {
							sb.append(StringEscapeUtils.escapeHtml4(sev.toString()));
						}
					}
                    resultLabel.setHtml(sb.toString(), req);
                }
				row.addCell("type", pl, 1);
				row.addCell("input", inputList, 2);
				row.addCell("results", resultLabel, 2);
				return row;
			}

			@Override
			public String getLineId(SingleEvaluationResult object) {
				Integer id = ids.getIfPresent(object);
				if (id == null) {
					synchronized (ids) {
						id = ids.getIfPresent(object);
						if (id == null) 
							id = cnt++;
					}
				}
				return "_" + id;
			}

			@Override
			public Map<String, Object> getHeader() {
				return header;
			}
			
		};
		singleResultsTable.setRowTemplate(template);
		
		this.plotSnippet= new PlotsSnippet(page, "plotSnippet", am, init);
		this.plotAccordion = new Accordion(page, "plotAccordion", true);
		plotAccordion.addItem("Show plots", plotSnippet, null);
		plotAccordion.addDefaultStyle(AccordionData.BOOTSTRAP_LIGHT_BLUE);
		
		buildPage();
		setDependencies();
	}
	
	private final void buildPage() {
		final StaticTable tab = new StaticTable(5, 3, new int[]{2,2,8}) // TODO add style="border:none;" to last column
				.setContent(0, 0, "Evaluation provider").setContent(0, 1, providerLabel)
				.setContent(1, 0, "Provider description").setContent(1, 1, providerDescription)
				.setContent(2, 0, "Evaluation instance").setContent(2, 1, evalInstanceId)
				.setContent(3, 0, "Result types").setContent(3, 1, resultTypesSelectorExt)
				.setContent(4, 0, "Input data").setContent(4, 1, inputSelectorExt);
		page.append(header).linebreak().append(alert).append(init).linebreak()
			.append(tab)
			.append(resultsHeader).append(singleResultsTable).linebreak()
			.append(plotAccordion);
	}
	
	private final void setDependencies() {
		init.triggerAction(alert, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		init.triggerAction(providerLabel, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		init.triggerAction(providerDescription, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		init.triggerAction(evalInstanceId, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		init.triggerAction(resultTypesSelector, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		init.triggerAction(inputSelector, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		init.triggerAction(plotSnippet, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		init.triggerAction(singleResultsTable, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST,1);
		resultTypesSelector.triggerAction(singleResultsTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		inputSelector.triggerAction(singleResultsTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	class ResultInit extends EmptyWidget implements InitWidget {

		private static final long serialVersionUID = 1L;

		public ResultInit(WidgetPage<?> page, String id) {
			super(page, id);
		}
		
		@Override
		public EmptyData createNewSession() {
			return new ResultData(this);
		}
	
		public EvaluationInstance getInstance(OgemaHttpRequest req) {
			return ((ResultData) getData(req)).instance;
		}
		
		public EvaluationProvider getProvider(OgemaHttpRequest req) {
			return ((ResultData) getData(req)).provider;
		}
		
		public Map<ReadOnlyTimeSeries, TimeSeriesData> getInputData(OgemaHttpRequest req) {
			return ((ResultData) getData(req)).inputData;
		}
		
		public List<SingleEvaluationResult> getSingleResults(OgemaHttpRequest req) {
			return ((ResultData) getData(req)).singleResults;
		}
		
		public Set<ResultType> getResultTypes(OgemaHttpRequest req) {
			return ((ResultData) getData(req)).resultTypes;
		}
		
		@Override
		public void init(OgemaHttpRequest req) {
			final String providerId;
			final String resultId;
			final Map<String,String[]> params = page.getPageParameters(req);
			try {
				providerId = URLDecoder.decode(params.get("provider")[0], "UTF-8");
				resultId = URLDecoder.decode(params.get("instance")[0], "UTF-8");
			} catch (UnsupportedEncodingException | NullPointerException | ArrayIndexOutOfBoundsException e) {
				return;
			}
			EvaluationProvider provider = null;
			for (EvaluationProvider ep : evalProviders.getItems()) {
				if (ep.id().equals(providerId)) {
					provider = ep;
					break;
				}
			}
			final EvaluationInstance instance = provider != null ? provider.getEvaluation(resultId) : null;
			if (instance == null) {
				alert.setText(provider == null ? "Provider not found: " + providerId : "Evaluation not found: " + resultId, req);
				alert.setWidgetVisibility(true, req);
				alert.setStyle(AlertData.BOOTSTRAP_DANGER, req);
				return;
			}
			final ResultData data  = (ResultData) getData(req);
			data.instance = instance;
			data.provider = provider;
			if (instance != null) {
				final Set<ResultType> resultTypes = new HashSet<>();
				final Map<ReadOnlyTimeSeries, TimeSeriesData> inputData = new HashMap<>();
				for (TimeSeriesData tsd : instance.getInputData()) {
					inputData.put(((TimeSeriesDataOffline)tsd).getTimeSeries(), (TimeSeriesData) tsd);
				}
				data.inputData = inputData;
				final List<SingleEvaluationResult> singleResults = new ArrayList<>();
				final Collection<EvaluationResult> r;
				if (instance.isDone()) 
					r = instance.getResults().values();
				else if (instance instanceof OnlineEvaluation)
					r = ((OnlineEvaluation) instance).getIntermediateResults().values();
				else
					throw new IllegalArgumentException("Requested evaluation is not finished yet");
				for (EvaluationResult evR : r) {
					for (SingleEvaluationResult ser : evR.getResults()) {
						singleResults.add(ser);
					}
					resultTypes.add(evR.getResultType());
				}
				data.singleResults = singleResults;
				data.resultTypes = resultTypes;
			} 				
		}

	}
	
	private static class ResultData extends EmptyData {
		
		private EvaluationInstance instance;
		private EvaluationProvider provider;
		private Map<ReadOnlyTimeSeries, TimeSeriesData> inputData;
		private List<SingleEvaluationResult> singleResults;
		private Set<ResultType> resultTypes;

		public ResultData(ResultInit empty) {
			super(empty);
		}
		
	}
	
	
}
