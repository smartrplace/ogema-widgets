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
package de.iwes.tools.resource.tree.manipulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.array.ArrayResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirm;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirmData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.checkbox.Checkbox;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.listselect.ListSelect;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.resource.widget.autocomplete.ResourcePathAutocomplete;
import de.iwes.widgets.resource.widget.dropdown.ReferenceDropdown;
import de.iwes.widgets.resource.widget.dropdown.ResourceTypeDropdown;
import de.iwes.widgets.resource.widget.textfield.ArrayResourceListGroup;
import de.iwes.widgets.resource.widget.textfield.ValueResourceTextField;

public class ManipulatorPageBuilder {

	private final ApplicationManager am;
	private final Header header;
	private final Alert alert;
	private final Label typeSelectorLabel;
	private final ResourceTypeDropdown typeSelector;
	private final Label resourceSelectorLabel;
	private final ResourcePathAutocomplete resourceSelector;
	private final Label resourceTypeLabel;
	private final Label resourceType;
	private final WidgetGroup resourceDependencies;
	private final Label activationLabel;
	private final Button activationBtn;
	private final Label referenceLabel;
	private final ReferenceDropdown referenceDropdown;
	private final Label valueLabel;
	private final ValueResourceTextField<SingleValueResource> value;
	private final Popup arrayResourcePopup;
	private final Button triggerArrayPopup;
	private final Label arrayLabel;
//	private final ArrayResourceTable<ArrayResource> arrayTable;
	private final ArrayResourceListGroup<ArrayResource> arrayTable;
	private final Label deleteLabel;
	private final ButtonConfirm deleteBtn;
	private final Header addSubResHeader;
	private final WidgetGroup addSubWidgetGroup;
	private final Label selectSubTypeLabel;
	private final ResourceTypeDropdown subTypeSelector;
	private final Label subNameLabel;
	private final TextField subNameField;
	private final Label subAsReferenceLabel;
	private final Checkbox subAsReferenceCheckbox;
	private final Label subReferenceLabel;
	private final ReferenceDropdown subReferenceField;
	private final AddSubButton addSubButton;
//	private final DynamicTable subResTable;
	private final ListSelect subResTable2;
	private final Popup createPopup;
	private final Label subParentLabel;
	private final Label subParent;
	private final Label subresPopupLabel;
	private final Button subresPopupBtn;
	private final Label toplevelPopupLabel;
	private final Button toplevelPopoupBtn;
	
	public ManipulatorPageBuilder(WidgetPage<?> page, ApplicationManager am) {
		this.am = am;
		final ResourceAccess ra = am.getResourceAccess();
		this.header = new Header(page, "header", "Resource tree manipulator");
		header.addDefaultStyle(HeaderData.CENTERED);
		page.append(header).linebreak();
		
		this.alert  =new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);
		page.append(alert).linebreak();
		
		this.typeSelectorLabel = new Label(page, "typeSelectorLabel", "Select a Resource type");
		this.typeSelector = new ResourceTypeDropdown(page, "typeSelector", false, ra);
		
