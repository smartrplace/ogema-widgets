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

package de.iwes.widgets.pattern.widget.patternedit;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;

/**
 * This service allows to create basic user pages for creating and editing {@link ResourcePattern}s. It provides convenience methods
 * for the creation of pages containing a {@link PatternEditor} or {@link PatternCreator} widget.
 * The annotations defined in {@link PatternPageAnnotations} can be used to configure the representation of the pattern fields. 
 */
// TODO implement Unmodifiable annotation for booleanREsource and complex resources
public abstract class PatternPageUtil {
	
	public static PatternPageUtil getInstance(ApplicationManager am, WidgetApp widgetApp) {
		return new PatternPageUtilImpl(am, widgetApp);
	}
	
	/*********** Pattern creator page **********/
	
	/**
	 * See {@link #createPatternPage(Class, boolean, PatternCreatorConfiguration, Class)}
	 * @param pattern
	 * @param url
	 * @return
	 */
	public abstract <P extends ResourcePattern<R>, R extends Resource> PatternCreator<P, R, ?> newPatternCreatorPage(final Class<P> pattern, String url);
	
	/**
	 * See {@link #newPatternCreatorPage(Class, String, boolean, PatternCreatorConfiguration, Class)}.
	 * In addition, the user will be able to define the context of the pattern.
	 * @param pattern
	 * @param url
	 * @param contextType
	 * @return
	 */
	public abstract <P extends ContextSensitivePattern<R,C>, R extends Resource, C> PatternCreator<P, R, ?> 
			newContextPatternCreatorPage(final Class<P> pattern, String url, Class<C> contextType);
	
	/**
	 * Creates a user page allowing to create instances of the specified pattern. The page itself is accessible from the returned
	 * {@link PatternCreator} via its {@see org.ogema.gui.api.widgets.OgemaWidgetI#getPage()} method.
	 *  See {@link PatternCreatorConfiguration} for options.
	 * @param pattern
	 * 			The pattern type whose instances can be created by the user on the generated page.
	 * @param url
	 * 			url relative to the base url of the {@link WidgetApp app}
	 * @param setAsStartPage
	 * @param config
	 * 			Allows to specify several options for the new page.
	 * @param dictionary
	 * 			Provide localisation options for the labels of the page, as well as the page title. Note that individual language options
	 * 			must still be registered with the page via {@link WidgetPage#registerLocalisation(Class)}.<br><br>
	 * 
	 * 			In the default mode, the pattern entries are displayed on the generated page with their field names. If the provided 
	 * 			dictionary contains a method with String-signature and no arguments, of the same name as a pattern field entry, then
	 * 			the respective label will be replaced by the return value of the dictionary method. <br><br> 
	 * 
	 * 			If the provided dictionary class has a method <code>#pageTitle()</code>, then the page header will be adapted accordingly.
	 * 
	 * 			Pass <code>null</code> or {@link LocaleDictionary LocaleDictionary.class} if no dictionary shall be used.
	 * @return
	 */
	public abstract <P extends ResourcePattern<R>, R extends Resource, D extends LocaleDictionary> PatternCreator<P, R, D> newPatternCreatorPage(final Class<P> pattern, 
				String url, boolean setAsStartPage, PatternCreatorConfiguration<P, R> config, Class<D> dictionary);

	
	/**
	 * See {@link #newContextPatternCreatorPage(Class, String, boolean, PatternCreatorConfiguration, Class, Class)}.
	 * In addition, the user will be able to define the context of the pattern.
	 * @param pattern
	 * @param url
	 * @param setAsStartPage
	 * @param config
	 * @param dictionary
	 * @param contextType
	 * @return
	 */
	public abstract <P extends ContextSensitivePattern<R,C>, R extends Resource, C, D extends LocaleDictionary> PatternCreator<P, R, D> 
		newContextPatternCreatorPage(final Class<P> pattern, String url, boolean setAsStartPage, PatternCreatorConfiguration<P, R> config, Class<D> dictionary, Class<C> contextType);
	
