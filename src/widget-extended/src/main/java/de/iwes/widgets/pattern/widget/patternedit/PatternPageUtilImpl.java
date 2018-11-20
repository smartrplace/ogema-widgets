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
package de.iwes.widgets.pattern.widget.patternedit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;

public class PatternPageUtilImpl extends PatternPageUtil {
	
	private final ApplicationManager am;
	private final WidgetApp app;

	PatternPageUtilImpl(ApplicationManager am, WidgetApp widgetApp) {
		this.am= Objects.requireNonNull(am);
		this.app = Objects.requireNonNull(widgetApp);
	}

	/*********** Create page **********/
	
	@Override
	public <P extends ResourcePattern<R>, R extends Resource> PatternCreator<P, R, ?> newPatternCreatorPage(Class<P> pattern, String url) {
		return newPatternCreatorPage(pattern, url, false, null, LocaleDictionary.class);
	}

	@Override
	public <P extends ResourcePattern<R>, R extends Resource, D extends LocaleDictionary> PatternCreator<P, R, D> newPatternCreatorPage(Class<P> pattern, 
			String url, boolean setAsStartPage, PatternCreatorConfiguration<P, R> config, Class<D> dictionary) {
		if (config == null)
			config = new PatternCreatorConfiguration<>();
		WidgetPage<D> page = app.createWidgetPage(url, setAsStartPage);
//		page.setTitle("Create new pattern: "+ pattern.getSimpleName());
		Header header = new PageHeader<D>(page, "header",dictionary);
		header.setDefaultText("Create new pattern: "+ pattern.getSimpleName());
		PatternCreator<P, R, D> pc = addPatternCreator(pattern, page, dictionary, config);
		page.append(header).linebreak().append(pc).linebreak();
		return pc;
	}	
	
	@Override
	public <P extends ContextSensitivePattern<R, C>, R extends Resource, C> PatternCreator<P, R, ?> newContextPatternCreatorPage(Class<P> pattern, String url, Class<C> contextType) {
		return newContextPatternCreatorPage(pattern, url, false, null, LocaleDictionary.class, contextType);
	}
	
	@Override
	public <P extends ContextSensitivePattern<R, C>, R extends Resource, C, D extends LocaleDictionary> PatternCreator<P, R, D> newContextPatternCreatorPage(
			Class<P> pattern, String url, boolean setAsStartPage, PatternCreatorConfiguration<P, R> config, Class<D> dictionary, Class<C> contextType) {
		if (config == null)
			config = new PatternCreatorConfiguration<>();
		WidgetPage<D> page = app.createWidgetPage(url, setAsStartPage);
//		page.setTitle("Create new pattern: "+ pattern.getSimpleName());
		Header header = new PageHeader<D>(page, "header",dictionary);
		header.setDefaultText("Create new pattern: "+ pattern.getSimpleName());
		PatternCreator<P, R, D> pc = addPatternCreator(pattern, page, dictionary, config, contextType);
		page.append(header).linebreak().append(pc).linebreak();
		return pc;
	}
	
	/*
	 * use either (admissibleParentType and allowNonToplevelParent), or baseResource. The respective other field(s) should be null 
	 */
	public <P extends ResourcePattern<R>, R extends Resource, D extends LocaleDictionary> PatternCreator<P, R, D> addPatternCreator(Class<P> pattern, WidgetPage<D> page,
				Class<D> dictionary,PatternCreatorConfiguration<P, R> config) {
		PatternCreator<P,R,D> pageCreator = PatternCreator.getInstance(page,pattern.getSimpleName() + "_patternCreator",pattern,am,config, dictionary);
		return pageCreator;
	}
	
	public <P extends ContextSensitivePattern<R,C>, R extends Resource, C, D extends LocaleDictionary> PatternCreator<P, R, D> addPatternCreator(Class<P> pattern, WidgetPage<D> page,
			Class<D> dictionary,PatternCreatorConfiguration<P, R> config, Class<C> contextType) {
		PatternCreator<P,R,D> pageCreator = PatternCreator.getInstance(page,pattern.getSimpleName() + "_patternCreator",pattern,am,config, dictionary, contextType);
		return pageCreator;
	}
	
