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
package de.iwes.util.linkingresource;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.resourcemanager.pattern.PatternChangeListener;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.locations.Building;
import org.ogema.model.locations.BuildingPropertyUnit;
import org.ogema.model.locations.Location;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.util.resource.ResourceHelper;
import de.iwes.util.resourcelist.SensorResourceListHelper;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/** Collection of functions to support working with room resources. For access to
 * temperature/humidity sensors see also {@link SensorResourceListHelper}
 *
 */
public class RoomHelper {
	public static final String ROOM_TOPLELVEL_NAME = "rooms";
	
	public static final int LIVING = 1;
	public static final int TOILET = 5;
	public static final int CORRIDOR = 6;
	public static final int STORE_ROOM = 8;
	public static final int SLEEPING = 10;
	public static final int GARAGE = 20;
	public static final int TECHNICAL_ROOM = 30;
	public static final int OFFICE = 100;
	public static final int MEETING = 101;
	public static final int LARGE_OFFICE = 102;
	public static final int KITCHEN_COMM = 200;
	public static final int OUTSIDE = 500;
	public static final int OTHER = 900;
	public static final int CUSTOM = 10000;

	/** Find room in resource itself or in super resource
	 * @deprecated Use {@link ResourceUtils#getDeviceLocationRoom(PhysicalElement)} instead.
	 * */
	public static Room getResourceLocationRoom(Resource res) {
		Resource curRes = res.getLocationResource();
		PhysicalElement curPhys = null;
		if (curRes instanceof PhysicalElement) {
			curPhys = (PhysicalElement) curRes;
		}
		while(curRes != null) {
			if ((curPhys != null) && curPhys.location().room().exists()) {
				return curPhys.location().room();
			} 
			curRes = curRes.getParent();
			if (curRes instanceof PhysicalElement) {
				curPhys = (PhysicalElement) curRes;
			} else {
				curPhys = null;
			}
		}
		return null;
	}
	
	/**Method intended to be used in {@link ResourcePattern}s in order to get an accept() callback
	 * 		when room information is available in the resource itself or in a super resource. For
	 * 		each level above the resource that shall be considered one entry to the pattern needs
	 * 		to be added. A typical use inside the pattern would be:
	 * 	"@ChangeListener(structureListener=true)
	 *  @Existence(required=CreateMode.OPTIONAL)
	 *  public Room room0 = RoomHelper.ResourceLocationRoom4Pattern(model, 0);
	 *  @ChangeListener(structureListener=true)
	 *  @Existence(required=CreateMode.OPTIONAL)
	 *  public Room room1 = RoomHelper.ResourceLocationRoom4Pattern(model, 1);
	 *  ..."
	 *  This also requires to register a {@link PatternChangeListener}
	 * @param res typically the model of the pattern
	 * @param levelUp
	 * @return
	 */
	public static Room ResourceLocationRoom4Pattern(Resource res, int levelUp) {
		Resource curRes = res.getLocationResource();
		PhysicalElement curPhys = null;
		if (curRes instanceof PhysicalElement) {
			curPhys = (PhysicalElement) curRes;
		}
		int curLevel = 0;
		while(curRes != null) {
			if(curLevel == levelUp) {
				if ((curPhys != null)) {
					return curPhys.location().room();
				} else return null;
			}
			curRes = curRes.getParent();
			if (curRes instanceof PhysicalElement) {
				curPhys = (PhysicalElement) curRes;
			} else {
				curPhys = null;
			}
			curLevel++;
		}
		return null;
	}
	
	/** Get a single {@link BuildingPropertyUnit} or {@link Building} of the framework.
	 * 
	 * @param resAcc reference to application's resource access
	 * @param createIfNotExisting create defaultBuildingPropertyUnit if no result found
	 * @return  The property unit can be determined
	 * if a single top-level BuildingPropertyUnit exists. If this is not the case a single
	 * top-level {@link Building} is searched. If this is also not found also single non-toplevel
	 * resources are accepted for return. If no fitting resource is found the return value is null or
	 * a new resource is created if createIfNotExisting is true.
	 */
	public static PhysicalElement getSystemBuildingOrPropertyUnit(ApplicationManager appMan, boolean createIfNotExisting) {
		ResourceAccess resAcc = appMan.getResourceAccess();
		List<BuildingPropertyUnit> propList = resAcc.getToplevelResources(BuildingPropertyUnit.class);
		if(propList.size() == 1) {
			return propList.get(0);
		}
		List<Building> buildList = resAcc.getToplevelResources(Building.class);
		if(buildList.size() == 1) {
			return buildList.get(0);
		}
		if(propList.isEmpty()) {
			propList = resAcc.getResources(BuildingPropertyUnit.class);
			if(propList.size() == 1) {
				return propList.get(0);
			}
		}
		if(buildList.isEmpty()) {
			buildList = resAcc.getResources(Building.class);
			if(buildList.size() == 1) {
				return buildList.get(0);
			}
		}
		if(createIfNotExisting) {
			BuildingPropertyUnit newUnit = appMan.getResourceManagement().createResource(
					"Building", BuildingPropertyUnit.class);
			newUnit.name().create();
			newUnit.name().setValue("defaultBuildingPropertyUnit");
			newUnit.activate(true);
			return newUnit;
		}
		return null;
	}
	
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
	
