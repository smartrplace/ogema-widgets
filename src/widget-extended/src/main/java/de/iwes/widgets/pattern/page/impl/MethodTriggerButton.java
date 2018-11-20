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
import de.iwes.widgets.html.form.button.Button;

/*
 * Used to trigger the display of a method result 
 */
public class MethodTriggerButton<D extends LocaleDictionary> extends Button {
	
	private final Class<D> dictionary;
	private final String methodName;
	private final TriggerableMethodLabel valueWidget;
	private Method dictionaryMethod = null; 

	public MethodTriggerButton(WidgetPage<D> page, String id, String text, TriggerableMethodLabel valueWidget, Class<D> dictionary) {
		super(page, id, text);
		this.methodName = text; // 
		this.dictionary = dictionary;
		this.valueWidget = valueWidget;
		try {
			dictionaryMethod = dictionary.getDeclaredMethod(methodName); // check if the provided dictionary contains a method whose name matches the
																		// method or field name
		} catch (NoSuchMethodException | SecurityException e) {
		}
		setDefaultText(text);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void onPrePOST(String data, OgemaHttpRequest req) {
		valueWidget.setMessage(req);
	}
	
	// FIXME duplicate from PatternEntryLabel
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
		if (labelText == null) labelText = methodName;
		setText(labelText, req);
	}
	
	// FIXME duplicate from PatternEntryLabel
	private String invokeMethod(D dictionaryObject) {
		if (dictionaryMethod == null || dictionaryObject == null || methodName == null || dictionary == null) 
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
