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
package org.ogema.apps.simulation.gui.speed;

import org.ogema.core.administration.AdministrationManager;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.ResourceManagement;

import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.textfield.TextField;

public class SimulationSpeedField extends TextField {

	private static final long serialVersionUID = 1L;
	private final ResourceManagement rm;
	private final AdministrationManager admin;
	private final OgemaLogger logger;
	
	public SimulationSpeedField(WidgetPageBase<?> page, String id, ApplicationManager am) {
		super(page, id, true);
		this.rm = am.getResourceManagement();
		this.admin = am.getAdministrationManager();
		this.logger = am.getLogger();
	}

	@Override
	public void onPOSTComplete(String data, de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest req) {
		try {
			float newValue = Float.parseFloat(getValue(req));
			if (newValue > 0) {
				SimulationFactor config = rm.createResource("simulationConfiguration", SimulationFactor.class);
				config.factor().create();
				config.factor().setValue(newValue);
				config.activate(true);
				Thread.sleep(400); // allow for callbacks before triggering GET
				logger.debug("Set simulation speed resource to " +newValue );
			}
			else throw new RuntimeException("Simulation speed factor must be greater than 0.");
		} catch (Exception e) {
			logger.error("Invalid simulation speed factor. " + e);
		}
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		setValue(String.valueOf(admin.getFrameworkClock().getSimulationFactor()), req);
	}
	
}
