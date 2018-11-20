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

public class RoomLinkDictionary_fr extends RoomLinkDictionary {

	@Override
	public OgemaLocale getLocale() {
		return OgemaLocale.FRENCH;
	}

	@Override
	public String header() {
		return "Associations appareil-chambre";
	}

	@Override
	public String unassignedHeader() {
		return "Vacantes";
	}

	@Override
	public String newRoomNameLabel() {
		return "Nouveau chambre";
	}

	@Override
	public String newRoomName() {
		return "Nouveau chambre";
	}

	@Override
	public String newRoomFileLabel() {
		return "Image (optionnel)";
	}

	@Override
	public String createRoomButton() {
		return "Crée un chambre";
	}

	@Override
	public String alertEnterName() {
		return "Nom manquant";
	}

	@Override
	public String roomNameInUse(String name) {
		return "Nom déjà utilisé "+ name;
	}

	@Override
	public String newRoomCreated(String name) {
		return "Chambre créé: " + name;
	}

	@Override
	public String editRoomButton() {
		return "Adapter les chambres";
	}

	@Override
	public String editRoomNameLabel() {
		return "Adapté chambre";
	}

	@Override
	public String editRoomName() {
		return "Apellation";
	}

	@Override
	public String editRoomFileLabel() {
		return "Adapté image";
	}

	@Override
	public String roomEdited(String name) {
		return "Chambre adapté " + name ;
	}

	@Override
	public String roomSelectorLabel() {
		return "Choisis un chambre";
	}

	@Override
	public String editRoomPathLabel() {
		return "Ressource OGEMA";
	}
	
	@Override
	public String abort() {
		return "Annule";
	}

	@Override
	public String deleteRoom() {
		return "Détruis";
	}

	@Override
	public String noRoomSelected() {
		return "N'aucune chambre choisis";
	}

	@Override
	public String roomDeleted(String name) {
		return "Chambre détruis: " + name;
	}

	@Override
	public String confirmDeletionPopupTitle() {
		return "Confirme";
	}

	@Override
	public String confirmDeletionCancelBtn() {
		return "Annule";
	}

	@Override
	public String confirmDeletionConfirmBtn() {
		return "Détruis";
	}

	@Override
	public String confirmDeletionMsg(String name) {
		return "Est-ce que tu veut détruir " + name + "?";
	}
	
	@Override
	public String save() {
		return "Enregistrer";
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
		return "Choisis le type de chambre";
	}

	@Override
	public String noRoomTypeSelected() {
		return "Pas choisi";
	}
	
	@Override
	public String pairingModeActive(String driverId) {
		return "Pairing mode active";
	}
}