		this.resourceSelectorLabel = new Label(page, "resourceSelectorLabel", "Enter a Resource path");
		this.resourceSelector = new ResourcePathAutocomplete(page, "resourceSelector", ra, true) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				Class<? extends Resource> type = typeSelector.getSelectedType(req);
				if (type == null)
					type = Resource.class;
				setResourceType(type, req);
			}
			
		};
		resourceSelector.setDefaultPlaceholder("Resource path");
		
		this.resourceTypeLabel = new Label(page, "resourceTypeLabel", "Resource type");
		this.resourceType = new Label(page, "resourceType") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				Resource res = resourceSelector.getSelectedResource(req);
				if (res == null)
					setText("", req);
				else
					setText(res.getResourceType().getName(), req);
			}
			
		};
		
		this.activationLabel  = new Label(page, "activationLabel", "Toggle activation status");
		this.activationBtn = new Button(page, "activationBtn", "Activate") {
	
			private static final long serialVersionUID = 1L;

			public void onGET(OgemaHttpRequest req) {
				Resource res = resourceSelector.getSelectedResource(req);
				if (res == null) {
					setWidgetVisibility(false, req);
					disable(req);
				}
				else {
					enable(req);
					setWidgetVisibility(true, req);
					if (res.isActive()) {
						setStyle(ButtonData.BOOTSTRAP_RED, req);
						setText("Deactivate", req);
					} else {
						setStyle(ButtonData.BOOTSTRAP_GREEN, req);
						setText("Activate", req);
					}
					
				}
			};
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				Resource res = resourceSelector.getSelectedResource(req);
				if (res == null) 
					return;
				if (res.isActive())
					res.deactivate(false);
				else
					res.activate(false);
			}
			
		};
		
		this.valueLabel = new Label(page, "valueLabel", "Resource value");
		this.value = new ValueResourceTextField<SingleValueResource>(page, "value") {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				Resource res = resourceSelector.getSelectedResource(req);
				if (res instanceof SingleValueResource) {
					selectItem((SingleValueResource) res, req);
					setWidgetVisibility(true, req);
				}
				else {
					selectItem(null, req);
					setWidgetVisibility(false, req);
				}
			}
			
		};
		
		arrayResourcePopup = new Popup(page, "arrayResourcePopup", true);
		arrayTable = new ArrayResourceListGroup<ArrayResource>(page, "arrayResourceTable", false) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				Resource res = resourceSelector.getSelectedResource(req);
				if (!(res instanceof ArrayResource)) {
					selectItem(null, req);
				}
				else {
					selectItem((ArrayResource) res, req);
				}
			}
			
		};
