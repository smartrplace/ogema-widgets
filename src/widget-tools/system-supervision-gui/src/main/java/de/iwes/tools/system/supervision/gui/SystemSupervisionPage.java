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
package de.iwes.tools.system.supervision.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ValueResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.tools.resource.util.LoggingUtils;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.tools.resource.util.ValueResourceUtils;

import de.iwes.tools.system.supervision.gui.model.SupervisionMessageSettings;
import de.iwes.tools.system.supervision.model.SupervisionResult;
import de.iwes.tools.system.supervision.model.SupervisionUtils;
import de.iwes.tools.system.supervision.model.SystemSupervisionConfig;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.accordion.Accordion;
import de.iwes.widgets.html.accordion.AccordionData;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.checkbox.Checkbox;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.form.textfield.ValueInputField;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.flexbox.AlignContent;
import de.iwes.widgets.html.html5.flexbox.AlignItems;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;
import de.iwes.widgets.html.plot.api.PlotType;
import de.iwes.widgets.resource.widget.dropdown.ResourceDropdown;
import de.iwes.widgets.reswidget.scheduleviewer.ScheduleViewerBasic;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationBuilder;
import de.iwes.widgets.template.DisplayTemplate;

public class SystemSupervisionPage {
	
	private final static Map<String,String> quantitiesMap;
	
	static {
		quantitiesMap = new HashMap<>();
		quantitiesMap.put("dataFolderSize", "Data folder size");
		quantitiesMap.put("rundirFolderSize", "Rundir folder size");
		quantitiesMap.put("freeDiskSpace", "Free disk space");
		quantitiesMap.put("usedMemorySize", "Used RAM");
		quantitiesMap.put("maxAvailableMemorySize", "Max RAM available");
		quantitiesMap.put("nrResources", "Number of OGEMA resources");
	}
	private final static String MESSAGE_CONFIG_NAME = "messaging";
	
	private final WidgetPage<?> page;
	private final Header header;
	private final Alert alert;
	private final Header resultsHeader;
//	private final Header configHeader;
//	private final Header plotsHeader;
	private final Header messageConfigHeader;
	private final Alert messageConfigInfobox;
	// hidden if at least one config resource exists
	private final Flexbox newConfigArea;
	private final TextField newConfigPath;
	private final Button createConfigResource;
	private final ResourceDropdown<SystemSupervisionConfig> configSelector;
	private final Button updateBtn;
	private final Label dataFolderSize;
	private final Label rundirFolderSize;
	private final Label freeDiskSpace;
	private final Label ramUsage;
	private final Label maxRamAvailable;
	private final Label resourceUsage;
	private final LoggingCheckbox rundirFoldeLogging;
	private final LoggingCheckbox dataFolderLogging;
	private final LoggingCheckbox freeDiskSpaceLogging;
	private final LoggingCheckbox ramUsageLogging;
	private final LoggingCheckbox maxRamLogging;
	private final LoggingCheckbox resourceUsageLogging;
	
	private final ValueInputField<Long> diskCheckItv;
	private final ValueInputField<Long> ramCheckItv;
	private final ValueInputField<Long> resourcesCheckItv;
	private final Button triggerDiskCalc;
	private final Button triggerRamCalc;
	private final Button triggerResCalc;
	
	private final ValueInputField<Long> diskWarnThresholdLow;
	private final ValueInputField<Long> diskWarnThresholdMedium;
	private final ValueInputField<Long> diskWarnThresholdHigh;
	private final ValueInputField<Long> ramWarnThresholdLow;
	private final ValueInputField<Long> ramWarnThresholdMedium;
	private final ValueInputField<Long> ramWarnThresholdHigh;
	private final ValueInputField<Long> resWarnThresholdLow;
	private final ValueInputField<Long> resWarnThresholdMedium;
	private final ValueInputField<Long> resWarnThresholdHigh;
	
	private final ScheduleViewerBasic<RecordedData> plots;
	
	public SystemSupervisionPage(final WidgetPage<?> page, final ApplicationManager am) {
		this.page = page;
		this.header =new Header(page, "header", true);
		header.setDefaultColor("blue");
		header.setDefaultText("System supervision");
		header.addDefaultStyle(HeaderData.CENTERED);
		
		this.alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);