	/** Get integer value for room type according to {@link Room#type()} 
	 * @param typeString String as returned by {@link #roomType(Room)}
	 * @param spacesRemoved if true the String returned by {@link #roomType(Room)} is used without spaces
	 * @return room type integer value
	 */
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

	/** Return room type integer as human readable short string*/
	public static String roomType(Room r) {
		if(!r.type().exists()) {
			return "";
		} else {
			return getRoomTypeString(r.type().getValue());
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
		case 6:
			return "hall or corridor";
		case 7:
			return "staircase area";
		case 8:
			return "store room";
		case 10:
			return "bed room";
		case 20:
			return "garage";
		case 30:
			return "technical/server room";
		case 100:
			return "office";
		case 101:
			return "meeting room";
		case 102:
			return "open-plan office";
		case 200:
			return "kitchen/cafeteria";
		case 210:
			return "dining area";
		case 500:
			return "outside";
		case 900:
			return "other";
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
		case 30:
			if (locale == OgemaLocale.GERMAN)
				return "Technik-/Serverraum";
			if (locale == OgemaLocale.FRENCH)
				return "sale de technologie";
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
		case 102:
			if (locale == OgemaLocale.GERMAN)
				return "Großraumbüro";
			if (locale == OgemaLocale.FRENCH)
				return "bureau paysager";
		case 200:
			if (locale == OgemaLocale.GERMAN)
				return "Küche/Cafeteria";
			if (locale == OgemaLocale.FRENCH)
				return "cuisine collective";
		case 210:
			if (locale == OgemaLocale.GERMAN)
				return "Speisesaal";
			if (locale == OgemaLocale.FRENCH)
				return "salle communale";
		case 900:
			if (locale == OgemaLocale.GERMAN)
				return "Sonstige";
			if (locale == OgemaLocale.FRENCH)
				return "Autres";
		default:
			if(type >= 10000) {
				return "Spezial";
			}
			return "??";
		}
	}
	
	/**
	 * Room type keys
	 * @return
	 * 
	 * @see Room#type()
	 */
	public static int[] getRoomTypeKeys() {
		return new int[]{ 0,1,2,3,4,5,6,7,8,10,20,100,101,200,210 };
	}

	@Deprecated 
	/** @deprecated Use {@link ResourceUtils#getDevicesFromRoom()} instead.
	 * */
	public static <T extends PhysicalElement> List<T> getDevicesInRoom(Room room, Class<T> type, ResourceAccess ra) {
		List<T> result = new ArrayList<T>();
		List<T> all = ra.getResources(type);
		for(T dev: all) {
			if(dev.location().room().equalsLocation(room))
				result.add(dev);
		}
		return result ;
	}
	
	public static List<PhysicalElement> getAllDevicesInRoom(Room room, ResourceAccess ra) {
		List<PhysicalElement> result = new ArrayList<>();
		List<Location> locs = room.getReferencingResources(Location.class);
		for(Location loc: locs) {
			Resource devraw = loc.getParent();
			if(!(devraw instanceof PhysicalElement))
				continue;
			PhysicalElement dev = (PhysicalElement) devraw;
			result.add(dev);
		}
		return result ;
	}

	@SuppressWarnings("unchecked")
	public static ResourceList<Room> getRoomToplevelResource(ApplicationManager appMan) {
		ResourceList<Room> result = ResourceHelper.getTopLevelResource(ROOM_TOPLELVEL_NAME, ResourceList.class, appMan.getResourceAccess());
		if(result != null && result.isActive())
			return result;
		result = appMan.getResourceManagement().createResource(ROOM_TOPLELVEL_NAME, ResourceList.class);
		result.setElementType(Room.class);
		result.activate(false);
		return result;
	}
}
