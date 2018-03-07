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
package de.iwes.widgets.reswidget.scheduleviewer.clone.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.reswidget.scheduleviewer.api.ConditionalTimeSeriesFilter;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationProvider.SelectionConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilter;
import de.iwes.widgets.reswidget.scheduleviewer.clone.ScheduleViewerBasisConfig;

public class ScheduleViewerConfigPersistenceUtil {

	public  final String PROVIDER_NAME;
	private final String NO_RESOURCE_PATH_FOUND = "noResourcePathFound";
	private final String J_CONDITIONAL_TIME_SERIES_FILTER_CATEGORY_PRESELECTED = "conditionalTimeSeriesFilterCategoryPreselected";
	private final String J_FILTERS_PRE_SELECTED = "filtersPreSelected";
	private final String J_TIME_SERIES_SELECTED = "timeSeriesSelected";
	private final String J_PROGRAMS_PRESELECTED = "programsPreselected";
	private final String J_CONFIGURATION_ID = "configurationId";
	private final ApplicationManager appManager;
	private final Map<String, JSONObject> jsonConfigurations;
	private final Map<String, SelectionConfiguration> selectionConfigurations;
	private ScheduleViewerBasisConfig config;
	
	public ScheduleViewerConfigPersistenceUtil(ApplicationManager appManager, String providerName) {
		this.appManager = appManager;
		this.PROVIDER_NAME = providerName;
		jsonConfigurations = new HashMap<>();
		selectionConfigurations = new HashMap<>();		
		initConfigurationResource();
		readConfigurationResource();
	}
	
	public SelectionConfiguration getSelectionConfiguration(SelectionConfiguration providerSelectionConfiguration, String configurationId) {
		
		if(selectionConfigurations.containsKey(configurationId)) {
			return selectionConfigurations.get(configurationId);
		}
		
		JSONObject json = jsonConfigurations.get(configurationId); 
		
		if(json != null) {
			SelectionConfiguration configuration = parseConfigurationResource(json, providerSelectionConfiguration, configurationId);		
			selectionConfigurations.put(json.getString(J_CONFIGURATION_ID), configuration);
			return configuration;
		}
		
		return providerSelectionConfiguration;		
	}
	
	public void saveCurrentSelectionConfiguration(SelectionConfiguration currentConfiguration, String configurationId) {
		final List<ReadOnlyTimeSeries> timeSeriesSelected = currentConfiguration.timeSeriesSelected();
		final List<Collection<TimeSeriesFilter>> programsPreselected = currentConfiguration.programsPreselected();
		final Integer conditionalTimeSeriesFilterCategoryPreselected = currentConfiguration
				.conditionalTimeSeriesFilterCategoryPreselected();
		final List<ConditionalTimeSeriesFilter<?>> filtersPreSelected = currentConfiguration.filtersPreSelected();

		final List<String> pathsOfTimeSeries = new ArrayList<>();
		final List<String> idsOfProgramms = new ArrayList<>();
		final List<String> idsOfFilers = new ArrayList<>();

		for (final ReadOnlyTimeSeries ts : timeSeriesSelected) {
			pathsOfTimeSeries.add(getPath(ts));
		}

		for (final Collection<TimeSeriesFilter> col : programsPreselected) {
			for (final TimeSeriesFilter programm : col) {
				idsOfProgramms.add(programm.id());
			}
		}

		for (final ConditionalTimeSeriesFilter<?> filter : filtersPreSelected) {
			idsOfFilers.add(filter.id());
		}

		if (configurationId == null) {
			configurationId = "config_" + jsonConfigurations.size();
		}

		final JSONObject json = new JSONObject();
		json.put(J_CONFIGURATION_ID, configurationId);
		json.put(J_PROGRAMS_PRESELECTED, idsOfProgramms);
		json.put(J_TIME_SERIES_SELECTED, pathsOfTimeSeries);
		json.put(J_FILTERS_PRE_SELECTED, idsOfFilers);
		json.put(J_CONDITIONAL_TIME_SERIES_FILTER_CATEGORY_PRESELECTED, conditionalTimeSeriesFilterCategoryPreselected);
		saveJSONConfiguration(json.toString(), configurationId);
		jsonConfigurations.put(configurationId, json);
	}

	
	
	private void initConfigurationResource() {
		config = appManager.getResourceAccess().getResource(PROVIDER_NAME);
		if (config == null) {
			config = appManager.getResourceManagement().createResource(PROVIDER_NAME, ScheduleViewerBasisConfig.class);
		}		
		if(!config.isActive()) {
			config.create();
			config.activate(true);
		}
	}
	