		this.newConfigPath = new TextField(page, "newConfigPath");
		newConfigPath.setDefaultPlaceholder("Enter new configuration resource path");
		this.createConfigResource = new Button(page, "createConfigResource","Create",true) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final String path = newConfigPath.getValue(req).trim();
				if (path.length() < 5) {
					alert.showAlert("Path must contain at least 5 characters", false, req);
					return;
				}
				if (!ResourceUtils.getValidResourceName(path).equals(path)) { // 
					final String replaced = path.replace('/', '_');
					if (!ResourceUtils.getValidResourceName(replaced).equals(replaced)) {
						alert.showAlert("Path contains invalid characters: " + path, false, req);
						return;
					}
				}
				final SystemSupervisionConfig config;
				try {
					config = am.getResourceManagement().createResource(path, SystemSupervisionConfig.class);
				} catch (Exception e) {
					alert.showAlert("Could not create configuration resource: " + e, false, req);
					return;
				}
				config.activate(false); // triggers callback in system supervision
				alert.showAlert("New config resource created: " + config, true, req);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					return;
				}
			}
			
		};
		createConfigResource.setWaitForPendingRequest(true);
		this.newConfigArea = new Flexbox(page, "newConfigArea" ,true) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final boolean empty = am.getResourceAccess().getResources(SystemSupervisionConfig.class).isEmpty();
				setWidgetVisibility(empty, req);
			}
			
		};
		newConfigArea.addItem(newConfigPath, null);
		newConfigArea.addItem(createConfigResource, null);
		
		newConfigArea.setDefaultAlignItems(AlignItems.CENTER);
		newConfigArea.setDefaultJustifyContent(JustifyContent.CENTER);
		newConfigArea.setDefaultAlignContent(AlignContent.CENTER);
		
		this.configSelector = new ResourceDropdown<>(page, "configSelector", false, SystemSupervisionConfig.class, UpdateMode.AUTO_ON_GET, am.getResourceAccess());
		this.updateBtn = new Button(page, "updateBtn", "Update view") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (configSelector.getSelectedItem(req)!=null)
					enable(req);
				else
					disable(req);
			}
			
		};
		
		this.dataFolderSize = new MbLabel(page, "dataFolderSize", "dataFolderSize");
		this.rundirFolderSize = new MbLabel(page, "rundirFolderSize", "rundirFolderSize");
		this.freeDiskSpace = new MbLabel(page, "freeDiskSpace", "freeDiskSpace");
		this.ramUsage = new MbLabel(page, "ramUsage", "usedMemorySize");
		this.maxRamAvailable = new MbLabel(page, "maxAvailableMemorySize", "maxAvailableMemorySize");
		this.resourceUsage = new Label(page, "nrResources") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final SystemSupervisionConfig config = configSelector.getSelectedItem(req);
				final IntegerResource resource = config == null ? null : config.results().nrResources();
				if (resource == null || !resource.isActive()) {
					setText("", req);
					return;
				}
				setText(String.valueOf(resource.getValue()), req);
			}
			
		};
		this.dataFolderLogging = new LoggingCheckbox(page, "dataFolderSizeLogging", "dataFolderSize");
		this.rundirFoldeLogging = new LoggingCheckbox(page, "rundirFolderSizeLogging", "rundirFolderSize");
		this.freeDiskSpaceLogging = new LoggingCheckbox(page, "freeDiskSpaceLogging", "freeDiskSpace");
		this.ramUsageLogging = new LoggingCheckbox(page, "ramUsageLogging", "usedMemorySize");
		this.maxRamLogging = new LoggingCheckbox(page, "maxAvailableMemorySizeLogging", "maxAvailableMemorySize");
		this.resourceUsageLogging = new LoggingCheckbox(page, "resourceUsageLogging", "nrResources");
		
		this.diskCheckItv = new UpdateField(page, "diskCheckInterval", "diskCheckInterval", SupervisionUtils.DEFAULT_DISK_SUPERVISION_ITV);
		this.ramCheckItv = new UpdateField(page, "memoryCheckInterval", "memoryCheckInterval", SupervisionUtils.DEFAULT_RAM_SUPERVISION_ITV);
		this.resourcesCheckItv = new UpdateField(page, "resourceCheckInterval", "resourceCheckInterval", SupervisionUtils.DEFAULT_RESOURCE_SUPERVISION_ITV);
		
		this.triggerDiskCalc = new TriggerButton(page, "triggerDiskCheck", "triggerDiskCheck");
		this.triggerRamCalc = new TriggerButton(page, "triggerMemoryCheck", "triggerMemoryCheck");
		this.triggerResCalc = new TriggerButton(page, "triggerResourceCheck", "triggerResourceCheck");
		
		this.resultsHeader =new Header(page, "resultsHeader", true);
		resultsHeader.setDefaultColor("blue");
		resultsHeader.setDefaultText("Results");
		resultsHeader.setDefaultHeaderType(3);
		resultsHeader.addDefaultStyle(HeaderData.CENTERED);
		
