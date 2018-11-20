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
package org.ogema.apps.roomlink.localisation.mainpage;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class RoomLinkDictionary_en extends RoomLinkDictionary {

	@Override
	public OgemaLocale getLocale() {
		return OgemaLocale.ENGLISH;
	}

	@Override
	public String header() {
		return "Device-room links";
	}

	@Override
	public String unassignedHeader() {
		return "Unassigned";
	}

	@Override
	public String newRoomNameLabel() {
		return "New room";
	}
	
	@Override
	public String newRoomName() {
		return "Enter name";
	}
	
	@Override
	public String newRoomFileLabel() {
		return "Upload image (optional)";
	}
	
	@Override
	public String createRoomButton() {
		return "Create new room";
	}
	
	@Override
	public String alertEnterName() {
		return "Please enter a name";
	}
	
	@Override
	public String roomNameInUse(String name) {
		return "Room name " + name + " already exists.";
	}
	
	@Override
	public String newRoomCreated(String name) {
		return "New room has been created: " + name;
	}
	
	@Override
	public String editRoomButton() {
		return "Edit rooms";
	}
	
	@Override
	public String editRoomNameLabel() {
		return "Edit room";
	}
	
	@Override
	public String editRoomName() {
		return "Enter name";
	}
	
	@Override
	public String editRoomFileLabel() {
		return "Upload new image";
	}
	
	@Override
	public String roomEdited(String name) {
		return "Room edited: " + name;
	}
	
	@Override
	public String roomSelectorLabel() {
		return "Select room";
	}
	
	@Override
	public String editRoomPathLabel() {
		return "OGEMA resource path";
	}

	@Override
	public String abort() {
		return "Abort";
	}

	@Override
	public String deleteRoom() {
		return "Delete room";
	}

	@Override
	public String noRoomSelected() {
		return "No room selected";
	}

	@Override
	public String roomDeleted(String name) {
		return "Room deleted " + name;
	}

	@Override
	public String confirmDeletionPopupTitle() {
		return "Confirm deletion";
	}

	@Override
	public String confirmDeletionCancelBtn() {
		return "Cancel";
	}

	@Override
	public String confirmDeletionConfirmBtn() {
		return "Delete";
	}

	@Override
	public String confirmDeletionMsg(String name) {
		return "Do you really want to delete " + name + "?";
	}
	
	@Override
	public String save() {
		return "Save";
	}

	@Override
	public String registerDeviceTitle() {
		return "Scan for new devices";
	}

	@Override
	public String registerDeviceHeader() {
		return "Start device pairing";
	}
	
	@Override
	public String selectDriverLabel() {
		return "Select a driver";
	}
	
	@Override
	public String pairingModeStartedAlert(String driverId) {
		return "Started pairing mode for driver " + driverId;
	}
	
	@Override
	public String roomTypeLabel() {
		return "Select a room type";
	}

	@Override
	public String noRoomTypeSelected() {
		return "Unspecified";
	}
	
	@Override
	public String pairingModeActive(String driverId) {
		return "Pairing mode active";
	}
}
