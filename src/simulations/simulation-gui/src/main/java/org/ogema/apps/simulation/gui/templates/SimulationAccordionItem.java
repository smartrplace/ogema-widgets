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
package org.ogema.apps.simulation.gui.templates;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ogema.apps.simulation.gui.Utils;
import org.ogema.apps.simulation.gui.configuration.ConfigModal;
import org.ogema.apps.simulation.gui.configuration.CreateModal;
import org.ogema.apps.simulation.gui.configuration.PlotsModal;
import org.ogema.apps.simulation.gui.configuration.SimQuModal;
import org.ogema.apps.simulation.gui.configuration.SimQuModal.SimQuModalOptions;
import org.ogema.core.model.Resource;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.tools.simulation.service.api.SimulationProvider;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.DynamicTableData;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.label.Label;

public class SimulationAccordionItem extends RowTemplate<String> {
	
	private final SimulationProvider<? extends Resource> provider;
	private final DynamicTable<String> headerTable;
	private final DynamicTable<String> table;
	private final String id;   // this is the id of the SimulationProvider
	private final WidgetPageBase<?> page;
	private final Map<String,Row> rows;
	private final ConfigModal configModal;
	private final CreateModal createModal;
	private final SimQuModal simQuModal;
	private final PlotsModal plotsModal;
	private final Button createBtn;
	private final Label descriptionLabel;
	private final Alert alert;
	private final PageSnippet panel;
	
	private final static String COL1 = "colResource";
	private final static String COL2 = "colValues";
	private final static String COL3 = "colPlots";
	private final static String COL4 = "colConfBtn";
	private final static String COL5 = "colActive";
	private final static String COL6 = "colToggleActivationBtn";
	

