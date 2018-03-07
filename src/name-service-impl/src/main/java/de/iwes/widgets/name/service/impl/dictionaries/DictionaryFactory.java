/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package de.iwes.widgets.name.service.impl.dictionaries;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.name.service.impl.DefaultNameService;
import de.iwes.widgets.name.service.impl.TypeDictionary;

public class DictionaryFactory {
	
	private Map<String, Class<? extends TypeDictionary>> dicts = new HashMap<String, Class<? extends TypeDictionary>>();
	private ConcurrentMap<String, TypeDictionary> initialisedDicts = new ConcurrentHashMap<String, TypeDictionary>();
	
	public DictionaryFactory() {
		init();
	}
	
	public TypeDictionary getDefaultDictionary() {
		TypeDictionary dict = getDictionary(OgemaLocale.ENGLISH);
		if (dict != null) return dict;
		return getFirstEntry();
	}

	public TypeDictionary getDictionary(OgemaLocale locale) {
		if (locale == null) return null;
		String lang = locale.getLanguage();
		return getDictionary(lang);
	}
	
	public TypeDictionary getDictionary(String lang) {
		if (lang == null) return null;
		TypeDictionary dict = initialisedDicts.get(lang);
		if (dict != null) return dict;
		Class<? extends TypeDictionary> clz = dicts.get(lang);
		if (clz == null) return null;
		try {
			dict = clz.getConstructor().newInstance();
			initialisedDicts.put(lang, dict);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			LoggerFactory.getLogger(DefaultNameService.class).error("Could not create a type dictionary for {}: {}",lang,e);
		}
		return dict; 
	}
	
	
	private void init() {
		dicts.put(OgemaLocale.ENGLISH.getLanguage(), English.class);
		dicts.put(OgemaLocale.GERMAN.getLanguage(), German.class);
	}
	
	// last fallback option only
	private TypeDictionary getFirstEntry() {
		if (!initialisedDicts.isEmpty()) {
			Iterator<TypeDictionary> initit = initialisedDicts.values().iterator();
			while (initit.hasNext()) {
				return initit.next();
			}
		}
		if (dicts.isEmpty()) return null;
		Iterator<String> it = dicts.keySet().iterator();
		String lang = null;
		while (it.hasNext()) {
			lang = it.next();
			break;
		}
		return getDictionary(lang);
	}

}
