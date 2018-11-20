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

import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;

public abstract class RoomLinkDictionary implements LocaleDictionary {

	public abstract String header();
	public abstract String unassignedHeader();
	
	public abstract String newRoomNameLabel();
	public abstract String newRoomName();
	public abstract String newRoomFileLabel();
	public abstract String createRoomButton();
	public abstract String alertEnterName();
	public abstract String roomNameInUse(String name);
	public abstract String newRoomCreated(String name);
	public abstract String roomTypeLabel();
	public abstract String noRoomTypeSelected();
	public abstract String editRoomButton();
	public abstract String editRoomNameLabel();
	public abstract String editRoomName();
	public abstract String editRoomFileLabel();
	public abstract String roomEdited(String name);	
	public abstract String roomSelectorLabel();	
	public abstract String editRoomPathLabel();
	
	public abstract String abort();
	public abstract String save();
	public abstract String deleteRoom();
	public abstract String noRoomSelected();
	public abstract String roomDeleted(String name);
	
	public abstract String confirmDeletionPopupTitle();
	public abstract String confirmDeletionCancelBtn();
	public abstract String confirmDeletionConfirmBtn();
	public abstract String confirmDeletionMsg(String name);
	
	public abstract String registerDeviceTitle();
	public abstract String registerDeviceHeader();
	public abstract String selectDriverLabel();
	public abstract String pairingModeStartedAlert(String driverId);
	public abstract String pairingModeActive(String driverId);
	
}
