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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationManager;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.LabelledItemProvider;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.LabelledItemSelectorSingle;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.MutableProviderLabel;
import de.iwes.timeseries.eval.viz.TimeSeriesEvaluationApp;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.DataTree;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.SelectionTree;
import de.iwes.widgets.html.selectiontree.TerminalOption;
import de.iwes.widgets.multipage.MultiPage;
import de.iwes.widgets.template.PageSnippetTemplate;

class SourceSelectorPopup extends Popup {

	private static final long serialVersionUID = 1L;
	private final MultiPage<RequiredInputData> bodySnippet;
	final ConfigurationPopupSnippet configSnippet;
	final Button submitButton;
	private final List<RequiredInputData> inputDataTypes;
	
	SourceSelectorPopup(final WidgetPage<?> page, final String id, final EvaluationManager evalMan, final EvaluationProvider provider, 
			final LabelledItemProvider<DataProvider<?>> dataProviders, final TemplateMultiselect<ResultType> resultTypeSelector, final Alert alert) {
		super(page, id, true);
		setDefaultWidth("80%");
		final PageSnippetTemplate<RequiredInputData> template = new PageSnippetTemplate<RequiredInputData>() {

			@Override
			public String getId(RequiredInputData object) {
				return object.id();
			}

			@Override
			public String getLabel(RequiredInputData object, OgemaLocale locale) {
				return object.label(locale);
			}

			@Override
			public PageSnippetI getSnippet(RequiredInputData item, OgemaHttpRequest req) {
				return new InputDataSnippet(page, ResourceUtils.getValidResourceName(id + item.id()), item,
						dataProviders, getInputTypeIndex(item));
			}
		};
		this.inputDataTypes = provider.inputDataTypes();
		this.bodySnippet = new MultiPage<>(page, id + "_bodySnippet", template, inputDataTypes);
		this.submitButton = new Button(page, id + "_submitBtn", "Start evaluation") {
			
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final List<ResultType> types = resultTypeSelector.getSelectedItems(req);
				if (types.isEmpty()) {
					alert.showAlert("No result types selected", false, req);
					return;
				}
				final List<EvaluationInput> allItems = new ArrayList<>();
				final Iterator<InputDataConfigPopup> configPopupsIt = configSnippet.inputDataConfigPopups.iterator();
				for (PageSnippetI snippetI : bodySnippet.getSubPages()) {
					InputDataSnippet snippet = (InputDataSnippet) snippetI;
					snippet.setWidgetVisibility(false, req);
					final DataProvider<?> provider = snippet.getSelectedProvider(req);
					if (provider == null) {
						allItems.add(new EvaluationInputImpl(Collections.<TimeSeriesData> emptyList()));
						continue;
					}
					final OgemaWidget widget = snippet.tree.getTerminalSelectWidget(req);
					if (widget == null) { // TODO
						throw new UnsupportedOperationException("not implemented yet");
					}
					final List<SelectionItem> items;
					if (widget instanceof TemplateMultiselect) {
						 items = ((TemplateMultiselect<SelectionItem>) widget).getSelectedItems(req);
					} else { // TemplateDropdown
						 items = Collections.singletonList(((TemplateDropdown<SelectionItem>) widget).getSelectedItem(req));
					}
					final List<TimeSeriesData> dataList = new ArrayList<>();
					final TerminalOption<? extends ReadOnlyTimeSeries> converter = provider.getTerminalOption();
					final InterpolationMode im;
					if (!configPopupsIt.hasNext()) {
						TimeSeriesEvaluationApp.logger.warn("Eval viz setting corrupt... missing config popup");
						im = null;
					} else {
						im = configPopupsIt.next().modesDropdown.getSelectedItem(req);
					}
					for (SelectionItem item : items) {
						TimeSeriesData timeSeriesData = new TimeSeriesDataImpl(converter.getElement(item), 
								item.label(req.getLocale()), item.label(req.getLocale()), im); // TODO offset, factor, etc
						dataList.add(timeSeriesData);
					}
					final EvaluationInput input = new EvaluationInputImpl(dataList);
					allItems.add(input);
				}
				final List<ConfigurationInstance> configs = configSnippet.getSelectedConfigurations(req);
//				final EvaluationInstance instance = provider.newEvaluation(allItems, types, configs);
				final EvaluationInstance instance = evalMan.newEvaluation(provider, allItems, types, configs);
				try {
					Thread.sleep(300); // ensure short running evaluations will be shown as finished immediately
				} catch (InterruptedException ignore) {}
				if (instance == null) 
					alert.showAlert("Unknown error occurred", false, req);
				else 
					alert.showAlert("New " + (instance.isOnlineEvaluation() ? "online" : "offline") + " evaluation started. Id: " + instance.id(), true, req);
			}
			
		};
		
		configSnippet = new ConfigurationPopupSnippet(page, id + "_configSnippet", provider , bodySnippet);
		bodySnippet.append(configSnippet, null);
		
