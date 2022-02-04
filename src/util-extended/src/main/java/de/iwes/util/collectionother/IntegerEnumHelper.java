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
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.util.collectionother;

import java.util.ArrayList;
import java.util.List;

import org.ogema.model.locations.Room;

import de.iwes.util.linkingresource.RoomHelper;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
/** @deprecated use {@link RoomHelper instead}*/
public class IntegerEnumHelper {
	
	/** @deprecated use {@link RoomHelper#getRoomTypeStringsToChoose()} instead*/
	@Deprecated
	public static List<String> getRoomTypeStringsToChoose() {
		List<String> result = new ArrayList<String>();
		//TODO: not very efficient, but ok for now; should be stored somewhere after initial call
		for(int type=0; type<299; type++) {
			String s = getRoomTypeString(type);
			if(!s.equals("??")) {
				result.add(s);
			}
		}
		return result;
	}
	
	/** @deprecated use {@link RoomHelper#getRoomTypeInt(String, boolean)} instead*/
	@Deprecated
	public static int getRoomTypeInt(String typeString, boolean spacesRemoved) {
		for(int type=0; type<299; type++) {
			String s = getRoomTypeString(type);
			if(spacesRemoved) 
				s = s.replaceAll("\\s","");
			if(s.equals(typeString)) {
				return type;
			}
		}
		return -1;
	}

	/** Return room type integer as human readable short string
	 *  @deprecated use {@link RoomHelper#roomType(Room) instead}*/
	@Deprecated
	public static String roomType(Room r) {
		if(!r.type().exists()) {
			return "";
		} else {
			return getRoomTypeString(r.type().getValue());
		}
	}

	/**
	 * @deprecated use {@link RoomHelper#getRoomTypeString(int)} instead
	 */
	public static String getRoomTypeString(int type) {
		switch(type) {
		case 0:
			return "undefined";
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
		case 500:
			return "outside";
		default:
			if(type >= 10000) {
				return "custom";
			}
			return "??";
		}
	}
	
	/**
	 *  @deprecated use {@link RoomHelper#getRoomTypeString(int, OgemaLocale)} instead
	 */
	public static String getRoomTypeString(int type, OgemaLocale locale) {
		// if locale == English, or not one of the supported languages, use English as default
		if (locale != OgemaLocale.GERMAN && locale != OgemaLocale.FRENCH) 
			return getRoomTypeString(type);
		switch(type) {
		case 0:
			if (locale == OgemaLocale.GERMAN)
				return "Außen";
			if (locale == OgemaLocale.FRENCH)
				return "extérieur";
		case 1:
			if (locale == OgemaLocale.GERMAN)
				return "Wohnzimmer";
			if (locale == OgemaLocale.FRENCH)
				return "salle de séjour";
		case 2:
			if (locale == OgemaLocale.GERMAN)
				return "Wohnküche";
			if (locale == OgemaLocale.FRENCH)
				return "cuisine ouverte";
		case 3:
			if (locale == OgemaLocale.GERMAN)
				return "Küche";
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
				return "Büro";
			if (locale == OgemaLocale.FRENCH)
				return "bureau";
		case 101:
			if (locale == OgemaLocale.GERMAN)
				return "Besprechungsraum";
			if (locale == OgemaLocale.FRENCH)
				return "salle de réunion";
		case 200:
			if (locale == OgemaLocale.GERMAN)
				return "Gemeinschaftsküche";
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
	
	/**
	 * @deprecated use {@link RoomHelper#getRoomTypeKeys()} instead
	 */
	public static int[] getRoomTypeKeys() {
		return new int[]{ 0,1,2,3,4,5,10,20,100,101,200,210 };
	}
	
}
