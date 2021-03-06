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
 * Fraunhofer IIS Fraunhofer ISE Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.apps.simulation.gui.templates;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ogema.apps.simulation.gui.Utils;
import org.ogema.core.application.ApplicationManager;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.tools.simulation.service.api.SimulationProvider;

import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.label.Label;

public class SimulatedDeviceRowTemplate extends RowTemplate<String> {
	
	private final WidgetPageBase<?> page;
	private final ApplicationManager am;
	private final List<SimulationProvider<?>> providers;
	private static final long UPDATE_NUMBERS_ITV = 30000L; //  usually not required to update very often
	//private ComplexTable table;

	public SimulatedDeviceRowTemplate(WidgetPageBase<?> page, ApplicationManager am, List<SimulationProvider<?>> providers) {
		super();
		this.page = page;
		this.am = am;
		this.providers = providers;
		//this.table = table;
	}

	@Override
	public Row addRow(final String devicePath, OgemaHttpRequest req) {
		final SimulationProvider<?> provider = getProvider(devicePath);
		if (provider == null) return null;
		String lineId = ResourceUtils.getValidResourceName(devicePath);
		Row row = new Row();
		Label label = new Label(page, "provider_" + lineId,true) {
			private static final long serialVersionUID = 1L;			
		};		
		label.setText(provider.getProviderId(),req);
		row.addCell("colProvider", label);
		
		Label typeLabel = new Label(page, "type_" + lineId,true) {
			private static final long serialVersionUID = 1L;			
		};		
		typeLabel.setText(provider.getSimulatedType().getSimpleName(),req);
		row.addCell("colType", typeLabel);
		
		Label nrObjects = new Label(page,"nr_" + lineId,true) {

			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				this.setText(String.valueOf(provider.getSimulatedObjects().size()),req);
			}
		};
		nrObjects.setPollingInterval(UPDATE_NUMBERS_ITV,null);
		row.addCell("colNrObjects", nrObjects);
		
		Button addBtn = new Button(page, "addBtn_" + lineId, "Add simulated object",true) {

			private static final long serialVersionUID = 1L;
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				provider.createSimulatedObject(Utils.getInstance().getNextResourceName(provider,am.getResourceAccess()));
			}
			
		};
		addBtn.setCss("btn btn-primary",null);
		addBtn.triggerAction(nrObjects, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		row.addCell("colBtn", addBtn);
		
		return row;
	}
	
	private SimulationProvider<?> getProvider(String providerId) {
		Iterator<SimulationProvider<?>> it = providers.iterator();
		while(it.hasNext()) {
			SimulationProvider<?> sp = it.next();
			if (Utils.getInstance().getValidWidgetId(sp.getProviderId()).equals(providerId)) return sp;
		}
		return null;
	}

	@Override
	public String getLineId(String object) {
		return object;
	}

	@Override
	public Map<String, Object> getHeader() {
		 Map<String,Object> header = new LinkedHashMap<>();
        header.put("colProvider", "Simulation Provider");
        header.put("colType", "Simulated Type");
        header.put("colNrObjects", "Number of simulated instances");
        header.put("colBtn", "Add instance");
        return header;
	}
	
	

}