//		this.configHeader =new Header(page, "configHeader", true);
//		configHeader.setDefaultColor("blue");
//		configHeader.setDefaultText("Settings");
//		configHeader.setDefaultHeaderType(3);
//		configHeader.addDefaultStyle(HeaderData.CENTERED);
//
//		this.plotsHeader =new Header(page, "plotsHeader", true);
//		plotsHeader.setDefaultColor("blue");
//		plotsHeader.setDefaultText("Plots");
//		plotsHeader.setDefaultHeaderType(3);
//		plotsHeader.addDefaultStyle(HeaderData.CENTERED);
		
		this.messageConfigHeader =new Header(page, "messageConfigHeader", true);
		messageConfigHeader.setDefaultColor("blue");
		messageConfigHeader.setDefaultText("Reports configuration");
		messageConfigHeader.setDefaultHeaderType(3);
		messageConfigHeader.addDefaultStyle(HeaderData.CENTERED);
		
		this.messageConfigInfobox = new Alert(page, "messageConfigInfobox", true, "The system supervision can be configured to send OGEMA messages when one of the "
				+ "supervised quantities exceeds a limit. Use the fields below to set those limits; enter 0 to remove a configuration.");
		messageConfigInfobox.addDefaultStyle(AlertData.BOOTSTRAP_SUCCESS);
		messageConfigInfobox.setDefaultVisibility(true);
		
		this.diskWarnThresholdHigh = new WarningThresholdField(page, "diskWarnThreshold3", "freeDiskWarnThreshold", 3, true);
		this.diskWarnThresholdMedium = new WarningThresholdField(page, "diskWarnThreshold2", "freeDiskWarnThreshold", 2, true);
		this.diskWarnThresholdLow = new WarningThresholdField(page, "diskWarnThreshold1", "freeDiskWarnThreshold", 1, true);
		
		this.ramWarnThresholdHigh = new WarningThresholdField(page, "ramWarnThreshold3", "memoryWarnThreshold", 3, true);
		this.ramWarnThresholdMedium = new WarningThresholdField(page, "ramWarnThreshold2", "memoryWarnThreshold", 2, true);
		this.ramWarnThresholdLow = new WarningThresholdField(page, "ramWarnThreshold1", "memoryWarnThreshold", 1, true);

		this.resWarnThresholdHigh = new WarningThresholdField(page, "resWarnThreshold3", "resourcesWarnThreshold", 3, false);
		this.resWarnThresholdMedium = new WarningThresholdField(page, "resWarnThreshold2", "resourcesWarnThreshold", 2, false);
		this.resWarnThresholdLow = new WarningThresholdField(page, "resWarnThreshold1", "resourcesWarnThreshold", 1, false);
		
		final ScheduleViewerConfiguration viewerConfig = ScheduleViewerConfigurationBuilder.newBuilder()
				.setShowIndividualConfigBtn(true)
				.setShowManipulator(false)
				.setShowCsvDownload(true)
				.setShowOptionsSwitch(false)
				.setShowStandardIntervals(true)
				.setUseNameService(false)
				.build();
		final DisplayTemplate<RecordedData> displayTemplate = new DisplayTemplate<RecordedData>() {
			
			@Override
			public String getId(RecordedData object) {
				final Resource r = am.getResourceAccess().getResource(object.getPath());
				return r != null ? r.getName() : object != null ? object.getPath() : null;
			}

			@Override
			public String getLabel(RecordedData object, OgemaLocale locale) {
				final Resource r = am.getResourceAccess().getResource(object.getPath());
				String label = r == null ? null : quantitiesMap.get(r.getName());
				if (label == null)
					am.getLogger().warn("Label not found for recorded data {}", (object == null ? null : object.getPath()) );
				return label != null ? label : object != null ? "unknown: " + object.getPath()  : "unknown";
			}
		};
		this.plots = new ScheduleViewerBasic<RecordedData>(page, "plots", am, viewerConfig, displayTemplate) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				final SystemSupervisionConfig config = configSelector.getSelectedItem(req);
				if (config == null) {
					selectSchedules(Collections.<RecordedData> emptyList(), req);
					return;
				}
				final SupervisionResult results = config.results();
				final List<RecordedData> schedules = new ArrayList<>();
				check(schedules, results.dataFolderSize());
				check(schedules, results.rundirFolderSize());
				check(schedules, results.freeDiskSpace());
				check(schedules, results.usedMemorySize());
				check(schedules, results.maxAvailableMemorySize());
				check(schedules, results.nrResources());
				setSchedules(schedules, req);
				selectSchedules(schedules, req);
			}
			
		};
		plots.getSchedulePlot().getDefaultConfiguration().doScale(false);
		plots.getSchedulePlot().getDefaultConfiguration().setPlotType(PlotType.LINE_WITH_POINTS);
		
		buildPage();
		setDependencies();
	}

	private final void buildPage() {
		page.append(header).linebreak().append(alert).append(newConfigArea);
		int row = 0;
		StaticTable tab = new StaticTable(2, 2, new int[]{2,2})
				.setContent(row, 0, "Select configuration").setContent(row++, 1, configSelector)
															.setContent(row++, 1, updateBtn);
		page.append(tab).linebreak();
		row = 0;
		StaticTable resultsTable = new StaticTable(6, 3, new int[]{2,2,2})
				.setContent(row, 0, "Data folder size").setContent(row, 1, dataFolderSize).setContent(row++, 2, dataFolderLogging)
				.setContent(row, 0, "Rundir folder size").setContent(row, 1, rundirFolderSize).setContent(row++, 2, rundirFoldeLogging)
				.setContent(row, 0, "Free disk space").setContent(row, 1, freeDiskSpace).setContent(row++, 2, freeDiskSpaceLogging)
				.setContent(row, 0, "Used RAM").setContent(row, 1, ramUsage).setContent(row++, 2, ramUsageLogging)
				.setContent(row, 0, "Max. RAM available").setContent(row, 1, maxRamAvailable).setContent(row++, 2, maxRamLogging)
				.setContent(row, 0, "Number of OGEMA resources").setContent(row, 1, resourceUsage).setContent(row++, 2, resourceUsageLogging);
		page.append(resultsHeader).append(resultsTable).linebreak();
		row = 0; 
		StaticTable configTable = new StaticTable(3, 3, new int[]{2,2,2})
				.setContent(row, 0, "Disk usage update interval (min)").setContent(row, 1, diskCheckItv).setContent(row++, 2, triggerDiskCalc)
				.setContent(row, 0, "RAM usage update interval (min)").setContent(row, 1, ramCheckItv).setContent(row++, 2, triggerRamCalc)
				.setContent(row, 0, "Resource nr update interval (min)").setContent(row, 1, resourcesCheckItv).setContent(row++, 2, triggerResCalc);
		row = 0;
		StaticTable messageConfigTable = new StaticTable(9, 2, new int[]{3,3})
				.setContent(row, 0, "Free disk space warn level LOW (MB)").setContent(row++, 1, diskWarnThresholdLow)
				.setContent(row, 0, "Free disk space warn level MEDIUM (MB)").setContent(row++, 1, diskWarnThresholdMedium)
				.setContent(row, 0, "Free disk space warn level HIGH (MB)").setContent(row++, 1, diskWarnThresholdHigh)
				.setContent(row, 0, "Used memory warn level LOW (MB)").setContent(row++, 1, ramWarnThresholdLow)
				.setContent(row, 0, "Used memory warn level MEDIUM (MB)").setContent(row++, 1, ramWarnThresholdMedium)
				.setContent(row, 0, "Used memory warn level HIGH (MB)").setContent(row++, 1, ramWarnThresholdHigh)
				.setContent(row, 0, "Resources warn level LOW (#)").setContent(row++, 1, resWarnThresholdLow)
				.setContent(row, 0, "Resources warn level MEDIUM (#)").setContent(row++, 1, resWarnThresholdMedium)
				.setContent(row, 0, "Resources warn level HIGH (#)").setContent(row++, 1, resWarnThresholdHigh);
//		page.append(configHeader).append(configTable).linebreak();
//		page.append(plotsHeader).append(plots);
		final Accordion acc = new Accordion(page, "accordion", true);
		acc.addDefaultStyle(AccordionData.BOOTSTRAP_LIGHT_BLUE);
		final PageSnippet configSnippet = new PageSnippet(page, "configSnippet", true);
		configSnippet.append(configTable, null).linebreak(null);
		configSnippet.append(messageConfigHeader, null).linebreak(null).append(messageConfigInfobox, null).append(messageConfigTable, null);
		acc.addItem("Settings", configSnippet, null);
		
		final PageSnippet plotSnippet = new PageSnippet(page, "plotSnippet", true);
		plotSnippet.append(plots, null);
		acc.addItem("Plots", plotSnippet, null);
		page.append(acc);
		
	}
	
	private final void setDependencies() {
//		createConfigResource.triggerAction(configSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		createConfigResource.triggerAction(newConfigArea, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		createConfigResource.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		createConfigResource.triggerAction(updateBtn, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
//		createConfigResource.triggerAction(diskCheckItv, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
//		createConfigResource.triggerAction(ramCheckItv, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
//		createConfigResource.triggerAction(resourcesCheckItv, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST,1);
//		configSelector.triggerAction(updateBtn, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		triggerDiskCalc.triggerAction(dataFolderSize, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		triggerDiskCalc.triggerAction(rundirFolderSize, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		triggerDiskCalc.triggerAction(freeDiskSpace, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		triggerRamCalc.triggerAction(ramUsage, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		triggerRamCalc.triggerAction(maxRamAvailable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		triggerResCalc.triggerAction(resourceUsage, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		resetView(configSelector);
		resetView(updateBtn);
		resetView(createConfigResource);
	}
	
	// FIXME better simply reload the "all" widgets group?
	// for update btn and configSelector
	private void resetView(final OgemaWidget trigger) {
//		trigger.triggerAction(dataFolderSize, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(rundirFolderSize, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(freeDiskSpace, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(ramUsage, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(maxRamAvailable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(resourceUsage, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(dataFolderLogging, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(rundirFoldeLogging, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(freeDiskSpaceLogging, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(ramUsageLogging, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(maxRamLogging, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(resourceUsageLogging, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(triggerDiskCalc, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(triggerRamCalc, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(triggerResCalc, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		trigger.triggerAction(plots, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		
//		trigger.triggerAction(diskWarnThresholdHigh, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		// TODO etc
		trigger.triggerAction(page.getAllWidgets(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	private static void check(final List<RecordedData> list, final SingleValueResource res) {
		if (LoggingUtils.isLoggingEnabled(res))
			list.add(LoggingUtils.getHistoricalData(res));
	}
	
	private class WarningThresholdField extends ValueInputField<Long> {
		
		private static final long serialVersionUID = 1L;
		private final String relativePath;
		private final int typeId;
		private final boolean useMb;
		
		/**
		 * 
		 * @param page
		 * @param id
		 * @param type
		 * @param relativePathTruncated
		 * 		relative path of threshold resource w/o Low, Medium, High ending
		 * @param typeId
		 * 		1: level LOW
		 *      2: level MEDIUM
		 *      3: level HIGH
		 */
		public WarningThresholdField(WidgetPage<?> page, String id, String relativePathTruncated, int typeId, boolean useMb) {
			super(page, id, Long.class);
			this.typeId = typeId;
			this.useMb = useMb;
			this.relativePath = relativePathTruncated + (typeId == 1 ? "Low" : typeId == 2 ? "Medium" : typeId == 3 ? "High" : null);
			triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final SupervisionMessageSettings settings = getMessageSettings(req);
			if (settings == null) {
				setNumericalValue(null, req);
				disable(req);
				return;
			}
			enable(req);
			SingleValueResource thresholdRes = settings.getSubResource(relativePath);
			if (thresholdRes == null || !thresholdRes.isActive()) {
				setNumericalValue(null, req);
				return;
			}
			long val = ((Number) ValueResourceUtils.getValue((ValueResource) thresholdRes)).longValue();
			if (val <= 0) {
				setNumericalValue(null, req);
				return;
			}
			if (useMb)
				val = val/mb;
			setNumericalValue(val, req);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			Long val = getNumericalValue(req);
			final SupervisionMessageSettings settings = getMessageSettings(req);
			if (settings == null) { // should return at least a virtual subresource
				return;
			}
			SingleValueResource thresholdRes = settings.getSubResource(relativePath);
			if (thresholdRes == null) {
				return;
			}
			if (val == null || val <= 0) {
				if (thresholdRes.isActive())
					thresholdRes.deactivate(false);
				return;
			}
			thresholdRes.create();
			if (useMb)
				val = val*mb;
			ValueResourceUtils.setValue((ValueResource) thresholdRes, val);
			thresholdRes.activate(false);
			settings.activate(false);
		}
		
	}
		
	private final SupervisionMessageSettings getMessageSettings(OgemaHttpRequest req) {
		final SystemSupervisionConfig config = configSelector.getSelectedItem(req);
		return config == null ? null : config.getSubResource(MESSAGE_CONFIG_NAME, SupervisionMessageSettings.class);
	}
	
	
	private class LoggingCheckbox extends Checkbox {
		
		private static final long serialVersionUID = 1L;
		private static final String text = "Enable logging";
		private final String relativePath;

		public LoggingCheckbox(WidgetPage<?> page, String id, String relativePath) {
			super(page, id, true);
			this.relativePath = relativePath;
			setDefaultList(Collections.singletonMap(text, false));
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final SystemSupervisionConfig config = configSelector.getSelectedItem(req);
			final SingleValueResource resource = config == null ? null : config.results().<SingleValueResource> getSubResource(relativePath);
			if (resource == null) {
				disable(req);
				return;
			}
			enable(req);
			final boolean log = LoggingUtils.isLoggingEnabled(resource);
			setCheckboxList(Collections.singletonMap(text, log), req);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			final SystemSupervisionConfig config = configSelector.getSelectedItem(req);
			final SingleValueResource resource = config == null ? null : config.results().<SingleValueResource> getSubResource(relativePath);
			if (resource == null) {
				return;
			}
			final Boolean doLog = getCheckboxList(req).get(text);
			if (doLog == null)
				return;
			if (doLog)
				LoggingUtils.activateLogging(resource, -2);
			else
				LoggingUtils.deactivateLogging(resource);
		}
		
	}
	
	private class UpdateField extends ValueInputField<Long> {
		
		private static final long serialVersionUID = 1L;
		private final String relativePath;
		private final long defaultUpdateRate;
		private final static long minute = 60*1000;

		public UpdateField(WidgetPage<?> page, String id, String relativePath, long defaultUpdateRate) {
			super(page, id, Long.class);
			this.relativePath = relativePath;
			this.defaultUpdateRate = defaultUpdateRate;
			triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final SystemSupervisionConfig config = configSelector.getSelectedItem(req);
			final TimeResource resource = config == null ? null : config.<TimeResource> getSubResource(relativePath);
			if (resource == null) {
				setNumericalValue(null, req);
				return;
			}
			final long rate = SupervisionUtils.getInterval(resource, defaultUpdateRate);
			setNumericalValue(rate/minute, req);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			final SystemSupervisionConfig config = configSelector.getSelectedItem(req);
			final TimeResource resource = config == null ? null : config.<TimeResource> getSubResource(relativePath);
			final Long value = getNumericalValue(req);
			if (resource == null || value == null || value*minute < SupervisionUtils.MIN_SUPERVISION_ITV) {
				return;
			}
			resource.setValue(value*minute);
		}
		
	}
	
	static final int mb = 1024*1024;
	
	private class MbLabel extends Label {

		private static final long serialVersionUID = 1L;
		private final String relativePath;
		
		public MbLabel(WidgetPage<?> page, String id, String relativePath) {
			super(page, id);
			this.relativePath = relativePath;
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final SystemSupervisionConfig config = configSelector.getSelectedItem(req);
			final TimeResource resource = config == null ? null : config.results().<TimeResource> getSubResource(relativePath);
			if (resource == null || !resource.isActive()) {
				setText("", req);
				return;
			}
			setText((resource.getValue()/mb) + " MB", req);
		}
		
	}

	private class TriggerButton extends Button {
		
		private static final long serialVersionUID = 1L;
		private final String relativePath;
		
		public TriggerButton(WidgetPage<?> page, String id, String relativePath) {
			super(page, id);
			setDefaultText("Recalculate");
			this.relativePath = relativePath;
			addDefaultStyle(ButtonData.BOOTSTRAP_LIGHT_BLUE);
		}
		
		private final BooleanResource getResource(OgemaHttpRequest req) {
			final SystemSupervisionConfig config = configSelector.getSelectedItem(req);
			return (config == null ? null : config.<BooleanResource> getSubResource(relativePath));
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final BooleanResource resource = getResource(req);
			if (resource == null || !resource.isActive()) 
				disable(req);
			else
				enable(req);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			final BooleanResource resource = getResource(req);
			if (resource == null || !resource.isActive())
				return;
			resource.setValue(true);
			try { // wait a bit, so value might have a chance to update immediately
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
		
	}
	
}
