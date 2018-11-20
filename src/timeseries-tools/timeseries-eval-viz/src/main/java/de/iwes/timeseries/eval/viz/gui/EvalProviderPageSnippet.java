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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ogema.tools.resource.util.ResourceUtils;
import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationManager;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.OnlineEvaluation;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.ResultType.ResultStructure;
import de.iwes.timeseries.eval.api.ResultType.ValueType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.ArrayResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.Status.EvaluationStatus;
import de.iwes.timeseries.eval.viz.TimeSeriesEvaluationApp;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.ItemsListLabel;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.LabelledItemProvider;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.ProviderLabel;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.RedirectButton;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.multiselect.extended.MultiSelectExtended;
import de.iwes.widgets.template.DisplayTemplate;

// TODO show finished and active evaluations (online/offline)
// TODO load evaluations lazily when tab is opened
public class EvalProviderPageSnippet extends PageSnippet {

	private static final long serialVersionUID = 1L;
	private final ProviderLabel label;
	private final ProviderLabel description;
	private final TemplateMultiselect<ResultType> resultTypeSelector;
	private final MultiSelectExtended<ResultType> resultTypeSelectorExt;
	private final Button newEvalSubmit;
	private final SourceSelectorPopup sourceSelectorPopup;
	private final Header ongoingHeader;
	private final Header finishedHeader;
	private final DynamicTable<EvaluationInstance> onlineOngoingEvalTable;
	private final DynamicTable<EvaluationInstance> onlineFinishedTable;
//	private final DynamicTable<OfflineEvaluation> offlineFinishedTable;
//	private final DynamicTable<OfflineEvaluation> offlineOngoingTable;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public EvalProviderPageSnippet(final WidgetPage<?> page, final String id, final EvaluationManager evalMan, final EvaluationProvider provider, 
				final LabelledItemProvider<DataProvider<?>> dataProviders, final Alert alert, final WidgetPage<?> resultsPage) {
		super(page, id, true);
		this.label = new ProviderLabel(page, id + "_evalLabel", provider, true);
		this.description = new ProviderLabel(page, id + "_evalDescription", provider, false);
		this.resultTypeSelector = new TemplateMultiselect<>(page, id + "_resultTypeSelector");
		resultTypeSelector.setTemplate((DisplayTemplate) LabelledItemUtils.LABELLED_ITEM_TEMPLATE); 
		resultTypeSelector.selectDefaultItems(provider.resultTypes());
		resultTypeSelector.setDefaultSelectedItems(provider.resultTypes());
		resultTypeSelectorExt = new MultiSelectExtended<>(page, id+"_resultTypeSelectorExt", resultTypeSelector);
		
		this.newEvalSubmit = new Button(page, id + "_newEvalSubmit", "Select input") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText("Select input ("+provider.inputDataTypes().size()+")", req);
			}
		};
		this.sourceSelectorPopup = new SourceSelectorPopup(page, id +"_sourceSelectorPopup", evalMan, provider, 
				dataProviders, resultTypeSelector, alert);
		this.ongoingHeader = new Header(page, id + "_ongoingHeader", true);
		ongoingHeader.setDefaultHeaderType(4);
		ongoingHeader.setDefaultText("Ongoing evaluations");
		
		this.finishedHeader = new Header(page, id + "_finishedHeader", true);
		finishedHeader.setDefaultHeaderType(4);
		finishedHeader.setDefaultText("Finished evaluations");
		
		this.onlineOngoingEvalTable = new EvaluationsTable(page, id + "_onlOngTab", true, provider);
		this.onlineFinishedTable = new EvaluationsTable(page, id + "_onlFinTab", false, provider);
//		this.offlineFinishedTable = new EvaluationsTable<>(page, id + "_offFinTab", false, false, provider);
//		this.offlineOngoingTable = new EvaluationsTable<>(page, id + "_offOngTab", false, true, provider);
		onlineOngoingEvalTable.setRowTemplate((RowTemplate) new EvalTemplate(true, provider, page, alert, resultsPage));
		onlineFinishedTable.setRowTemplate((RowTemplate) new EvalTemplate(false, provider, page, alert, resultsPage));
