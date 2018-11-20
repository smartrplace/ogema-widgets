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
