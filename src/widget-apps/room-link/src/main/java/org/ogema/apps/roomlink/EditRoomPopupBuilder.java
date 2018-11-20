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
package org.ogema.apps.roomlink;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.ogema.apps.roomlink.localisation.mainpage.RoomLinkDictionary;
import org.ogema.apps.roomlink.utils.RoomLinkUtils;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.locations.Room;

import de.iwes.util.linkingresource.RoomHelper;
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
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirm;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirmData;
import de.iwes.widgets.html.dragdropassign.DragDropAssign;
import de.iwes.widgets.html.fileupload.FileUpload;
import de.iwes.widgets.html.fileupload.FileUploadListener;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.popup.Popup;

public class EditRoomPopupBuilder {
	
	static final String emptyValue = DropdownData.EMPTY_OPT_ID;
	
	static Dropdown addWidgets(final WidgetPage<RoomLinkDictionary> page, Popup editRoomPopup, final Alert alert, DragDropAssign dragNdrop, final ApplicationManager am) {
		PageSnippet editSnippet  = new PageSnippet(page, "editSnippet", true);
		StaticTable tab = new StaticTable(5, 2, new int[]{5,7});
//		tab.setContent(0, 0, text).setContent(0, 1, nameField).setContent(0, 2, confirmBtn);
		editSnippet.append(tab, null);
		
		Label roomSelectorLabel = new Label(page, "roomSelectorLabel") {

			private static final long serialVersionUID = 1L;

			public void onGET(de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest req) {
				setText(page.getDictionary(req.getLocaleString()).roomSelectorLabel(), req);
			}
		};
		final DropdownOption emptyOpt = new DropdownOption(emptyValue, "", true);

		final Dropdown roomSelector = new Dropdown(page, "roomSelector") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				List<Room> rooms = am.getResourceAccess().getResources(Room.class);
				Set<DropdownOption> options = new HashSet<DropdownOption>();
				options.add(emptyOpt);
				for (Room room: rooms) {
					String name = room.getLocation();
					if (room.name().isActive()) {
						name = room.name().getValue();
					}
					DropdownOption option = new DropdownOption(room.getLocation(), name, false);
					options.add(option);
				}
				setOptions(options, req);
			}
			
		};
		tab.setContent(0, 0, roomSelectorLabel);
		tab.setContent(0, 1, roomSelector);
		
		Label editRoomNameLabel = new Label(page,"editRoomNameLabel") {

			private static final long serialVersionUID = 1L;

			public void onGET(de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest req) {
				setText(page.getDictionary(req.getLocaleString()).editRoomNameLabel(), req);
			}
		};
		tab.setContent(1, 0, editRoomNameLabel);
		
		final TextField editRoomName = new TextField(page, "editRoomName") {

			private static final long serialVersionUID = 1L;

			public void onGET(de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest req) {
				setPlaceholder(page.getDictionary(req.getLocaleString()).editRoomName(), req);
				DropdownOption opt  = roomSelector.getSelected(req);
				if (opt == null || opt.equals(emptyOpt)) {
					setValue("", req);
				}
				else {
					String name = "";
					Room room = am.getResourceAccess().getResource(opt.id());
					if (room.name().isActive()) {
						name = room.name().getValue();
					}
					setValue(name, req);
				}
			}
		};
		tab.setContent(1, 1, editRoomName);
		
		int[] ks = RoomHelper.getRoomTypeKeys();
		final List<Integer> keys = new ArrayList<>();
		for (int k: ks)
			keys.add(k);		
		Label editRoomTypeLabel = new Label(page, "editRoomTypeLabel", "Type") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				page.getDictionary(req).roomTypeLabel();
			}
			
		};
		
		final TemplateDropdown<Integer> editRoomTypeSelector = new TemplateDropdown<Integer>(page, "editRoomTypeSelector") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				update(Collections.<Integer> emptyList(), req); // clear list, since the language may have changed... :/
				setAddEmptyOption(true, page.getDictionary(req).noRoomTypeSelected(), req);
				update(keys, req);
				String path = roomSelector.getSelectedValue(req);
				if (path == null || path.equals(DropdownData.EMPTY_OPT_ID)) 
					selectSingleOption(DropdownData.EMPTY_OPT_ID, req);
				else {
					Room res = am.getResourceAccess().getResource(path);
					if (res == null || !res.type().isActive()) {
						selectSingleOption(DropdownData.EMPTY_OPT_ID, req);
					}
					else {
						selectSingleOption(res.type().getValue() + "", req);
					}
				}
			}
			
		};
		editRoomTypeSelector.setTemplate(NewRoomPopupBuilder.dropdownTemplate);
		roomSelector.triggerAction(editRoomTypeSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		tab.setContent(2, 0, editRoomTypeLabel).setContent(2, 1, editRoomTypeSelector);
		
		Label editRoomPathLabel = new Label(page,"editRoomPathLabel") {

			private static final long serialVersionUID = 1L;

			public void onGET(de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest req) {
				setText(page.getDictionary(req.getLocaleString()).editRoomPathLabel(), req);
			}
		};
		final Label editRoomPath = new Label(page,"editRoomPath") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				DropdownOption opt  = roomSelector.getSelected(req);
				if (opt == null || opt.equals(emptyOpt)) {
					setText("", req);
				}
				else {
					setText(opt.id(), req);
				}
			}
		};
		tab.setContent(3, 0, editRoomPathLabel);
		tab.setContent(3, 1, editRoomPath);

		
		Label editRoomFileLabel = new Label(page,"editRoomFileLabel") {

			private static final long serialVersionUID = 1L;

			public void onGET(de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest req) {
				setText(page.getDictionary(req.getLocaleString()).editRoomFileLabel(), req);
			}
		};
		tab.setContent(4, 0, editRoomFileLabel);
		
		final FileUpload editRoomFile = new FileUpload(page, "editRoomFile", am);
		tab.setContent(4, 1, editRoomFile);
		
		editRoomPopup.setBody(editSnippet, null);		
		
		List<OgemaWidget> widgets = new ArrayList<OgemaWidget>();
		widgets.add(editRoomNameLabel);widgets.add(editRoomName);widgets.add(editRoomFileLabel);widgets.add(editRoomFile);
		widgets.add(editRoomPathLabel);widgets.add(editRoomPath);
		WidgetGroup editRoomGroup = page.registerWidgetGroup("editRoomGroup",widgets);
		editRoomPopup.triggerAction(editRoomGroup, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		
		final FileUploadListener<Room> listener = new FileUploadListener<Room>() {

			@Override
			public void fileUploaded(FileItem fileItem, Room context, OgemaHttpRequest req) {
				File out = am.getDataFile("images/" + fileItem.getName());
				try {
					RoomLinkUtils.writeFile(fileItem.getInputStream(), out);
				} catch (IOException e) {
					am.getLogger().error("Error copying file",e);
					return;
				}
				
				StringResource filename = context.getSubResource("imageFile", StringResource.class).create();
				filename.setValue(fileItem.getName()); // TODO ensure name only contains the file name, not the whole path
				filename.activate(false);
			}
			
		};
		
		Button editRoomButton = new Button(page, "editRoomButton") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(page.getDictionary(req.getLocaleString()).save(), req);
			}
			
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				editRoomFile.removeListener(listener, req);
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				if (roomSelector.getSelected(req).equals(emptyOpt)) {
					return;
				}
				String name = editRoomName.getValue(req);
				RoomLinkDictionary dict = page.getDictionary(req.getLocaleString());
				alert.setWidgetVisibility(true, req);
				alert.autoDismiss(6000, req);
				if (name == null || name.isEmpty()) {
					alert.setText(dict.alertEnterName(), req);
					alert.setStyle(AlertData.BOOTSTRAP_DANGER, req);
					return;
				}
				String path = editRoomPath.getText(req);
				Room room = am.getResourceAccess().getResource(path);
				if (!nameUnchanged(name, room) && RoomLinkUtils.isNameInUse(name, am.getResourceAccess())) {
					alert.setText(dict.roomNameInUse(name), req);
					alert.setStyle(AlertData.BOOTSTRAP_DANGER, req);
					return;
				}
				setName(name, room);  
				int k=-1;
				try {
					k = Integer.parseInt(editRoomTypeSelector.getSelectedValue(req));
				} catch (Exception e) { /* ignore */}
				if (k>=0) {
					room.type().<IntegerResource> create().setValue(k);
					room.type().activate(false);
				}
				editRoomFile.registerListener(listener, room, req); // -> as soon as file upload is complete, the file subresource will be set
				
				alert.setText(dict.newRoomCreated(name), req);
				alert.setStyle(AlertData.BOOTSTRAP_SUCCESS, req);
				try {
					Thread.sleep(1000);	// hope that FileUpload will be complete by then, so new image is shown immediately
				} catch (InterruptedException e) {}
			}
		};
		editRoomButton.triggerAction(editRoomGroup, TriggeringAction.PRE_POST_REQUEST, TriggeredAction.POST_REQUEST);
		editRoomButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		Button abortButton = new Button(page, "editRoomAbort") {

			private static final long serialVersionUID = 1L;

			public void onGET(OgemaHttpRequest req) {
				setText(page.getDictionary(req.getLocaleString()).abort(), req);
			}
		};
		abortButton.triggerAction(editRoomPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		
		ButtonConfirm deleteButton = new ButtonConfirm(page, "deleteRoomButton") {

			private static final long serialVersionUID = 1L;

			public void onGET(OgemaHttpRequest req) {
				RoomLinkDictionary dict = page.getDictionary(req.getLocaleString());
				DropdownOption opt = roomSelector.getSelected(req);
				if (opt == null || opt.equals(emptyOpt)) {  // TODO disable delete button
					setText(dict.deleteRoom(), req);
					setConfirmPopupTitle(dict.noRoomSelected(), req);
					setCancelBtnMsg("", req);
					setConfirmBtnMsg("", req);
					setConfirmMsg("", req);
				}
				else {
					String name = opt.label(req.getLocale());
					setText(dict.deleteRoom(), req);
					setConfirmPopupTitle(dict.confirmDeletionPopupTitle(), req);
					setCancelBtnMsg(dict.confirmDeletionCancelBtn(), req);
					setConfirmBtnMsg(dict.confirmDeletionConfirmBtn(), req);
					setConfirmMsg(dict.confirmDeletionMsg(name), req);
				}
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				alert.setWidgetVisibility(true, req);
				alert.autoDismiss(6000, req);
				RoomLinkDictionary dict = page.getDictionary(req.getLocaleString());
				DropdownOption opt = roomSelector.getSelected(req);
				if (opt == null || opt.equals(emptyOpt)) {
					alert.setText(dict.noRoomSelected(), req);
					alert.setStyle(AlertData.BOOTSTRAP_DANGER, req);
					return;
				}
				String loc = opt.id();
				Room room = am.getResourceAccess().getResource(loc);
				room.delete();
				roomSelector.selectSingleOption(emptyValue, req);
				alert.setStyle(AlertData.BOOTSTRAP_SUCCESS, req);
				alert.setText(dict.roomDeleted(opt.label(req.getLocale())), req);
			}
		};
		Set<WidgetStyle<?>> deleteStyles = new HashSet<WidgetStyle<?>>();
		deleteStyles.add(ButtonConfirmData.CANCEL_LIGHT_BLUE);
		deleteStyles.add(ButtonData.BOOTSTRAP_RED);
		deleteStyles.add(ButtonConfirmData.CONFIRM_RED);
		deleteButton.setDefaultStyles(deleteStyles);
		editRoomButton.addDefaultStyle(ButtonData.BOOTSTRAP_BLUE);
		
		deleteButton.triggerAction(dragNdrop, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		deleteButton.triggerAction(editRoomPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		deleteButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		roomSelector.triggerAction(deleteButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		editRoomButton.triggerAction(dragNdrop, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editRoomButton.triggerAction(editRoomPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		PageSnippet footer = new PageSnippet(page, "editRoomFooter", true);
		StaticTable footerTable = new StaticTable(1, 3);
		footerTable.setContent(0, 0, deleteButton).setContent(0, 1, abortButton).setContent(0, 2, editRoomButton);
		footer.append(footerTable, null);
		editRoomPopup.setFooter(footer, null);
		
		roomSelector.triggerAction(editRoomGroup, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		return roomSelector;
	}
	
	private static boolean nameUnchanged(String name, Room room) {
		if (!room.name().isActive()) return false;
		String oldName = room.name().getValue();
		return name.equals(oldName);
	}
	
	private static void setName(String name, Room room) {
		room.name().create();
		room.name().setValue(name);
		room.name().activate(false);
	}
	
}
