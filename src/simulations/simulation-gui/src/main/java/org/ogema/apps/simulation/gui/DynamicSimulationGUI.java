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
package org.ogema.apps.simulation.gui;

import java.util.Map;

import org.ogema.apps.simulation.gui.speed.SimulationSpeedField;
import org.ogema.apps.simulation.gui.templates.SimulationAccordionItem;
import org.ogema.core.application.ApplicationManager;

import de.iwes.widgets.api.extended.WidgetAppImpl;
import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.html.accordion.Accordion;
import de.iwes.widgets.html.accordion.AccordionData;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.button.RedirectButton;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;

public class DynamicSimulationGUI extends WidgetPageBase {
	
	private Accordion acc;

	public DynamicSimulationGUI(WidgetAppImpl app, String startHtml,ApplicationManager am, Map<String,SimulationAccordionItem> accItems ) {
		super(app, startHtml);
		this.acc = new SimulationAccordion(this, "simAccWidget",accItems);
		acc.setStyle(AccordionData.BOOTSTRAP_LIGHT_BLUE,null);
		RedirectButton btn = new RedirectButton(this,"plotsRedirect","Show plots","plots/index.html",true);
		//btn.setCss("btn btn-primary");
		btn.addStyle(ButtonData.BOOTSTRAP_BLUE, null);
		Label simSpeedLabel = new Label(this,"simSpeedLabel","Simulation speed factor",true);
		TextField simSpeedField = new SimulationSpeedField(this, "simSpeedField", am);
		simSpeedField.triggerAction(simSpeedField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}

	public Accordion getAccordion() {
		return acc;
	}
	
	public static class SimulationAccordion extends Accordion {
		
		private static final long serialVersionUID = 1L;
		private Map<String,SimulationAccordionItem> accItems;

		public SimulationAccordion(WidgetPageBase<?> page, String id,Map<String,SimulationAccordionItem> accItems) {
			super(page, id,true);
			this.accItems = accItems;
		}
		
	}


}
