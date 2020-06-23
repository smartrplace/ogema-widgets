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
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.ogema.apps.roomlink.localisation.mainpage.RoomLinkDictionary;
import org.ogema.apps.roomlink.utils.RoomLinkUtils;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.model.locations.Room;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.util.linkingresource.RoomHelper;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.fileupload.FileUpload;
import de.iwes.widgets.html.fileupload.FileUploadListener;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.template.DisplayTemplate;

public class NewRoomPopupBuilder {
	
	public static Button addWidgets(final WidgetPage<RoomLinkDictionary> page, Popup newRoomPopup, final Alert alert, final ApplicationManager am) {
		PageSnippet bodySnippet  = new PageSnippet(page, "bodySnippet", true);
		StaticTable tab = new StaticTable(3, 2, new int[]{5,7});
//		tab.setContent(0, 0, text).setContent(0, 1, nameField).setContent(0, 2, confirmBtn);
		bodySnippet.append(tab, null);
		Label newRoomNameLabel = new Label(page,"newRoomNameLabel") {

			private static final long serialVersionUID = 1L;

			public void onGET(de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest req) {
				setText(page.getDictionary(req.getLocaleString()).newRoomNameLabel(), req);
			}
		};
		tab.setContent(0, 0, newRoomNameLabel);
		
		final TextField newRoomName = new TextField(page, "newRoomName") {

			private static final long serialVersionUID = 1L;

			public void onGET(de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest req) {
				setPlaceholder(page.getDictionary(req.getLocaleString()).newRoomName(), req);
			}
		};
		tab.setContent(0, 1, newRoomName);
		
		Label newRoomTypeLabel = new Label(page, "newRoomTypeLabel", "Type") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				page.getDictionary(req).roomTypeLabel();
			}
			
		};
		
		int[] ks = RoomHelper.getRoomTypeKeys();
		final List<Integer> keys = new ArrayList<>();
		for (int k: ks)
			keys.add(k);
		
		final TemplateDropdown<Integer> newRoomTypeSelector = new TemplateDropdown<Integer>(page, "newRoomTypeSelector") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				update(Collections.<Integer> emptyList(), req); // clear list, since the language may have changed... :/
				setAddEmptyOption(true, page.getDictionary(req).noRoomTypeSelected(), req);
				update(keys, req);
			}
			
		};
		newRoomTypeSelector.setTemplate(dropdownTemplate);
		tab.setContent(1, 0, newRoomTypeLabel).setContent(1, 1, newRoomTypeSelector);
		
		Label newRoomFileLabel = new Label(page,"newRoomFileLabel") {

			private static final long serialVersionUID = 1L;

			public void onGET(de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest req) {
				setText(page.getDictionary(req.getLocaleString()).newRoomFileLabel(), req);
			}
		};
		tab.setContent(2, 0, newRoomFileLabel);
		
		final FileUpload newRoomFile = new FileUpload(page, "newRoomFile", am);
		tab.setContent(2, 1, newRoomFile);
		
		newRoomPopup.setBody(bodySnippet, null);		
		
		List<OgemaWidget> widgets = new ArrayList<OgemaWidget>();
		widgets.add(newRoomNameLabel);widgets.add(newRoomName);widgets.add(newRoomFileLabel);widgets.add(newRoomFile);
		WidgetGroup createRoomGroup = page.registerWidgetGroup("createRoomGroup",widgets);
		newRoomPopup.triggerAction(createRoomGroup, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		
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
		
		Button createRoomButton = new Button(page, "createRoomButton") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(page.getDictionary(req.getLocaleString()).createRoomButton(), req);
			}
			
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				newRoomFile.removeListener(listener, req);
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				String name = newRoomName.getValue(req);
				RoomLinkDictionary dict = page.getDictionary(req.getLocaleString());
				alert.setWidgetVisibility(true, req);
				alert.autoDismiss(6000, req);
				if (name == null || name.isEmpty()) {
					alert.setText(dict.alertEnterName(), req);
					alert.setStyle(AlertData.BOOTSTRAP_DANGER, req);
					return;
				}
				if (RoomLinkUtils.isNameInUse(name, am.getResourceAccess())) {
					alert.setText(dict.roomNameInUse(name), req);
					alert.setStyle(AlertData.BOOTSTRAP_DANGER, req);
					return;
				}
				Integer type = newRoomTypeSelector.getSelectedItem(req);
				
				Room room = createRoom(name, type, am);
				newRoomFile.registerListener(listener, room, req); // -> as soon as file upload is complete, the file subresource will be set
				
				alert.setText(dict.newRoomCreated(name), req);
				alert.setStyle(AlertData.BOOTSTRAP_SUCCESS, req);
				try {
					Thread.sleep(1000);	// hope that FileUpload will be complete by then, so new image is shown immediately
				} catch (InterruptedException e) {}
			}
		};
		createRoomButton.triggerAction(createRoomGroup, TriggeringAction.PRE_POST_REQUEST, TriggeredAction.POST_REQUEST);
		createRoomButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		newRoomPopup.setFooter(createRoomButton, null);
		return createRoomButton;
	}
	
	public static DisplayTemplate<Integer> dropdownTemplate = new DisplayTemplate<Integer>() {

		@Override
		public String getId(Integer object) {
			return String.valueOf(object.intValue());
		}

		@Override
		public String getLabel(Integer object, OgemaLocale locale) {
			if(Boolean.getBoolean("org.ogema.messaging.basic.services.config.fixconfigenglish"))
				locale = OgemaLocale.ENGLISH;
			return RoomHelper.getRoomTypeString(object, locale);
		}
	};
	
	private static Room createRoom(String name, Integer type, ApplicationManager am) {
		String path = ResourceUtils.getValidResourceName(name);
		ResourceAccess ra = am.getResourceAccess();
		Resource res = ra.getResource(path);
		while (res != null) {
			path += "_1";
			res = ra.getResource(path);
		}
		Room room = am.getResourceManagement().createResource(path, Room.class);
		room.name().create();
		room.name().setValue(name);
		if (type != null) 
			room.type().<IntegerResource> create().setValue(type.intValue());
		room.activate(true);		
		return room;
	}
	
	
}
