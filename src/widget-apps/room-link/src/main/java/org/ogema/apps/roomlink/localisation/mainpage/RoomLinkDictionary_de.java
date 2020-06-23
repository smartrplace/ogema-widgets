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

public class RoomLinkDictionary_de extends RoomLinkDictionary {

	@Override
	public OgemaLocale getLocale() {
		//FIXME
		return OgemaLocale.CHINESE;
	}

	@Override
	public String header() {
		return "Geräte-Raumzuordnung";
	}

	@Override
	public String unassignedHeader() {
		return "Nicht zugeordnet";
	}

	@Override
	public String newRoomNameLabel() {
		return "Neuer Raum";
	}

	@Override
	public String newRoomName() {
		return "Raumbezeichnung";
	}

	@Override
	public String newRoomFileLabel() {
		return "Bild des Raums (optional)";
	}

	@Override
	public String createRoomButton() {
		return "Raum anlegen";
	}

	@Override
	public String alertEnterName() {
		return "Bitte gib einen Namen an";
	}

	@Override
	public String roomNameInUse(String name) {
		return "Name wird bereits verwendet";
	}

	@Override
	public String newRoomCreated(String name) {
		return "Neuer Raum wurde angelegt: " + name;
	}

	@Override
	public String editRoomButton() {
		return "Räume bearbeiten";
	}

	@Override
	public String editRoomNameLabel() {
		return "Raum bearbeiten";
	}

	@Override
	public String editRoomName() {
		return "Bezeichnung";
	}

	@Override
	public String editRoomFileLabel() {
		return "Neues Bild hochladen";
	}

	@Override
	public String roomEdited(String name) {
		return "Raumdaten wurden gespeichert: " + name;
	}

	@Override
	public String roomSelectorLabel() {
		return "Wähle einen Raum aus";
	}

	@Override
	public String editRoomPathLabel() {
		return "OEGMA Ressourcenpfad";
	}
	
	@Override
	public String abort() {
		return "Abbrechen";
	}

	@Override
	public String deleteRoom() {
		return "Raum löschen";
	}

	@Override
	public String noRoomSelected() {
		return "Keinen Raum ausgewählt";
	}

	@Override
	public String roomDeleted(String name) {
		return "Raum gelöscht " + name;
	}

	@Override
	public String confirmDeletionPopupTitle() {
		return "Löschen bestätigen";
	}

	@Override
	public String confirmDeletionCancelBtn() {
		return "Abbrechen";
	}

	@Override
	public String confirmDeletionConfirmBtn() {
		return "Löschen";
	}

	@Override
	public String confirmDeletionMsg(String name) {
		return "Willst Du " + name + " wirklich löschen?";
	}

	@Override
	public String save() {
		return "Speichern";
	}

	@Override
	public String registerDeviceTitle() {
		return "Geräte einlernen";
	}

	@Override
	public String registerDeviceHeader() {
		return "Einlernvorgang starten";
	}
	
	@Override
	public String selectDriverLabel() {
		return "Treiber wählen";
	}
	
	@Override
	public String pairingModeStartedAlert(String driverId) {
		return "Der Treiber " + driverId + " wurde in den Einlernmodus versetzt";
	}

	@Override
	public String roomTypeLabel() {
		return "Raumtyp auswählen";
	}

	@Override
	public String noRoomTypeSelected() {
		return "Kein Typ ausgewählt";
	}

	@Override
	public String pairingModeActive(String driverId) {
		return "Anlernmodus Modus aktiv";
	}
}
