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
package de.iwes.widgets.html.selectiontree.samples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.ogema.model.locations.Room;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class RoomTypeOption extends LinkingOption {
	
	private final static List<SelectionItem> items;
	public final static int[] roomTypeKeys = { 0,1,2,3,4,5,6,7,8,10,20,100,101,200,210 };
	
	static {
		final List<SelectionItem> list = new ArrayList<>();
		for (int i : roomTypeKeys) {
			list.add(new RoomTypeItem(i));
		}
		items = Collections.unmodifiableList(list);
	}

	@Override
	public String id() {
		return "room_type";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Select room type";
	}

	@Override
	public LinkingOption[] dependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies) {
		return items;
	}

	private final static class RoomTypeItem implements SelectionItem {
		
		private final int type;
		
		public RoomTypeItem(int type) {
			this.type = type;
		}

		@Override
		public String id() {
			return type + "";
		}

		@Override
		public String label(OgemaLocale locale) {
			return getRoomTypeString(type, locale);
		}
		
	}
	
	
	/**
	 * Returns a English-language String for display in a user interface, that represents the room type. 
	 * 
	 * @param type
	 * @return
	 * 
	 * @see Room#type()
	 * @see #getRoomTypeString(int, OgemaLocale) for a locale-dependent version
	 */
	private static String getRoomTypeString(int type) {
		switch(type) {
		case 0:
			return "outside";
		case 1:
			return "living room";
		case 2:
			return "living kitchen";
		case 3:
			return "kitchen";
		case 4:
			return "bath room";
		case 5:
			return "toilet";
		case 6:
			return "corridor";
		case 7:
			return "staircase area";
		case 8:
			return "store room";
		case 10:
			return "bed room";
		case 20:
			return "garage";
		case 100:
			return "office";
		case 101:
			return "meeting room";
		case 200:
			return "comm. kitchen";
		case 210:
			return "comm. dining";
		default:
			if(type >= 10000) {
				return "custom";
			}
			return "??";
		}
	}
	
	/**
	 * Returns a language-dependent String for display in a user interface, that represents the room type.
	 * If the passed language is not supported, the English version is returned.
	 * <br>
	 * Capitalization of the returned string depends on the locale. For English and French fully lower case letters are used,
	 * German names start with a capital letter. 
	 * 
	 * @param type
	 * @return
	 * 
	 * @see Room#type()
	 */
	private static String getRoomTypeString(int type, OgemaLocale locale) {
		// if locale == English, or not one of the supported languages, use English as default
		if (locale != OgemaLocale.GERMAN && locale != OgemaLocale.FRENCH) 
			return getRoomTypeString(type);
		switch(type) {
		case 0:
			if (locale == OgemaLocale.GERMAN)
				return "Au�en";
			if (locale == OgemaLocale.FRENCH)
				return "ext�rieur";
		case 1:
			if (locale == OgemaLocale.GERMAN)
				return "Wohnzimmer";
			if (locale == OgemaLocale.FRENCH)
				return "salle de s�jour";
		case 2:
			if (locale == OgemaLocale.GERMAN)
				return "Wohnk�che";
			if (locale == OgemaLocale.FRENCH)
				return "cuisine ouverte";
		case 3:
			if (locale == OgemaLocale.GERMAN)
				return "K�che";
			if (locale == OgemaLocale.FRENCH)
				return "cuisine";
		case 4:
			if (locale == OgemaLocale.GERMAN)
				return "Badezimmer";
			if (locale == OgemaLocale.FRENCH)
				return "salle de bains";
		case 5:
			if (locale == OgemaLocale.GERMAN)
				return "Toilette";
			if (locale == OgemaLocale.FRENCH)
				return "toilette";
		case 6:
			if (locale == OgemaLocale.GERMAN)
				return "Gang/Flur";
			if (locale == OgemaLocale.FRENCH)
				return "corridor";
		case 7:
			if (locale == OgemaLocale.GERMAN)
				return "Treppenhaus";
			if (locale == OgemaLocale.FRENCH)
				return "cage d'escalier";
		case 8:
			if (locale == OgemaLocale.GERMAN)
				return "Lagerraum";
			if (locale == OgemaLocale.FRENCH)
				return "depot";
		case 10:
			if (locale == OgemaLocale.GERMAN)
				return "Schlafzimmer";
			if (locale == OgemaLocale.FRENCH)
				return "chambre";
		case 20:
			if (locale == OgemaLocale.GERMAN)
				return "Garage";
			if (locale == OgemaLocale.FRENCH)
				return "garage";
		case 100:
			if (locale == OgemaLocale.GERMAN)
				return "B�ro";
			if (locale == OgemaLocale.FRENCH)
				return "bureau";
		case 101:
			if (locale == OgemaLocale.GERMAN)
				return "Besprechungsraum";
			if (locale == OgemaLocale.FRENCH)
				return "salle de r�union";
		case 200:
			if (locale == OgemaLocale.GERMAN)
				return "Gemeinschaftsk�che";
			if (locale == OgemaLocale.FRENCH)
				return "cuisine collective";
		case 210:
			if (locale == OgemaLocale.GERMAN)
				return "Gemeinschaftsesszimmer";
			if (locale == OgemaLocale.FRENCH)
				return "salle communale";
		default:
			if(type >= 10000) {
				return "custom";
			}
			return "??";
		}
	}

}
