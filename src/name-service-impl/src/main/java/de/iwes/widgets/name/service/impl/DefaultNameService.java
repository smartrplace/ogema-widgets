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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.iwes.widgets.name.service.impl;


import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServlet;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.StringResource;
import org.slf4j.Logger;

import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.name.service.impl.dictionaries.DictionaryFactory;

@Component
@Service(NameService.class)
public class DefaultNameService extends HttpServlet implements NameService {
	
	private static final long serialVersionUID = 1L;
//	private static final String SERVLET_URL = "/ogema/nameservice/servlet";
	private Logger logger;
	private final DictionaryFactory factory = new DictionaryFactory();

//	@Reference
//	HttpService httpService;
	
	@Override
	public String getName(Class<? extends Resource> resourceType, OgemaLocale locale) {
		return getName(resourceType, locale, true);
	}

	@Override
	public String getName(Class<? extends Resource> resourceType, OgemaLocale locale, boolean useDefaultLanguage) {
		TypeDictionary dict = factory.getDictionary(locale);
		if (dict == null) {
			if (!useDefaultLanguage) return null;
			else dict = factory.getDefaultDictionary();
		}
		return (dict != null) ? dict.getName(resourceType) : null;
	}

	@Override
	public List<Class<? extends Resource>> getAvailableTypes(OgemaLocale locale) {
		TypeDictionary dict = factory.getDictionary(locale);
		if (dict == null) return Collections.emptyList();
		return dict.getAvailableTypes();
	}

	@Override
	public boolean isTypeAvailable(Class<? extends Resource> resourceType, OgemaLocale locale) {
		TypeDictionary dict = factory.getDictionary(locale);
		if (dict == null) return false;
		return dict.isTypeAvailable(resourceType);
	}
	

	@Override
	public String getName(Resource resource, OgemaLocale locale) {
		return getName(resource, locale, false, true);
	}

	@Override
	public String getName(Resource resource, OgemaLocale locale, boolean useRelativePathAsAlias, boolean useDefaultLanguage) {
		String name = null;
		String relativePath = "";
		while (name == null && resource != null) {
			name = getNameValue(resource);
			if (name == null) {
				if (relativePath.length() > 0) relativePath = "/" + relativePath;
				relativePath = resource.getName() + relativePath;
				try {
					resource = resource.getParent();
				} catch (SecurityException e) {
					break;
				}
			}
		}
		if (name == null || relativePath.length() == 0) return name;
		
		TypeDictionary dict = factory.getDictionary(locale);
		if (dict == null && useDefaultLanguage) {
			dict = factory.getDefaultDictionary();
		}
		if (dict == null) {
			if (!useRelativePathAsAlias) return null;
			return name + "/" + relativePath;
		}
		String alias = null;
		if (dict != null) {
			alias = dict.getName(relativePath, resource.getResourceType());	
		}
		if (alias == null) {
			if (!useRelativePathAsAlias) return null;
			return name + "/" + relativePath;
		}
		return name + ": " + alias;
	}

	@Override
	public String getServletUrl() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Not supported"); 
	}
	
	private String getNameValue(Resource resource) {
		try {
			Resource name = resource.getSubResource("name");
			if (name != null && name.isActive() && (name instanceof StringResource)) {
				return ((StringResource) name).getValue();
			}
		} catch (Exception e) {
		}
		return null;
	}
    
}