	private void readConfigurationResource() {
	 List<StringResource> list = config.sessionConfigurations().getSubResources(StringResource.class, true); 
		 
		 for(StringResource resource : list) {
			 String string = resource.getValue();
			 JSONObject json = new JSONObject(string);
			 jsonConfigurations.put(json.getString(J_CONFIGURATION_ID), json); 
		 }	
	}
	
	private SelectionConfiguration parseConfigurationResource(JSONObject json, SelectionConfiguration selectionConfiguration, String configurationId) {

		final Integer conditionalTimeSeriesFilterCategoryPreselected = json
				.getInt(J_CONDITIONAL_TIME_SERIES_FILTER_CATEGORY_PRESELECTED);
		JSONArray jtimeSeriesSelected = json.getJSONArray(J_TIME_SERIES_SELECTED);
		JSONArray jprogrammsSelected = json.getJSONArray(J_PROGRAMS_PRESELECTED);
		JSONArray jfilterSelected = json.getJSONArray(J_FILTERS_PRE_SELECTED);
		final List<String> pathsOfTimeSeries = convertJsonStringArray(jtimeSeriesSelected);
		final List<String> idsOfProgramms = convertJsonStringArray(jprogrammsSelected);
		final List<String> idsOfFilers = convertJsonStringArray(jfilterSelected);

		final List<ReadOnlyTimeSeries> timeSeriesSelected = new ArrayList<>();
		final List<ConditionalTimeSeriesFilter<?>> filtersPreSelected = new ArrayList<>();
		final List<Collection<TimeSeriesFilter>> programsPreselected = new ArrayList<>();
		for (String path : pathsOfTimeSeries) {
			ReadOnlyTimeSeries ts = getReadOnlyTimeSeries(path);
			if (ts != null) {
				timeSeriesSelected.add(ts);
			}
		}
		for (String filterId : idsOfFilers) {
			for (ConditionalTimeSeriesFilter<?> filter : selectionConfiguration.filtersPreSelected()) {
				if (filterId.equals(filter.id())) {
					filtersPreSelected.add(filter);
				}
			}
		} 

		for (String programmId : idsOfProgramms) {
			for (Collection<TimeSeriesFilter> outerProgrammFilter : selectionConfiguration.programsPreselected()) {
				Collection<TimeSeriesFilter> list = new ArrayList<>();
				for (TimeSeriesFilter innerProgrammFilter : outerProgrammFilter) {
					if (programmId.equals(innerProgrammFilter.id())) {
						list.add(innerProgrammFilter);
					}
				}
				if (!list.isEmpty()) {
					programsPreselected.add(list);
				}
			}
		} 
		
		SessionConfigurationPersistent sessionConfiguration = new SessionConfigurationPersistent(appManager,
				configurationId, timeSeriesSelected, programsPreselected, conditionalTimeSeriesFilterCategoryPreselected,
				filtersPreSelected);
		return sessionConfiguration;
	}

	


	private void saveJSONConfiguration(String jsonConfiguration, String configurationId) {
		StringResource entry = this.config.sessionConfigurations().getSubResource(configurationId,
				StringResource.class);
		if (entry == null) {
			entry = config.sessionConfigurations().addDecorator(configurationId, StringResource.class);
		}

		if (!entry.isActive()) {
			entry.create();
			entry.activate(true);
		}
		entry.setValue(jsonConfiguration);
	}

	private String getPath(ReadOnlyTimeSeries ts) {
		String path = NO_RESOURCE_PATH_FOUND;
		if (ts instanceof Schedule) {
			Schedule schedule = (Schedule) ts;
			path = schedule.getPath();
		} else if (ts instanceof RecordedData) {
			RecordedData recordedData = (RecordedData) ts;
			path = recordedData.getPath();
		}
		return path;
	}

	private ReadOnlyTimeSeries getReadOnlyTimeSeries(String path) {

		Resource resource = appManager.getResourceAccess().getResource(path);

		if (resource instanceof ReadOnlyTimeSeries) {
			ReadOnlyTimeSeries ts = (ReadOnlyTimeSeries) resource;
			return ts;
		}
		return null;
	}

	private List<String> convertJsonStringArray(JSONArray array) {
		final List<String> list = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			String id = array.getString(i);
			list.add(id);
		}
		return list;
	}

	
}
