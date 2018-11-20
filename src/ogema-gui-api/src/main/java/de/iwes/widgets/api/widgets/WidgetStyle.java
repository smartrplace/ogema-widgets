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
package de.iwes.widgets.api.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Specify the appearance of a widget in terms of CSS classes.<br>
 * Internally, this keeps a map of ids of HTML-elements, and the 
 * corresponding CSS classes. Instead of ids, it is also possible 
 * to identify HTML-subelements based on their class.
 */
public class WidgetStyle<T extends OgemaWidget> {
	
	// keys: ids of HTML elements (or classes, if idClassToggle == false), values: class names
	private final Map<String,List<String>> clazzMap;
	/*
	 *  0 : identify HTML-subelements based on ID<br>
	 *  1 : identify HTML-subelements based on class<br>
	 *  2 : identify by tag type (not yet implemented)
	 */
	private final int idClassTagToggle;
	
	/************ Constructors  ***********/
	
	/**
	 *  default constructor: pass one id to identify an HTML-subelement of the widget, and a list of classes to attach to it
	 */
	@Deprecated
	public WidgetStyle(String id,List<String> clazz) {
		this(id,clazz,0);
	}
	
	/**
	 * Use this constructor to attach classes to different HTML-subelements of the widgets, identified by the keys of clazzMap
	 */
	@Deprecated
	public WidgetStyle(Map<String,List<String>> clazzMap) {
		this(clazzMap,0);
	}
	
	/**
	 * @param id
	 * 		identifier for the DOM element(s) to which the new classes shall be applied
	 * @param clazz
	 * 		CSS class names to be applied to the DOM element
	 * @param selectorId: 
	 * <ul>
	 *  <li>0 : identify HTML-subelements based on ID (hence only applicable for a single subelement)
	 *  <li>1 : identify HTML-subelements based on class
	 *  <li>2 : identify by tag type (direct child of widget tag)
	 *  <li>3 : identify by tag type (all children)
	 *  <li>4 : style applies to widget div itself; parameter {@code id} is ignored
	 * </ul>
	 * Preferred option: 2, since this does not affect nested widgets
	 */	
	public WidgetStyle(String id,List<String> clazz, int selectorId) {
		if (selectorId < 0  || selectorId > 4)
			throw new IllegalArgumentException("Invalid selectorId: " + selectorId);
		this.clazzMap = new HashMap<String,List<String>>();
		if (selectorId == 4)
			id = "";
		clazzMap.put(id, clazz);
		this.idClassTagToggle = selectorId;
	}
	
	/**
	 * @param clazzMap
	 * 		Keys: identifiers for the DOM elements to which the new classes shall be applied
	 * 		Values: CSS class names to be applied to the DOM elements
	 * @param selectorId: 
	 * <ul>
	 *  <li>0 : identify HTML-subelements based on ID (hence only applicable for a single subelement)
	 *  <li>1 : identify HTML-subelements based on class
	 *  <li>2 : identify by tag type (direct child of widget tag)
	 *  <li>3 : identify by tag type (all children)
	 * </ul>
	 * Preferred option: 2, since this does not affect nested widgets
	 */	
	public WidgetStyle(Map<String,List<String>> clazzMap, int selectorId) {
		if (selectorId < 0  || selectorId > 3)
			throw new IllegalArgumentException("Invalid selectorId: " + selectorId);
		this.clazzMap = clazzMap;
		this.idClassTagToggle = selectorId;
	}

	
	/************ Methods ******************/
	
	public Map<String,List<String>> getCssMap() {
		return clazzMap;
	}
	
	public int getSelectorType() {
		return idClassTagToggle;
	}
	
	 /* note: this implies that two styles for different generics parameters <T> and <S> can be equal,
	 * which probably makes sense, since they will have the same effect when applied to a given widget,
	 * although only the type appropriate for the widget should be used.
	 */ 
	@Override
	public boolean equals(Object obj) { 						 
		if (obj == null) return false;
		else if (obj == this) return true;
		if (!(obj instanceof WidgetStyle)) return false;
		WidgetStyle objStyle = (WidgetStyle) obj;
		if (idClassTagToggle != objStyle.getSelectorType()) return false;
		return objStyle.getCssMap().equals(clazzMap);
	}
	
	@Override
	public int hashCode() {
		return clazzMap.hashCode();
	}
}
