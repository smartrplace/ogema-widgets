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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.configuration.StartEndConfiguration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.DateConfiguration;
import de.iwes.timeseries.eval.viz.gui.LabelledItemUtils.ProviderLabel;
import de.iwes.timeseries.eval.viz.gui.SourceSelectorPopup.InputDataSnippet;
import de.iwes.widgets.api.extended.OgemaWidgetBase;
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
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;
import de.iwes.widgets.multipage.MultiPage;
import de.iwes.widgets.shadowwidgets.ShadowMultiselect;

public class ConfigurationPopupSnippet extends PageSnippet {

	private static final long serialVersionUID = 1L;
	private final Header inputHeader;
	private final StaticTable inputTable;
//	private final List<ShadowMultiselect<SelectionItem>> terminalSelectorCopies = new ArrayList<>();
	private final MultiPage<RequiredInputData> multiPage;
	// one instance per RequiredInputData / popup page
	private final List<ShadowContainerSnippet> selectContainers;
	final List<InputDataConfigPopup> inputDataConfigPopups;
	private final Alert alert;
	private final Header configHeader;
	final ConfigTable configTable;
	
	public ConfigurationPopupSnippet(WidgetPage<?> page, String id, 
			final EvaluationProvider provider, final MultiPage<RequiredInputData> multiPage) {
		super(page, id, true);
		this.multiPage = multiPage;
		final List<RequiredInputData> inputTypes = provider.inputDataTypes();
		inputHeader = new Header(page, id + "_inputHeader", true);
		inputHeader.setDefaultText("Input data");
		inputHeader.setDefaultHeaderType(3);
		
		final List<PageSnippetI> subpages = multiPage.getSubPages();
		inputTable = new StaticTable(subpages.size(), 3, new int[]{2,8,2});
		this.alert = new Alert(page, id + "_alert", "") {

			private static final long serialVersionUID = 1L;
			private final String getMessage(List<String> groups) {
				return "At least one selected schedule in input " + 
					getGroupsText(groups)
					+ " has interpolation mode NONE. This will possibly lead to errors. Select <i>Show configuration</i> to "
					+ "adapt the mode for the evaluation.";
			}

			private final String getGroupsText(List<String> groups) {
				if (groups.size() == 1)
					return "group '" + groups.get(0) + "'";
				final StringBuilder sb= new StringBuilder();
				sb.append("groups '");
				boolean first = true;
				for (String group : groups) {
					if (!first)
						sb.append("', '");
					sb.append(group);
					first = false;
				}
				sb.append('\'');
				return sb.toString();
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				List<String> nones = null;
				for (InputDataConfigPopup configPop : inputDataConfigPopups) {
					if (configPop.modesDropdown.hasNoneMode(req)) {
						if (nones == null)
							nones = new ArrayList<>();
						nones.add(configPop.getInputSnippet().input.label(req.getLocale()));
					}
				}
				if (nones != null) {
					setText(getMessage(nones), req);
					setStyle(AlertData.BOOTSTRAP_DANGER, req);
					setWidgetVisibility(true, req);
				}
				else 
					setWidgetVisibility(false, req);
			}
			
		};
		alert.setDefaultVisibility(false);
		this.selectContainers = new ArrayList<>();
		this.inputDataConfigPopups = new ArrayList<>();
		final Iterator<RequiredInputData> it = inputTypes.iterator();
		int row = 0;
		for (PageSnippetI snippet : subpages) {
			final RequiredInputData dataType = it.next();
			final ShadowContainerSnippet container = new ShadowContainerSnippet(page, id + "_container_"+ row, false);
			final InputDataConfigPopup configPopup = new InputDataConfigPopup(page, id + "_inputConfigPopup_" + row, "", (InputDataSnippet) snippet);
			final Button configPopupTrigger=  new Button(page, id + "_configPopupTrigger_" + row, "Show configurations", true);
//			configPopupTrigger.triggerAction(configPopup.modesDropdown, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST); // must be triggered when this is triggered
			configPopupTrigger.triggerAction(configPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
			configPopup.modesDropdown.triggerAction(alert, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
			configPopup.modesDropdown.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			inputDataConfigPopups.add(configPopup);
			// TODO append configPopup
			inputTable.setContent(row, 0, dataType.label(OgemaLocale.ENGLISH))
				.setContent(row, 1, container).setContent(row++, 2, configPopupTrigger);
			
			selectContainers.add(container);
		}
		this.configHeader = new Header(page, id + "_configHeader", true);
		configHeader.setDefaultText("Configurations");
		configHeader.setDefaultHeaderType(3);
		this.configTable = new ConfigTable(page, id + "_configTable", provider, this);
		final ConfigTemplate template = new ConfigTemplate(this);
		configTable.setRowTemplate(template);
		
		setDefaultVisibility(false);
		appendWidgets();
	}

	@Override
	public void onGET(OgemaHttpRequest req) {
		if (!isVisible(req)) {
			return;
		}
		final List<PageSnippetI> subpages = multiPage.getSubPages();
//		final Iterator<RequiredInputData> it = inputTypes.iterator();
		final Iterator<ShadowContainerSnippet> it = selectContainers.iterator();
		for (PageSnippetI snippet : subpages) {
			InputDataSnippet inputSnippet  =(InputDataSnippet) snippet;
			@SuppressWarnings("unchecked")
			final TemplateMultiselect<SelectionItem> selector = (TemplateMultiselect<SelectionItem>) inputSnippet.tree.getTerminalSelectWidget(req);
			final ShadowContainerSnippet container = it.next();
			if (selector == null) {
				container.clear(req);
				return;
			}
			final InputDataSnippet shadowedSnippet = container.getShadowSnippet(req);
			@SuppressWarnings("unchecked")
			final TemplateMultiselect<SelectionItem> oldSelect = 
					(TemplateMultiselect<SelectionItem>) (shadowedSnippet != null ? shadowedSnippet.tree.getTerminalSelectWidget(req) : null);
			if (selector != oldSelect) {
				container.clear(req);
				final ShadowMultiselect<SelectionItem> copy = new ShadowMultiselect<>(this, selector.getId()+"_copy", req, selector);
				container.append(copy, req);
				container.setShadowSnippet(inputSnippet, req);
//				container.setSelector(copy, req);
			}
		}
	}
	
	void triggerUpdates(OgemaWidget governor) {
		Iterator<InputDataConfigPopup> configIt = inputDataConfigPopups.iterator();
		for (PageSnippet snippet : selectContainers) {
			governor.triggerAction(snippet, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
			if (configIt.hasNext()) // should always be true
				governor.triggerAction(configIt.next().modesDropdown, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
		}
	}
	
	// TODO initialize start and end configurations
	public List<ConfigurationInstance> getSelectedConfigurations(OgemaHttpRequest req) {
		final List<ConfigurationInstance> configs = new ArrayList<>();
		for (String row : configTable.getRows(req)) {
			if (!row.equals(DynamicTable.HEADER_ROW_ID)) {
				final Object o = configTable.getCellContent(row, "config", req);
				if (!(o instanceof ConfigurationWidget))
					continue;
				final ConfigurationInstance inst = ((ConfigurationWidget<?>) o).getSelectedInstance(req);
				if (inst != null) {
					configs.add(inst);
				}
			}
		}
		return configs;
	}
	
	public List<ReadOnlyTimeSeries> getSelectedTimeseries(OgemaHttpRequest req) {
		final List<ReadOnlyTimeSeries> list = new ArrayList<>();
		for (ShadowContainerSnippet shadow : selectContainers) {
			final InputDataSnippet snippet = shadow.getShadowSnippet(req);
			if (snippet == null)
				continue;
			@SuppressWarnings("unchecked")
			final TemplateMultiselect<SelectionItem> select = (TemplateMultiselect<SelectionItem>) snippet.tree.getTerminalSelectWidget(req);
			if (select == null)
				continue;
			@SuppressWarnings("unchecked")
			final TerminalOption<ReadOnlyTimeSeries> terminalOpt = (TerminalOption<ReadOnlyTimeSeries>) snippet.tree.getTerminalOption(req);
			if (terminalOpt == null)
				continue;
			for (SelectionItem item : select.getSelectedItems(req)) {
				final ReadOnlyTimeSeries timeSeries = terminalOpt.getElement(item);
				if (timeSeries != null)
					list.add(timeSeries);
			}
		}
		return list;
		
	}
	
	private static class ConfigTemplate extends RowTemplate<Configuration<?>> {
		
		private final static Map<String,Object> header;
		private final ConfigurationPopupSnippet snippet;
		private final ConfigTable table;
		
		static {
			final Map<String,Object> headerMap = new LinkedHashMap<>();
			headerMap.put("label", "Configuration");
			headerMap.put("descr", "Description");
			headerMap.put("config", "Value");
			header = Collections.unmodifiableMap(headerMap);
		}
		
		public ConfigTemplate(ConfigurationPopupSnippet snippet) {
			this.snippet = snippet;
			this.table = snippet.configTable;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Row addRow(final Configuration<?> object, final OgemaHttpRequest req) {
			final Row row = new Row();
			final String prefix = table.getId() + getLineId(object);
			final ProviderLabel label = new ProviderLabel(table, prefix + "_labelC", req, object, true);
			final ProviderLabel description = new ProviderLabel(table, prefix + "_descrC", req, object, false);
			final ConfigurationWidget<?> configWidget;
			if (object == StartEndConfiguration.START_CONFIGURATION) {
				configWidget = new StartEndConfigurationWidget(snippet.configTable, prefix + "_configC", req, 
						(Configuration<DateConfiguration>) object, snippet, true);
			} else if (object == StartEndConfiguration.END_CONFIGURATION) {
				configWidget = new StartEndConfigurationWidget(snippet.configTable, prefix + "_configC", req, 
						(Configuration<DateConfiguration>) object, snippet, false);
			}
			else 
				configWidget = new ConfigurationWidget(table, prefix + "_configC", req, object); 
			row.addCell("label", label, 1);
			row.addCell("descr", description, 2);
			row.addCell("config", configWidget);
			
			return row;
		}

		// we could use the id of the configuration, but then there is (an even higher) risk 
		// of collisions, between configs of different providers
		@Override
		public String getLineId(Configuration<?> object) {
			return "_" + Objects.hashCode(object);
		}

		@Override
		public Map<String, Object> getHeader() {
			return header;
		}
		
	}
	
	private static class ConfigTable extends DynamicTable<Configuration<?>> {
		
		private static final long serialVersionUID = 1L;
		private final EvaluationProvider provider;
		private final OgemaWidgetBase<?> popup;

		public ConfigTable(WidgetPage<?> page, String id, EvaluationProvider provider, OgemaWidgetBase<?> popup) {
			super(page, id);
			this.provider = provider;
			this.popup = popup;
		}
		
		// TODO filter for applicable result types
		@Override
		public void onGET(OgemaHttpRequest req) {
			if (!popup.isVisible(req))
				return;
			final List<Configuration<?>> configs = provider.getConfigurations();
			boolean containsStart = false;
			boolean containsEnd = false;
			if (configs != null) {
	 			for (Configuration<?> c : configs) {
					if (c == StartEndConfiguration.START_CONFIGURATION)
						containsStart = true;
					else if (c == StartEndConfiguration.END_CONFIGURATION)
						containsEnd = true;
				}
			}
			final List<Configuration<?>> newList = new ArrayList<>();
			if (!containsStart)
				newList.add(StartEndConfiguration.START_CONFIGURATION);
			if (!containsEnd)
				newList.add(StartEndConfiguration.END_CONFIGURATION);
			if (configs != null)
				newList.addAll(configs);
			updateRows(newList, req);
		}
		
	}
	
	private final void appendWidgets() {
		append(inputHeader, null).linebreak(null).append(inputTable, null).linebreak(null)
			.append(alert, null)
			.append(configHeader, null).linebreak(null).append(configTable, null).linebreak(null);
		for (InputDataConfigPopup popup : inputDataConfigPopups) {
			append(popup, null).linebreak(null);
		}
		linebreak(null);
	}

}
