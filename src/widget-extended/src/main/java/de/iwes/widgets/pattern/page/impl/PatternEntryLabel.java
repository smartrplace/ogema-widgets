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