	/*********** Pattern editor page **********/
	
	/**
	 * See {@link #newPatternEditorPage(Class, String, boolean, Class)}
	 */
	public abstract <P extends ResourcePattern<R>, R extends Resource> PatternEditor<P, ?> newPatternEditorPage(final Class<P> pattern, String url);	
	
	/**
	 * Creates a user page allowing to edit instances of the specified pattern. The page itself is accessible from the returned
	 * {@link PatternEditor} via its {@see org.ogema.gui.api.widgets.OgemaWidgetI#getPage()} method.
	 * @param pattern
	 * @param url
	 * @param setAsStartPage
	 * @param dictionary
	 * 			Provide localisation options for the labels of the page, as well as the page title. Note that individual language options
	 * 			must still be registered with the page via {@link WidgetPage#registerLocalisation(Class)}.<br><br>
	 * 
	 * 			In the default mode, the pattern entries are displayed on the generated page with their field names. If the provided 
	 * 			dictionary contains a method with String-signature and no arguments, of the same name as a pattern field entry, then
	 * 			the respective label will be replaced by the return value of the dictionary method. <br><br> 
	 * 
	 * 			If the provided dictionary class has a method <code>#pageTitle()</code>, then the page title will be adapted accordingly.<br>
	 * 			If the provided dictionary class has a method <code>#patternSelectLabel()</code>, then the pattern selection label will
	 * 			be customized accordingly.<br>
	 * 
	 * 			Pass <code>null</code> or {@link LocaleDictionary LocaleDictionary.class} if no dictionary shall be used.
	 * @return
	 */
	public abstract <P extends ResourcePattern<R>, R extends Resource, D extends LocaleDictionary> PatternEditor<P, D> newPatternEditorPage(final Class<P> pattern, 
			String url, boolean setAsStartPage, Class<D> dictionary);
	
	/*********** Pattern display page **********/
	
	/**
	 * @see {@link #newPatternDisplayPage(Class, String, boolean, Class)}
	 * 
	 * @param pattern
	 * @param url
	 * @return
	 */
	public abstract <P extends ResourcePattern<R>, R extends Resource> PatternEditor<P, ?> newPatternDisplayPage(final Class<P> pattern, String url);
	
	/**
	 * Display pattern values.
	 * Like {@link #newPatternEditorPage(Class, String, boolean, Class)}, except that the fields of the pattern cannot be modified via the GUI. 
	 * This could also be achieved through a pattern edit page by annotating all pattern fields with an 
	 * {@see org.ogema.tools.pattern.widget.patternedit.PatternPageAnnotations.Unmodifiable}
	 * annotation.
	 * 
	 * @param pattern
	 * @param url
	 * @param setAsStartPage
	 * @param dictionary
	 * 			Provide localisation options for the labels of the page, as well as the page title. Note that individual language options
	 * 			must still be registered with the page via {@link WidgetPage#registerLocalisation(Class)}.<br><br>
	 * 
	 * 			In the default mode, the pattern entries are displayed on the generated page with their field names. If the provided 
	 * 			dictionary contains a method with String-signature and no arguments, of the same name as a pattern field entry, then
	 * 			the respective label will be replaced by the return value of the dictionary method. <br><br> 
	 * 
	 * 			If the provided dictionary class has a method <code>#pageTitle()</code>, then the page title will be adapted accordingly.<br>
	 * 			If the provided dictionary class has a method <code>#patternSelectLabel()</code>, then the pattern selection label will
	 * 			be customized accordingly.<br>
	 * 
	 * 			Pass <code>null</code> or {@link LocaleDictionary LocaleDictionary.class} if no dictionary shall be used.
	 * @return
	 */
	public abstract <P extends ResourcePattern<R>, R extends Resource, D extends LocaleDictionary> PatternEditor<P, D> newPatternDisplayPage(final Class<P> pattern, 
			String url, boolean setAsStartPage, Class<D> dictionary);
	
}