	public SimulationAccordionItem(final SimulationProvider<? extends Resource> provider, WidgetPageBase<?> page, 
			ConfigModal modal, final CreateModal createModal, SimQuModal simQuModal, PlotsModal plotsModal, final Alert alert) {
		this.provider = provider;
		this.page = page;
		this.id = ResourceUtils.getValidResourceName(provider.getProviderId());
		this.rows = new LinkedHashMap<String,Row>();	
		this.configModal = modal;
		this.createModal = createModal;
		this.simQuModal = simQuModal;
		this.plotsModal = plotsModal;
		this.alert = alert;
		this.createBtn = new Button(page,"createBtn__" + id,true) {
			
			private static final long serialVersionUID = 1L;
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				sessionData.setCreationProvider(provider);
				
				createModal.setProvider(provider, req);		
				alert.setWidgetVisibility(false,req);
				SimulationProvider<? extends Resource> provider =  createModal.getProvider(req); // Utils.getInstance().getCreationProvider(page, req);
				//if (provider == null) return super.retrieveGETData(req);
				String type;
				if (provider == null) {
					type = "not selected";
				}
				else {
					type = provider.getSimulatedType().getSimpleName();
				}
				createModal.setTitle("Create new object",req);
				createModal.setHeaderLabel("Type: "  + type, req);				
				//headerLabel.setText("Type: "  + type, req);
				
			/*	String name = Utils.getInstance().getNextResourceName(provider);
				provider.createSimulatedObject(name);
				alert.setText("New simulated object created: " + name);
				alert.setVisible(true);
				alert.setColor(BootstrapColor.GREEN); */
			}
		};
		createBtn.setText("Create new simulated object",null);
//		createBtn.setCss("btn btn-info");
		createBtn.addStyle(ButtonData.BOOTSTRAP_LIGHT_BLUE,null);
		createBtn.triggerAction(createModal.getSubWidgets(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		createBtn.triggerAction(createModal, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		createBtn.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		this.headerTable = new DynamicTable<String>(page,"headerTable__"+id,true);
		Map<String,Object> descriptionMap = new LinkedHashMap<>();
		descriptionLabel = new Label(page, "description__" + id,true);
		descriptionLabel.setText(provider.getDescription(),null);
		descriptionMap.put("descriptionCell", descriptionLabel);
		descriptionMap.put("createButtonCell", createBtn);
		headerTable.addRow("row1", descriptionMap,null);
		headerTable.setColumnSize("descriptionCell", 9,null);
		headerTable.setColumnSize("createButtonCell", 3,null);
		Map<String,String> properties = new LinkedHashMap<String, String>();
		properties.put("font-size", "15px");
		properties.put("background-color","#606060");
		properties.put("color","#cccccc");		
		headerTable.addCssItem("tr", properties,null);
//		headerTable.setWidgetAlignment(descriptionLabel.getId(), "left");
		headerTable.setStyle(DynamicTableData.CELL_ALIGNMENT_CENTERED,null);
//		Map<String,String> propertiesTd = new LinkedHashMap<String, String>();
//		propertiesTd.put("text-align","left");
//		headerTable.addCssItem("tr td", propertiesTd); 

		this.table = new DynamicTable<String>(page, id + "_table",true) {
			
			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (this.getRowTemplate() != null ) {
					updateRows();
				}
			}
			
			private void updateRows() {
				List<? extends Resource> items = provider.getSimulatedObjects();
				List<String> locs = new LinkedList<String>();
				Iterator<? extends Resource> itRes = items.iterator();
				Set<String> oldRows = rows.keySet();
				while(itRes.hasNext()) {
					Resource res = itRes.next();
					String loc = res.getLocation();
					locs.add(loc);
					if (!oldRows.contains(loc)) {
						this.addItem(loc,null);
					}
				}
				Iterator<String> it = oldRows.iterator();
				while (it.hasNext()) {
					if (!locs.contains(it.next())) it.remove();
				}
			}
		};
//		table.addRow("headerRow", getTableHeader(),null);
		table.setRowTemplate(this);
		Map<String,String> propertiesMainT = new LinkedHashMap<String, String>();
		propertiesMainT.put("font-weight","bold");
		//propertiesMainT.put("color", "red");
		//table.addCssItem("", propertiesMainT); // for demonstration: empty selector means it will be applied to all subelements
		table.addCssItem("tr:first-child", propertiesMainT,null);
		table.setStyle(DynamicTableData.CELL_ALIGNMENT_CENTERED,null);
		table.onGET(null);;  // initialize subwidgets
		//createBtn.triggerAction(table.getId(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		createModal.getConfirmButton().triggerAction(table, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);

		
		panel = new PageSnippet(page, id + "_panel",true);
		panel.append(headerTable, null);
		panel.linebreak(null);
		panel.append(table, null);
		
		
		
	}
	
/*	public String getHTML() {
	//	return "<table><tr><td><p>" + provider.getDescription() + "</p></td><td><div id = " + createBtn.getId()  
	//			+ "></div></td></tr></table><br><div id = " +  table.getId() + "></div>";
		return "<div id = " +  headerTable.getId() + "></div> <br> <div id = " +  table.getId() + "></div>";
	} */
	
	public OgemaWidgetBase<?> getPanel() {
		return panel;
	}
	
	public void destroy() {
		Iterator<String> it = rows.keySet().iterator();
		while (it.hasNext()) {
			String row = it.next();
			table.removeRow(row,null);  // deregister sub-widgets
		}
		page.unregister(table);
	}


	@Override
	public Row addRow(final String lineId,OgemaHttpRequest req) {  // lineId is resource location of simulated device
		Row row = new Row();
		String lineIdSafe = ResourceUtils.getValidResourceName(lineId);
		Label label = new Label(page, id + "__" + lineIdSafe,true);		
		label.setText(lineId,req);
		row.addCell(COL1, label);
		
		Button valBtn = new Button(page,"valBtn__" + lineIdSafe,true) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				//modal.setDevice(lineId);
				alert.setWidgetVisibility(false,req);
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) page.getSession(req);
//				sessionData.setData(provider, lineId);
				simQuModal.setProvider(provider, req);
				simQuModal.setDeviceId(lineId, req);
				
				SimQuModalOptions options = simQuModal.getSessionData(req);
//				String activeDevice = getActiveDevice(req);
				String activeDevice = options.getDeviceId();
				if (activeDevice == null) return;
				StringBuilder titleHTML = new StringBuilder("Active device: ");
				titleHTML.append(activeDevice);
				simQuModal.setTitle(titleHTML.toString(),req);
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				sessionData.setSimQuDescription("");
//				sessionData.setSimQuId("");
//				sessionData.setSimQuValue("");
//				sessionData.setSimQuPath("");	
				options.setDescription("");
				options.setSimQuId("");
				options.setSimQuValue("");
				options.setSimQuPath("");
				
			}
			
		};
		valBtn.setText("Show values",null);
		//valBtn.setCss("btn btn-default");
		valBtn.addStyle(ButtonData.BOOTSTRAP_DEFAULT,null);
		row.addCell(COL2,valBtn);
		//cnfBtn.triggerAction(modal.getModalTable().getId(), "POST", "sendGET");
		valBtn.triggerAction(simQuModal.getSubWidgets(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		valBtn.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		valBtn.triggerAction(simQuModal, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);		
		simQuModal.triggerAction(simQuModal, TriggeringAction.GET_REQUEST, TriggeredAction.SHOW_WIDGET);
		
		Button plotsBtn = new Button(page,"plotsBtn__" + lineIdSafe,true) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				//modal.setDevice(lineId);
				alert.setWidgetVisibility(false,req);
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) page.getSession(req);
//				sessionData.setData(provider, lineId);			
				plotsModal.setProvider(provider, req);
				plotsModal.setDeviceId(lineId, req);
				String activeDevice = plotsModal.getDeviceId(req);
//				System.out.println("  plots modal device Id: " + lineId + ", " + activeDevice);
				if (activeDevice == null) return;
				StringBuilder titleHTML = new StringBuilder("Active device: ");
				titleHTML.append(activeDevice);
				plotsModal.setTitle(titleHTML.toString(),req);
				Map<String,Map> map = Utils.getInstance().getLoggedDataPanels(plotsModal.getProvider(req),activeDevice);
				// System.out.println("  logged data panels for " + activeDevice + ": " + map);
				plotsModal.setPanels(map);			
				
			}
			
		};
		plotsBtn.setText("Show plots",null);
		plotsBtn.addStyle(ButtonData.BOOTSTRAP_BLUE,null);
		row.addCell(COL3, plotsBtn);
		plotsBtn.triggerAction(plotsModal, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		plotsModal.triggerAction(plotsModal, TriggeringAction.GET_REQUEST, TriggeredAction.SHOW_WIDGET);
		
		Button cnfBtn = new Button(page,"cnfBtn__" + lineIdSafe,true) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				//modal.setDevice(lineId);
				alert.setWidgetVisibility(false,req);
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) page.getSession(req);
//				sessionData.setData(provider, lineId);
//				sessionData.showValueDropdown = false;
				configModal.setProvider(provider, req);
				configModal.setDeviceId(lineId, req);
				
				String activeDevice = configModal.getDeviceId(req);
				if (activeDevice == null) return;
				StringBuilder titleHTML = new StringBuilder("Active device: ");
				titleHTML.append(activeDevice);
				configModal.setTitle(titleHTML.toString(),req);