		final PageSnippetI terminalSnippet = bodySnippet.getTerminalSnippet();
		final Button forth = new Button(page, id + "_terminalForth","Next",true) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				configSnippet.setWidgetVisibility(true, req);
			}
			
		};
		forth.triggerAction(terminalSnippet, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		forth.triggerAction(configSnippet, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		forth.triggerAction(configSnippet.configTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
		configSnippet.triggerUpdates(forth);
		
		final Button back = new Button(page, id + "_terminalBack", "Back", true) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				configSnippet.setWidgetVisibility(false, req);
			}
			
		};
		back.triggerAction(terminalSnippet, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		back.triggerAction(configSnippet, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		terminalSnippet.append(forth, null);
		configSnippet.append(back, null);
		configSnippet.append(submitButton, null);		
		
//		terminalSnippet.append(submitButton, null); // TODO layout
		this.setBody(bodySnippet, null);
		this.setTitle("Select data sources", null);
		
		// set dependencies
		submitButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		submitButton.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		submitButton.triggerAction(configSnippet, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
	}
	
	void triggerInitialState(OgemaWidget governor) {
		bodySnippet.triggerInitialState(governor);
	}
	
	static class InputDataSnippet extends PageSnippet {

		private static final long serialVersionUID = 1L;
		final RequiredInputData input;
		private final LabelledItemSelectorSingle<DataProvider<?>> dataProviderSelector;
		private final MutableProviderLabel<DataProvider<?>> dataProviderLabel;
		private final MutableProviderLabel<DataProvider<?>> dataProviderDescription;
		private final Label inpuLabel;
		final SelectionTree tree;
		private final Label selectDataProviderLabel;
		
		public InputDataSnippet(WidgetPage<?> page, String id, final RequiredInputData input,
				final LabelledItemProvider<DataProvider<?>> dataProviders, int inputTypeIndex) {
			super(page, id, true);
			this.input = input;
			setDefaultVisibility(false);
			this.selectDataProviderLabel = new Label(page, id+"_dplabel", "Select data provider ("+
					inputTypeIndex+" / "+dataProviders.getItems().size());
			this.dataProviderSelector = new LabelledItemSelectorSingle<>(page, id + "_dataPrvoiderSelector", dataProviders);
			this.dataProviderLabel = new MutableProviderLabel<>(page, id + "_dataProviderLabel", dataProviderSelector, true);
			this.dataProviderDescription = new MutableProviderLabel<>(page, id + "_dataProviderDescription", dataProviderSelector, false);
			this.inpuLabel = new Label(page, id + "_inputDescription") {
				private static final long serialVersionUID = 1L;
				@Override
				public void onGET(OgemaHttpRequest req) {
					setText(input.description(null), req);
				}
			};
			this.tree = new DataTree(page, id + "_dataTree", dataProviderSelector);
			appendWidgets();
			setDependencies();
		}
		
		private final void appendWidgets() {
			this.append(new StaticTable(4, 2)
					.setContent(0, 0, selectDataProviderLabel).setContent(0, 1, dataProviderSelector)
					.setContent(1, 0, "Argument Description").setContent(1, 1, inpuLabel)
					.setContent(2, 0, "Provider").setContent(2, 1, dataProviderLabel)
					.setContent(3, 0, "Provider Description").setContent(3, 1, dataProviderDescription),
				null).linebreak(null);
			this.append("Select data sources:", null).linebreak(null).append(tree, null);
			
		}
		
		private final void setDependencies() {
			dataProviderSelector.triggerAction(dataProviderLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			dataProviderSelector.triggerAction(dataProviderDescription, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			dataProviderSelector.triggerAction(tree, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
		
		public DataProvider<?> getSelectedProvider(OgemaHttpRequest req) {
			return (DataProvider<?>) dataProviderSelector.getSelectedItem(req);
		}
		
		public List<ReadOnlyTimeSeries> getSelectedSchedules(OgemaHttpRequest req) {
			@SuppressWarnings("unchecked")
			final TemplateMultiselect<SelectionItem> selector = (TemplateMultiselect<SelectionItem>) tree.getTerminalSelectWidget(req);
			if (selector == null)
				return Collections.emptyList();
			@SuppressWarnings("unchecked")
			final TerminalOption<ReadOnlyTimeSeries> terminalOpt = (TerminalOption<ReadOnlyTimeSeries>) tree.getTerminalOption(req);
			if (terminalOpt == null)
				return Collections.emptyList();
			final List<ReadOnlyTimeSeries> list = new ArrayList<>();
			for (SelectionItem item : selector.getSelectedItems(req)) {
				list.add(terminalOpt.getElement(item));
			}
			return list;
		}
		
		public RequiredInputData getInputData() {
			return input;
		}
		
	}
	
	private int getInputTypeIndex(RequiredInputData input) {
		int i = 0;
		for (RequiredInputData t: inputDataTypes) {
			if (t.equals(input)) 
				return i;
			i++;
		}
		return -1;
	}

}
