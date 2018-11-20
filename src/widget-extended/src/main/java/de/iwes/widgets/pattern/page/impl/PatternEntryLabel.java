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
package de.iwes.widgets.pattern.page.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Label;

/*
 * For each field or method of a pattern to be displayed on the page, an instance of this widget type is created.
 * It displays the name of the field/method, or an alternative text provided via a dictionary.
 */
public class PatternEntryLabel<D extends LocaleDictionary> extends Label {
	
	private final Class<D> dictionary;
	private final String fieldName;
	private final Method dictionaryMethod; 

	public PatternEntryLabel(WidgetPage<D> page, String id, String text, Class<D> dictionary) {
		super(page, id, text);
		this.fieldName = text; // 
		this.dictionary = dictionary;
		Method m = null;
		try {
			m = dictionary.getDeclaredMethod(fieldName); // check if the provided dictionary contains a method whose name matches the
																		// method or field name
		} catch (NoSuchMethodException | SecurityException e) {
		}
		this.dictionaryMethod = m;
		setDefaultText(text);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void onGET(OgemaHttpRequest req) {
		if (dictionary == null || dictionaryMethod == null || dictionary.equals(LocaleDictionary.class)) {
			return; // no dictionary provided 
		}
		@SuppressWarnings("unchecked")
		D dict = (D) getPage().getDictionary(req);
		if (dict == null) {
			LoggerFactory.getLogger(getClass()).error("Dictionary not available for page " + getPage().getFullUrl());
			return;
		}
		String labelText = invokeMethod(dict);
		if (labelText == null) labelText = fieldName;
		setText(labelText, req);
	}
	
	private String invokeMethod(D dictionaryObject) {
		if (dictionaryMethod == null || dictionaryObject == null || fieldName == null || dictionary == null) 
			return null;
		synchronized (dictionary) { // TODO move accessibility handling to calling method?

			boolean acc = dictionaryMethod.isAccessible();
			if (!acc) {
				dictionaryMethod.setAccessible(true);
			}
			String result;
			try {
				result = (dictionaryMethod.invoke(dictionaryObject)).toString();
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				return null;
			} finally {
				if (!acc) dictionaryMethod.setAccessible(false);
			}
			return result;
		}
	}
	
}
