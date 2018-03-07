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
