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