//		arrayTable = new ArrayResourceTable<ArrayResource>(page, "arrayResourceTable") {
//
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public void onGET(OgemaHttpRequest req) {
//				Resource res = resourceSelector.getSelectedResource(req);
//				if (!(res instanceof ArrayResource)) {
//					selectItem(null, req);
//				}
//				else {
//					selectItem((ArrayResource) res, req);
//				}
//			}
//			
//		};
		
		arrayResourcePopup.setBody(arrayTable, null);
		arrayLabel = new Label(page, "arrayLabel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				ArrayResource arr = arrayTable.getSelectedItem(req);
				if (arr == null) {
					setText("No Resource selected", req);
				}
				else {
					setText("Resource: " + arr.getPath(), req);
				}
			}
			
		};
		arrayResourcePopup.setHeader(arrayLabel, null);
		arrayResourcePopup.setTitle("ArrayResource values", null);
		triggerArrayPopup = new Button(page, "triggerArrayPopup", "Show values") {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				Resource res = resourceSelector.getSelectedResource(req);
				if (res instanceof ArrayResource) {
					setWidgetVisibility(true, req);
					enable(req);
				}
				else {
					setWidgetVisibility(false, req);
					disable(req);
				}
			}
			
		};
		
		PageSnippet valueSnippet = new PageSnippet(page, "valueSnippet", true);
		valueSnippet.append(value, null).append(triggerArrayPopup, null);
		
		
		this.referenceLabel = new Label(page, "referenceLabel", "Set as reference");
		this.referenceDropdown = new ReferenceDropdown(page, "referenceDropdown", am, alert) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				Resource res = resourceSelector.getSelectedResource(req);
				if (res == null || res.isTopLevel()) {
					disable(req);
					setWidgetVisibility(false, req);
					setResource(null, null, req);
				}
				else {
					enable(req);
					setWidgetVisibility(true, req);
					setResource(res, res.getResourceType(), req);
				}
			}
			
			
		};
		
		this.deleteLabel = new Label(page, "deleteLabel", "Delete resource");
		this.deleteBtn = new ButtonConfirm(page, "deleteBtn", "Delete") {

			private static final long serialVersionUID = 1L;
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				Resource res = resourceSelector.getSelectedResource(req);
				if (res == null) {
					alert.showAlert("Please select a resource", false, req);
					return;
				}
				String path  = res.getPath();
				res.delete();
				alert.showAlert("Resource " + path + " has been deleted", true, req);
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				Resource res = resourceSelector.getSelectedResource(req);
				if (res == null) {
					setWidgetVisibility(false, req);
					disable(req);
					setConfirmMsg("I should not be visible",req);
				} else {
					setWidgetVisibility(true, req);
					enable(req);
					setConfirmMsg("Do you really want to delete " + res.getPath() + "?", req);
				}
			}
			
		};
		List<WidgetStyle<?>> styles = new ArrayList<WidgetStyle<?>>();
		styles.add(ButtonConfirmData.BOOTSTRAP_RED);
		styles.add(ButtonConfirmData.CANCEL_BLUE);
		styles.add(ButtonConfirmData.CONFIRM_RED);
		deleteBtn.setDefaultStyles(styles);
		deleteBtn.setDefaultConfirmMsg("Confirm deletion");
		deleteBtn.setDefaultConfirmPopupTitle("Delete resource");
		
		
		this.subresPopupLabel = new Label(page, "subresPopupLabel", "Create subresource");
		this.subresPopupBtn = new Button(page, "subresPopupBtn", "Create") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				Resource res = resourceSelector.getSelectedResource(req);
				addSubButton.setParent(res, req);
				subAsReferenceCheckbox.setWidgetVisibility(true, req);
				subAsReferenceCheckbox.deselectAll(req);
				subReferenceField.selectSingleOption(DropdownData.EMPTY_OPT_ID, req);
			}
			@Override
			public void onGET(OgemaHttpRequest req) {
				Resource res = resourceSelector.getSelectedResource(req);
				if (res == null) {
					disable(req);
					setWidgetVisibility(false, req);
				}
				else {
					enable(req);
					setWidgetVisibility(true, req);
				}
			}
			
		};
		this.toplevelPopupLabel = new Label(page, "toplevelPopupLabel", "Create new toplevel resource");
		this.toplevelPopoupBtn = new Button(page, "toplevelPopupBtn","Create") {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				addSubButton.setParent(null, req);
				subAsReferenceCheckbox.setWidgetVisibility(false, req);
				subAsReferenceCheckbox.deselectAll(req);
				subReferenceField.selectSingleOption(DropdownData.EMPTY_OPT_ID, req);
			}
			
		};
		
		subresPopupBtn.addDefaultStyle(ButtonData.BOOTSTRAP_BLUE);
		toplevelPopoupBtn.addDefaultStyle(ButtonData.BOOTSTRAP_BLUE);
		
		List<OgemaWidget> dependencies = new ArrayList<OgemaWidget>();
		dependencies.add(resourceType);dependencies.add(resourceTypeLabel);
		dependencies.add(activationBtn);dependencies.add(activationBtn);
		dependencies.add(referenceLabel);dependencies.add(referenceDropdown);
		dependencies.add(value);dependencies.add(valueLabel);
		dependencies.add(triggerArrayPopup);
		dependencies.add(deleteBtn);dependencies.add(deleteLabel);
		dependencies.add(subresPopupLabel);dependencies.add(subresPopupBtn);
		resourceDependencies = page.registerWidgetGroup("resourceDependencies", dependencies);
		
		StaticTable table = new StaticTable(9, 2, new int[] {2,3});
		table.setContent(0, 0, typeSelectorLabel).setContent(0, 1, typeSelector);
		table.setContent(1, 0, resourceSelectorLabel).setContent(1, 1, resourceSelector);
		table.setContent(2, 0, resourceTypeLabel).setContent(2, 1, resourceType);
		table.setContent(3, 0, valueLabel).setContent(3, 1, valueSnippet);
		table.setContent(4, 0, referenceLabel).setContent(4, 1, referenceDropdown);
		table.setContent(5, 0, activationLabel).setContent(5, 1, activationBtn);
		table.setContent(6, 0, deleteLabel).setContent(6, 1, deleteBtn);
		table.setContent(7, 0, subresPopupLabel).setContent(7, 1, subresPopupBtn);
		table.setContent(8, 0, toplevelPopupLabel).setContent(8, 1, toplevelPopoupBtn);
		
		page.append(table).linebreak();
		
		this.addSubResHeader = new Header(page, "addSubResHeader", "Subresources");
		addSubResHeader.setDefaultHeaderType(2);
		addSubResHeader.addDefaultStyle(HeaderData.CENTERED);
		page.append(addSubResHeader).linebreak();
		this.createPopup = new Popup(page, "createPopup", true);
		createPopup.setTitle("Create Resource", null);
		PageSnippet createBodySnippet = new PageSnippet(page, "createBodySnippet", true);
		
		this.addSubButton = new AddSubButton(page, "addSubButton", "Create subresource");
		
		this.subParentLabel = new Label(page, "subParentLabel","Parent");
		this.subParent = new Label(page, "subParent") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				Resource res = addSubButton.getParent(req);
				if (res == null)
					setText("--", req);
				else 
					setText(res.getPath(), req);
			}
			
		};

		this.selectSubTypeLabel = new Label(page, "selectSubTypeLabel", "Select resource type");
		this.subTypeSelector = new ResourceTypeDropdown(page, "subTypeSelector", true, ra);
		this.subNameLabel = new Label(page, "subNameLabel", "Enter name");
		this.subNameField = new TextField(page, "subNameField");

		this.subAsReferenceLabel = new Label(page, "subAsReferenceLabel", "Create as reference");
		this.subAsReferenceCheckbox = new Checkbox(page, "subAsReferenceCheckbox");
		Map<String,Boolean> chckBoxOpts = new HashMap<String, Boolean>();
		chckBoxOpts.put("Reference", false);
		subAsReferenceCheckbox.setDefaultList(chckBoxOpts);
		this.subReferenceLabel = new Label(page, "subReferenceLabel", "Select reference");
		// this is a reference dropdown, but since we do not specify a base resource,
		// we only use it to select a resource of the selected type
		this.subReferenceField = new ReferenceDropdown(page, "subReferenceField", am, alert) {
	
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				Resource res = addSubButton.getParent(req);
				if (res == null || !subAsReferenceCheckbox.getCheckboxList(req).get("Reference")) {
					setResource(null, null, req);
					disable(req);
					setWidgetVisibility(false, req);
					return;
				}
				enable(req);
				setWidgetVisibility(true, req);
				Class<? extends Resource> type = subTypeSelector.getSelectedType(req);
				setResource(null, type, req);
			}
			
		};
				
		StaticTable addSubTable = new StaticTable(5, 2, new int[] {4,8});
		
		addSubTable.setContent(0, 0, subParentLabel).setContent(0, 1, subParent)
				   .setContent(1, 0, selectSubTypeLabel).setContent(1, 1, subTypeSelector)
				   .setContent(2, 0, subNameLabel).setContent(2, 1, subNameField)
				   .setContent(3, 0, subAsReferenceLabel).setContent(3, 1, subAsReferenceCheckbox)
				   .setContent(4, 0, subReferenceLabel).setContent(4, 1, subReferenceField);
		createBodySnippet.append(addSubTable, null);
		
		List<OgemaWidget> addSubResWidgets = new ArrayList<OgemaWidget>();
		addSubResWidgets.add(subParentLabel);addSubResWidgets.add(subParent);
		addSubResWidgets.add(addSubResHeader);addSubResWidgets.add(selectSubTypeLabel);
		addSubResWidgets.add(subTypeSelector);addSubResWidgets.add(subNameLabel);
		addSubResWidgets.add(subNameField);addSubResWidgets.add(subReferenceLabel);
		addSubResWidgets.add(subReferenceField);addSubResWidgets.add(addSubButton);
		addSubResWidgets.add(subAsReferenceCheckbox);
		this.addSubWidgetGroup = page.registerWidgetGroup("addSubWidgetGroup", addSubResWidgets);
		
		createPopup.setFooter(addSubButton, null);
		createPopup.setBody(createBodySnippet, null);
		