	/********** Edit page *************/
	@Override
	public <P extends ResourcePattern<R>, R extends Resource> PatternEditor<P, ?> newPatternEditorPage(final Class<P> pattern, String url) {
		return newPatternEditorPage(pattern, url, false, LocaleDictionary.class);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public <P extends ResourcePattern<R>, R extends Resource, D extends LocaleDictionary> PatternEditor<P,D> newPatternEditorPage(
			final Class<P> pattern, String url,	boolean setAsStartPage, Class<D> dictionary) {
		if (dictionary == null)
			dictionary = (Class<D>) LocaleDictionary.class;
		WidgetPage<D> page = app.createWidgetPage(url, setAsStartPage);
//		page.setTitle("Edit pattern: "+ pattern.getSimpleName());
		Header header = new PageHeader<D>(page, "header", dictionary);	
		header.setDefaultText("Edit patterns: "+ pattern.getSimpleName());
		page.append(header).linebreak();
		PatternEditor<P,D> patternSelector = addPatternEditor(pattern, page, pattern.getSimpleName() + "_patternEditor", dictionary, false);
		page.append(patternSelector).linebreak();
		return patternSelector;
	}
	
	public <P extends ResourcePattern<R>, R extends Resource, D extends LocaleDictionary> PatternEditor<P, D> addPatternEditor(Class<P> pattern, WidgetPage<D> page,String widgetID, Class<D> dictionary, boolean forceUnmodifiable) {
		PatternEditor<P,D> pageCreator = PatternEditor.getInstance(page, widgetID,pattern,am, dictionary, forceUnmodifiable);
		return pageCreator;
	}
	
	@Override
	public <P extends ResourcePattern<R>, R extends Resource> PatternEditor<P, ?> newPatternDisplayPage(final Class<P> pattern, String url) {
		return newPatternDisplayPage(pattern, url, false, LocaleDictionary.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P extends ResourcePattern<R>, R extends Resource, D extends LocaleDictionary> PatternEditor<P,D> newPatternDisplayPage(
			final Class<P> pattern, String url,boolean setAsStartPage, Class<D> dictionary) {
		if (dictionary == null)
			dictionary = (Class<D>) LocaleDictionary.class;
		WidgetPage<D> page = app.createWidgetPage(url,setAsStartPage);
//		page.setTitle("View pattern: "+ pattern.getSimpleName());
		Header header = new Header(page, "header","View patterns: "+ pattern.getSimpleName());
		header.addDefaultStyle(HeaderData.CENTERED);
		page.append(header).linebreak();
		PatternEditor<P,D> patternSelector = addPatternEditor(pattern, page, pattern.getSimpleName() + "_displayWidget", dictionary, true);
		page.append(patternSelector).linebreak();
		return patternSelector;
	}
	
	private class PageHeader<D extends LocaleDictionary> extends Header {

		private static final long serialVersionUID = 1L;
		private final Method pageTitle;  // dictionary mehtod; may be null
		
		public PageHeader(WidgetPage<? extends D> page, String id, Class<D> dictionaryClass) {
			super(page, id);
			Method pageTitleA;
			try {
				pageTitleA = dictionaryClass.getDeclaredMethod("pageTitle");
			} catch (NoSuchMethodException | SecurityException | NullPointerException e1) {
				pageTitleA = null;
			}
			pageTitle = pageTitleA;
			addDefaultStyle(HeaderData.CENTERED);
		}

		@Override
		public void onGET(OgemaHttpRequest req) {
			if (pageTitle == null) return;
			@SuppressWarnings("unchecked")
			D dict = (D) getPage().getDictionary(req.getLocaleString());
			try {
				String result = pageTitle.invoke(dict).toString();
				setText(result, req);
			} catch (Exception e) {
				// FIXME
				e.printStackTrace();
			}
		};
		
	}

	@SuppressWarnings("unchecked")
	static Field[] getResourceInfoRecursively(final Class<? extends ResourcePattern<?>> radClass) {
        Class<? extends ResourcePattern<?>> clazz = radClass;
        final List<Field> result = new ArrayList<>();
        while (!clazz.equals(ResourcePattern.class)) {
            result.addAll(getResourceInfo(clazz));
            clazz = (Class<? extends ResourcePattern<?>>) clazz.getSuperclass();
        }
        final Field[] arr = new Field[result.size()];
        result.toArray(arr);
        return arr;
    }

	/*
	 * Gets the list of all resource fields with their annotated parameters.
	 * Write priority must be explicitly given, since it is not encoded in the
	 * annotations.
	 */
	private static List<Field> getResourceInfo(final Class<? extends ResourcePattern<?>> radClass) {
        return AccessController.doPrivileged(new PrivilegedAction<List<Field>>() {
            @Override
            public List<Field> run() {
                final List<Field> result = new ArrayList<>();
                for (Field field : radClass.getDeclaredFields()) {
                    if (Resource.class.isAssignableFrom(field.getType())) {
                        result.add(field);
                    }
                }
                return result;
            }

        });
    }
	
}