//				SimulationAppSessionData sessionData = (SimulationAppSessionData) getPage().getSession(req);
//				sessionData.setConfigDescription("");
				
				configModal.setConfigDescription("", req);
				configModal.setConfigId("", req);
//				sessionData.setConfigId("");
//				sessionData.setConfigValue("");
				configModal.setConfigValue("", req);
				
			}
			
		};
		cnfBtn.setText("Open configuration",null);
		//cnfBtn.setCss("btn btn-default");
		cnfBtn.addStyle(ButtonData.BOOTSTRAP_DEFAULT,null);
		row.addCell(COL4,cnfBtn);
		//cnfBtn.triggerAction(modal.getModalTable().getId(), "POST", "sendGET");
		cnfBtn.triggerAction(configModal.getSubWidgets(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		cnfBtn.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		cnfBtn.triggerAction(configModal, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);  // ?
		configModal.triggerAction(configModal, TriggeringAction.GET_REQUEST, TriggeredAction.SHOW_WIDGET);
		
		Label activeLabel =  new Label(page, "active" + "__" + lineIdSafe) {

			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(provider.isSimulationActive(lineId) ? "active" : "inactive",req);
			}
		};		
		row.addCell(COL5, activeLabel);
		
		Button toggleActivationBtn = new Button(page, "toggleBtn__" + lineIdSafe,true) {

			private static final long serialVersionUID = 1L;
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(provider.isSimulationActive(lineId) ? "deactivate" : "activate",req);
				if(provider.isSimulationActivatable(lineId)) {
					enable(req);
				} else {
					disable(req);
				}
				//setCss(provider.isSimulationActive(lineId) ? "btn btn-danger" : "btn btn-success");
				setStyle(provider.isSimulationActive(lineId) ? ButtonData.BOOTSTRAP_RED : ButtonData.BOOTSTRAP_GREEN,req);
			}
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				try {				
					if (provider.isSimulationActive(lineId)) {
						provider.stopSimulation(lineId);
					}
					else {
						provider.startSimulation(lineId);
					}
					alert.setWidgetVisibility(false,req);
				} catch (Exception e) {
					alert.setText("Could not toggle activation state: " + e.getMessage(), req);
					alert.setStyle(AlertData.BOOTSTRAP_DANGER, req);
					alert.setWidgetVisibility(true,req);
					alert.autoDismiss(10000, req);
				}
			}
		};
		toggleActivationBtn.triggerAction(activeLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		toggleActivationBtn.triggerAction(toggleActivationBtn, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		toggleActivationBtn.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		row.addCell(COL6, toggleActivationBtn);
		rows.put(lineId,row);
		return row;
	}
	
	public Map<String,Object> getHeader() {
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		map.put(COL1,"Simulated device");
		map.put(COL2,"Simulated values");
		map.put(COL3,"Plot logged quantities");
		map.put(COL4,"Configuration");
		map.put(COL5,"Status");
		map.put(COL6,"Toggle simulation status");
		return map;
		
	}
	
	@Override
	public String getLineId(String object) {
		return object;
	}

}