//		this.subResTable = new DynamicTable(page, "subResTable");
		this.subResTable2 = new SubResourcesTable(page, "subResTable2", resourceSelector);
		page.append(subResTable2).linebreak();
		page.append(createPopup).linebreak().append(arrayResourcePopup);
		
		
		setDependencies();
	}
	
	private void setDependencies() {
		typeSelector.triggerAction(resourceSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		resourceSelector.triggerAction(resourceDependencies, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		resourceSelector.triggerAction(resourceDependencies, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		resourceSelector.triggerAction(addSubWidgetGroup, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		resourceSelector.triggerAction(addSubWidgetGroup, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		activationBtn.triggerAction(activationBtn, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		value.triggerAction(value, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		triggerArrayPopup.triggerAction(arrayTable, TriggeringAction.PRE_POST_REQUEST, TriggeredAction.GET_REQUEST);
		triggerArrayPopup.triggerAction(arrayLabel, TriggeringAction.PRE_POST_REQUEST, TriggeredAction.GET_REQUEST,1);
		triggerArrayPopup.triggerAction(arrayResourcePopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		
		deleteBtn.triggerAction(resourceSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		deleteBtn.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		referenceDropdown.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		subresPopupBtn.triggerAction(addSubWidgetGroup, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		toplevelPopoupBtn.triggerAction(addSubWidgetGroup, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		resourceSelector.triggerAction(subResTable2, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		resourceSelector.triggerAction(subResTable2, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		
		subAsReferenceCheckbox.triggerAction(subReferenceField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		subTypeSelector.triggerAction(subReferenceField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		addSubButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		subresPopupBtn.triggerAction(createPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		toplevelPopoupBtn.triggerAction(createPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		addSubButton.triggerAction(createPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		addSubButton.triggerAction(subResTable2, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		addSubButton.triggerAction(resourceSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
	}
	
	private static boolean isValidResourceName(String nameIn) {
		if (nameIn == null || nameIn.isEmpty()) return false;
		if (!Character.isJavaIdentifierStart(nameIn.charAt(0))) {
			return false;
		}
		for (char c : nameIn.toCharArray()) {
			if (!Character.isJavaIdentifierPart(c)) {
				return false;
			}
		}
		return true;
	}
	
	private class AddSubButton extends Button {
		
		private static final long serialVersionUID = 1L;
		
		public AddSubButton(WidgetPage<?> page, String id, String text) {
			super(page, id, text);
		}
		
		public Resource getParent(OgemaHttpRequest req) {
			return getData(req).getParent();
		}

		public void setParent(Resource parent, OgemaHttpRequest req) {
			getData(req).setParent(parent) ;
		}

		@Override
		public AddSubButtonOptions createNewSession() {
			return new AddSubButtonOptions(this);
		}
		
		@Override
		public AddSubButtonOptions getData(OgemaHttpRequest req) {
			return (AddSubButtonOptions) super.getData(req);
		}
	

		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			Resource res = getParent(req);
//			if (res == null) {
//				alert.showAlert("Error: no parent resource selected", false, req);
//				return;
//			}
			Class<? extends Resource> type = subTypeSelector.getSelectedType(req);
			if (type == null) {
				alert.showAlert("Error: no resource type selected", false, req);
				return;
			}
			String name = subNameField.getValue(req);
			if (!isValidResourceName(name)) {
				alert.showAlert("Error: not a valid resource name: " + name, false, req);
				return;
			}
			Resource referenceTarget = subReferenceField.getReferenceTarget(req);
			boolean setReference = subAsReferenceCheckbox.getCheckboxList(req).get("Reference");
			if (setReference && referenceTarget == null) {
				alert.showAlert("No reference selected", false, req);
				return;
			}
			Resource sub;
			try {
				Resource sub0;
				if (res != null)
					sub0 = res.getSubResource(name);
				else
					sub0 = am.getResourceAccess().getResource(name);
				if (sub0 != null && sub0.exists()) {
					alert.showAlert("Could not create resource " + sub0.getPath() + "; already exists", false, req);
					return;
				}
				if (res == null)
					sub = am.getResourceManagement().createResource(name, type);
				else if (referenceTarget == null) 
					sub = res.getSubResource(name, type).create();
				else 
					sub = res.addDecorator(name, referenceTarget);
				alert.showAlert("Resource " + sub.getPath() + " has been created.", true, req);
				subAsReferenceCheckbox.getCheckboxList(req).put("Reference", true);
				subNameField.setValue("", req);
				subParent.setText("--", req);
			} catch (Exception e) {
				alert.showAlert("Error: could not create resource " + name + ": " +e , false, req);
			}
		}
		
//		
//		@Override
//		public void onGET(OgemaHttpRequest req) {
//			Resource res = resourceSelector.getSelectedResource(req);
//			if (res == null) {
//				disable(req);
//				setWidgetVisibility(false, req);
//			} else {
//				enable(req);
//				setWidgetVisibility(true, req);
//			}
//			
//		}
		
	}
	
	private class AddSubButtonOptions extends ButtonData {
		
		private Resource parent  =null; // != null iff create subresource

		public AddSubButtonOptions(AddSubButton btn) {
			super(btn);
		}

		public Resource getParent() {
			return parent;
		}

		public void setParent(Resource parent) {
			this.parent = parent;
		}
		
	}
	
}