//		offlineOngoingTable.setRowTemplate((RowTemplate) new EvalTemplate(false, true, provider, page, alert));
//		offlineFinishedTable.setRowTemplate((RowTemplate) new EvalTemplate(false, false, provider, page, alert));
		
		sourceSelectorPopup.submitButton.triggerAction(onlineOngoingEvalTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		sourceSelectorPopup.submitButton.triggerAction(onlineFinishedTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		
		appendWidgets();
		setDependencies();
	}
	
	private final void appendWidgets() {
		this.append(new StaticTable(2, 3, new int[]{2,2,8})
				.setContent(0, 0, "Provider").setContent(0, 1, label)
				.setContent(1, 0, "Description").setContent(1, 1, description),
		null).linebreak(null);
		this.append(new StaticTable(1,4, new int[] {2,2,2,6})
				.setContent(0, 0, "Start new evaluation").setContent(0, 1, resultTypeSelectorExt).setContent(0, 2, newEvalSubmit), null).linebreak(null);
		this.append(ongoingHeader, null).linebreak(null)
			.append(onlineOngoingEvalTable, null)
			.append(finishedHeader, null).linebreak(null)
			.append(onlineFinishedTable, null);
		
		this.append(sourceSelectorPopup, null);
	}
	
	private final void setDependencies() {
		newEvalSubmit.triggerAction(sourceSelectorPopup.configSnippet, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		newEvalSubmit.triggerAction(sourceSelectorPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		// reset pages visibility of sourceSelectorPopup
		sourceSelectorPopup.triggerInitialState(newEvalSubmit);
	}
	
	private static class EvalTemplate extends RowTemplate<EvaluationInstance> {
		
		private final boolean isOngoing;
		private final EvaluationProvider provider;
		private final WidgetPage<?> page;
		private final Map<String,Object> header;
		private final Alert alert;
		private final WidgetPage<?> resultsPage;
		
		public EvalTemplate(boolean isOngoing, EvaluationProvider provider, WidgetPage<?> page, Alert alert, WidgetPage<?> resultsPage) {
			this.isOngoing = isOngoing;
			this.provider = provider;
			this.page = page;
			this.alert = alert;
			this.resultsPage = resultsPage;
			final Map<String,Object> mapLocal = new LinkedHashMap<>();
			mapLocal.put("label", "Instance");
			mapLocal.put("online", "Online evaluation");
			mapLocal.put("resultTypes", "Types");
			mapLocal.put("input", "Time series");
			mapLocal.put("started", "Started");
			mapLocal.put("result", isOngoing ? "Intermediate results" : "Results");
			if (isOngoing)
				mapLocal.put("finish", "Finish calculation"); // only for online evals
			this.header = Collections.unmodifiableMap(mapLocal);
		}

		@Override
		public Row addRow(final EvaluationInstance object, final OgemaHttpRequest req) {
			final Row row = new Row();
			final String id = ResourceUtils.getValidResourceName(provider.id() + "__" + getLineId(object));
			final Label lab =new Label(page, id + "_label", object.id(), true);
			final boolean isOnline = object.isOnlineEvaluation();
			final Label online = new Label(page, id + "_online", isOnline + "", true);
			final Label resultTypes = new ItemsListLabel(page, id + "_resultTypes", object.getResultTypes());
			final ItemsListLabel input = new ItemsListLabel(page, id + "_input", object.getInputData());
			if (!isOngoing) {
				final Map<String,StringBuilder> tooltips = new HashMap<>();
				Map<ResultType, EvaluationResult> results = null;
				try {
					results = object.getResults();
				} catch (Exception e) {}
				
				if (results != null) for (Map.Entry<ResultType, EvaluationResult> entry : object.getResults().entrySet()) {
					ResultType type = entry.getKey();
					if (type.resultStructure() != ResultStructure.PER_INPUT || type.valueType() != ValueType.NUMERIC)
						continue;
					for (SingleEvaluationResult sr : entry.getValue().getResults()) {
						try {
							final Object value;
							if (sr instanceof SingleValueResult)
								value = ((SingleValueResult<?>) sr).getValue();
							else if (sr instanceof ArrayResult) {
								final Iterator<String> labels = ((ArrayResult) sr).getLabels(req.getLocale()).iterator();
								value = "[" + ((ArrayResult) sr).getValues().stream()
									.map(res -> labels.next() + ": " + ((SingleValueResult) res).getValue())
									.collect(Collectors.joining(", "))
									+ "]";
							} else 
								value =null;
							if (value != null) {
								 final String target = sr.getInputData().get(0).label(OgemaLocale.ENGLISH);
								 StringBuilder sb = tooltips.get(target);
								 if (sb == null) {
									 sb = new StringBuilder();
									 tooltips.put(target, sb);
								 } else
									 sb.append("&#013;&#010;"); // linebreak in tooltips: CRL+LF
								 sb.append(type.label(req.getLocale())).append(": ").append(value);
									 
							}
						} catch (Exception e) {
							System.out.println("   HI!");
							e.printStackTrace();
							TimeSeriesEvaluationApp.logger.error("Provider returned invalid result {}",sr,e);
						}
					}
				}
				if (!tooltips.isEmpty()) {
					final Map<String,String> tt = new HashMap<>();
					for (Map.Entry<String, StringBuilder> entry : tooltips.entrySet()) {
						StringBuilder sb = entry.getValue();
						tt.put(entry.getKey(), sb.toString());
					}
					
					input.setTooltips(tt, req);
				}
			}
			final String started = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(object.getStartTime()));
			final Label startTime = new Label(page, id + "_startTime", started, true);
			row.addCell("label", lab, 1);
			row.addCell("online", online, 1);
			row.addCell("resultTypes", resultTypes, 2);
			row.addCell("input", input, 3);
			row.addCell("started", startTime, 1);
			final StringBuilder url = new StringBuilder(resultsPage.getFullUrl()).append("?provider=");
			try {
				url.append(URLEncoder.encode(provider.id(),"UTF-8")).append("&instance=")
					.append(URLEncoder.encode(object.id(),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			final RedirectButton showIntermediateResults = new RedirectButton(page, id + "_results",
					isOngoing ? "Show intermediate results" : "Show results", url.toString(), true);
			row.addCell("result", showIntermediateResults,1);
			if (isOnline && isOngoing) {
				final RedirectButton finish = new RedirectButton(page, id + "_finish", "Finish", "#", true) {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void onPOSTComplete(String data, OgemaHttpRequest req) {
						Status status = ((OnlineEvaluation) object).finish();
						alert.showAlert("Online calculation finished with status " + status.getStatus(), status.getStatus() == EvaluationStatus.FINISHED, req);
					}
					
				};
				row.addCell("finish", finish,1);
				finish.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			}
			return row;
		}

		@Override
		public String getLineId(EvaluationInstance object) {
			return ResourceUtils.getValidResourceName(object.id());
		}

		@Override
		public Map<String, Object> getHeader() {
			return header;
		}
		
	}
	
	private static class EvaluationsTable extends DynamicTable<EvaluationInstance> {

		private static final long serialVersionUID = 1L;
		private final boolean ongoing;
		private final EvaluationProvider provider;
		
		public EvaluationsTable(WidgetPage<?> page, String id, boolean ongoing, EvaluationProvider provider) {
			super(page, id, true);
			this.ongoing = ongoing;
			this.provider = provider;
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final List<EvaluationInstance> objects = new ArrayList<>();
			writeLock(req);
			try {
				objects.addAll(provider.getOnlineEvaluations(ongoing, !ongoing));
				objects.addAll(provider.getOfflineEvaluations(ongoing, !ongoing));
				setWidgetVisibility(!objects.isEmpty(),req); 
				updateRows(objects,req);
				setPollingInterval(provider.hasOngoingEvaluations() ? 5000 : -1, req);
			} finally {
				writeUnlock(req);
			}
		}
		
	}
	
}
