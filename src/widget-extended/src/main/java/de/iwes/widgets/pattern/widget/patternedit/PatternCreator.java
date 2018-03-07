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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.pattern.page.impl.ContextUtil;
import de.iwes.widgets.pattern.page.impl.FilterRange;
import de.iwes.widgets.pattern.page.impl.LocalisationUtil;
import de.iwes.widgets.pattern.page.impl.PatternPageUtilInternal;
import de.iwes.widgets.pattern.page.impl.TypeSelectorDropdown;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.DisplayValue;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.Entry;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.SetValue;
import de.iwes.widgets.resource.widget.autocomplete.ResourcePathAutocomplete;

/**
 * A widget that allows the user to create resource pattern instances (see {@link ResourcePattern}). 
 * This only exists as a global widget, in particular the pattern type cannot be changed for individual 
 * users. Configuration for the individual pattern fields is possible via special annotations defined in
 * {@link PatternPageAnnotations}. 
 *
 * @param <P>
 * @param <R>
 * @param <D>
 */
public class PatternCreator<P extends ResourcePattern<R>,R extends Resource, D extends LocaleDictionary> extends PageSnippet {

	private static final long serialVersionUID = 1L;
	private final Resource baseResource;
	/**
	 * either a Class<? extends Resource>, or a Class<? extends ResourcePattern<?>>
	 */
	private final Class<P> pattern;
	private final Class<?> contextType;
	private final ApplicationManager am;
	private final Field[] fields;
	private final Method[] staticMethods;
	private final Field[] contextFields;
	private final Collection<Class<? extends R>> allowedTypes;
	private final Label parentSelectorLabel;
	private final ResourcePathAutocomplete parentSelector;
	private final boolean addTypeSelector;
	private final Label typeSelectorLabel;
	private final TypeSelectorDropdown<R> typeSelector;
	private final Map<String,OgemaWidgetBase<?>> labelWidgets =  new LinkedHashMap<String, OgemaWidgetBase<?>>();
	private final Map<String,OgemaWidgetBase<?>> valueWidgets = new LinkedHashMap<String, OgemaWidgetBase<?>>();
	private final Map<String,Boolean> references = new LinkedHashMap<String, Boolean>();
	 // values are String, Integer, Boolean, Long, for StringResource, IntegerResource, etc.
	private final Map<String,Class<?>> fieldTypes = new LinkedHashMap<String, Class<?>>();
	private final Map<String,String> externalFilters = new LinkedHashMap<String, String>();
	private final Map<String,FilterRange> externalRangeFilters = new LinkedHashMap<String, FilterRange>();
	@SuppressWarnings("unused")
	private final WidgetGroup labelGroup;
	private final WidgetGroup valueGroup;
	private final Alert alert;
	private final Button createButton;
	private final Label nameLabel;
	// this is either a TextField (if name is editable), or a Label (it it is non-editable)
	private final OgemaWidget name;
	
	public static <P extends ContextSensitivePattern<R,C>,R extends Resource, D extends LocaleDictionary, C> PatternCreator<P,R,D> getInstance(final WidgetPage<D> page, final  String id, 
			final Class<P> pattern, final ApplicationManager am, final PatternCreatorConfiguration<P, R> config, final Class<D> dictionary, final Class<C> contextType) {
		return AccessController.doPrivileged(new PrivilegedAction<PatternCreator<P,R,D>>() {

			@Override
			public PatternCreator<P,R,D> run() {
				return new PatternCreator<P,R,D>(page, id, pattern, am, config, dictionary, contextType);
			}
		});
	}
	
	// this construction is required for permission reasons
	public static <P extends ResourcePattern<R>,R extends Resource, D extends LocaleDictionary> PatternCreator<P,R,D> getInstance(final WidgetPage<D> page, final  String id, 
			final Class<P> pattern, final ApplicationManager am, final PatternCreatorConfiguration<P, R> config, final Class<D> dictionary) {
		return AccessController.doPrivileged(new PrivilegedAction<PatternCreator<P,R,D>>() {

			@Override
			public PatternCreator<P,R,D> run() {
				return new PatternCreator<P,R,D>(page, id, pattern, am, config, dictionary, null);
			}
		});
	}
	
	private PatternCreator(WidgetPage<D> page, String id, Class<P> pattern, ApplicationManager am, PatternCreatorConfiguration<P, R> config, Class<D> dictionary, Class<?> contextType) {
		this(page, id, pattern, am, config.getBaseResource(), config.getAdmissibleParentType(), config.isAllowNonToplevelParent(), 
				config.getAllowedTypes(), dictionary, config.getDefaultResourceName(), config.isAllowNonDefaultName(), contextType);
	}
	
/**
 * 
 * @param snippet
 * @param pattern
 * @param am
 * @param baseResource
 * @param allowedTypes may be null or empty, in which case only the base type R is allowed 
 * 		new patterns will be created below this node; may be null, in which case top-level resources are created
 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PatternCreator(WidgetPage<D> page, String id, Class<P> pattern, ApplicationManager am, Resource baseResource, 
			Class<?> admissibleParentType, boolean allowNonToplevelParent, Collection<Class<? extends R>> allowedTypes, Class<D> dictionaryClass, 
			String defaultName, boolean allowNonDefault, Class<?> contextType) {
		super(page, id, true);
		this.baseResource = baseResource;
		this.pattern = pattern;
		this.contextType = contextType;
		if (contextType == null)
			this.contextFields = null;
		else {
			final List<Field> fields = new ArrayList<>();
			Entry e;
			for (Field f : contextType.getDeclaredFields()) {
				e = f.getAnnotation(Entry.class);
				if (e == null)
					continue;
				fields.add(f);
			}
			this.contextFields = new Field[fields.size()];
			fields.toArray(this.contextFields);
		}
		this.allowedTypes = allowedTypes;
		if (allowedTypes != null && !allowedTypes.isEmpty())
			addTypeSelector = true;
		else
			addTypeSelector = false;
		
		this.am = am;
		if (dictionaryClass == null)
			dictionaryClass = (Class<D>) LocaleDictionary.class;
		this.alert = new Alert(page, id + "_alert", "");
		alert.setDefaultVisibility(false);
		this.nameLabel = new Label(page, id + "_nameSelectionLabel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(LocalisationUtil.topResource(req.getLocale()), req);
			}
		};
		nameLabel.setDefaultText("Top-level resource");
		
		if (defaultName == null || allowNonDefault) {
//			this.nameEditable = true;
			this.name = new TextField(page, id + "_nameSelection");
			if (defaultName != null)
				((TextField) name).setDefaultValue(defaultName);
			else
				((TextField) name).setDefaultPlaceholder("Enter name");
		}
		else {
//			this.nameEditable = false;
			this.name = new Label(page, id + "_nameSelection", defaultName);
		}
		
		this.createButton = new SubmitButton(page, id + "_createButton","Create");
		createButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		// add parent selector
		if (baseResource == null && admissibleParentType != null) {
			parentSelector = new ResourcePathAutocomplete(page, id + "_parentSelector", am.getResourceAccess());
			parentSelector.setDefaultResourceType((Class<? extends Resource>) admissibleParentType); // TODO deal with pattern class
			parentSelectorLabel = new Label(page, id + "_parentSelectorLabel", "Select parent resource") {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onGET(OgemaHttpRequest req) {
					OgemaLocale locale = req.getLocale();
					String resType = parentSelector.getResourceType(req).getSimpleName();
					setText(LocalisationUtil.parentSelectorLabel(resType,locale), req);
				}
				
			};
		} else {
			parentSelector = null;
			parentSelectorLabel = null;
		}
		
		if (addTypeSelector) {
			typeSelectorLabel = new Label(page, id + "_typeSelectorLabel","Select type") {

				private static final long serialVersionUID = 1L;

				@Override
				public void onGET(OgemaHttpRequest req) {
					OgemaLocale locale = req.getLocale();
					setText(LocalisationUtil.typeSelectorLabel(locale), req);
				}
			};
			typeSelector = 
					new TypeSelectorDropdown<R>(page, id + "_typeSelectorDropdown", allowedTypes);
		}
		else {
			typeSelectorLabel = null;
			typeSelector = null;
		}
//		fields = pattern.getDeclaredFields();
		fields = PatternPageUtilImpl.getResourceInfoRecursively(pattern);
		staticMethods = getAnnotatedStaticMethods(pattern);
		PatternPageUtilInternal.createWidgets(page, /*pattern.getSimpleName()*/ id, fields, staticMethods, contextFields, labelWidgets, valueWidgets, references, fieldTypes, externalFilters, externalRangeFilters, null, true, am, dictionaryClass, true, false);
		buildPage();
		
		if (labelWidgets.isEmpty()) {
			labelGroup = null;
			valueGroup = null;
		} else {
			labelGroup = page.registerWidgetGroup("labelWidgets", (Collection) labelWidgets.values());
			valueGroup = page.registerWidgetGroup("valueWidgets", (Collection) valueWidgets.values());
			this.triggerAction(valueGroup, TriggeringAction.PRE_POST_REQUEST, TriggeredAction.POST_REQUEST);
		}
	}
	
	private class SubmitButton extends Button {

		private static final long serialVersionUID = 1L;

		public SubmitButton(WidgetPage<D> page, String id, String defaultText) {
			super(page, id, defaultText);
		}
	
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			OgemaLocale locale = req.getLocale();
			final P newPattern;
			try {
				String newResName; 
				if (name instanceof TextField) 
					newResName = ((TextField) name).getValue(req);
				else 
					newResName = ((Label) name).getText(req);
				if (!isValidJavaIdentifier(newResName)) throw new IllegalArgumentException(LocalisationUtil.invalidResourceName(newResName,locale));
				PatternPageUtilInternal.applyInternalAndExternalFilters(fieldTypes, valueWidgets, externalFilters, externalRangeFilters, req);	// throws exceptions that will be displayed on UI
				final Object context = (contextType == null ? null :  ContextUtil.createContext(contextType, contextFields, valueWidgets, am, req));
				newPattern = createPattern(newResName, context, req);
				if (newPattern == null) {
					NullPointerException e = new NullPointerException("Pattern could not be created; create returned null");
					am.getLogger().error("",e);
					throw e;
				}
				// FIXME where was this used? To be removed...
				if (contextType == null && newPattern instanceof ContextSensitivePattern) {
					try {
						((ContextSensitivePattern<?,?>) newPattern).init();
					} catch (Throwable e) {
						LoggerFactory.getLogger(PatternPageAnnotations.class).error("Exception thrown in pattern init() method; type " + pattern.getSimpleName() + ": " + e); 
					}
				}
				Map<String,String> exceptions = PatternPageUtilInternal.setValues(newPattern, fields, references, valueWidgets, req, am); // TODO same for editor
				am.getResourcePatternAccess().activatePattern(newPattern);
				newPattern.model.activate(true);
				if (exceptions != null) {
					StringBuilder sb =new StringBuilder();
					if (exceptions.size() == 1) {
						Map.Entry<String, String> entry = exceptions.entrySet().iterator().next();
						sb.append(entry.getValue());
					}
					else {
						sb.append("There were multiple problems with the pattern values: ");  // TODO localize
						for (Map.Entry<String, String> entry:exceptions.entrySet()) {
							sb.append(entry.getKey() + ", ");
						}
						sb.append("failed");
					}
					alert.setText(sb.toString(), req);
					alert.setStyle(AlertData.BOOTSTRAP_WARNING, req);
				}
				else {
					alert.setText(LocalisationUtil.patternCreated(newResName, locale), req);
					alert.setStyle(AlertData.BOOTSTRAP_SUCCESS, req);
				}
				
			} catch (Exception e) {
				alert.setText(LocalisationUtil.patternNotCreated(locale) +": " + e, req);
				alert.setStyle(AlertData.BOOTSTRAP_DANGER, req);
			}
			alert.setWidgetVisibility(true, req);
			alert.autoDismiss(6000, req);
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			setText(LocalisationUtil.create(req.getLocale()), req);
		}
	}
		
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private P createPattern(final String newResName, final Object context, final OgemaHttpRequest req) throws NoSuchMethodException, SecurityException, 
					InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {	
		final Resource parent;
		if (parentSelector != null) {
			parent = parentSelector.getSelectedResource(req);
			if (parent == null)
				throw new IllegalStateException(LocalisationUtil.selectParentMessage(req.getLocale()));
		}
		else 
			parent = baseResource;
		// FIXME required?
		final Resource res = AccessController.doPrivileged(new PrivilegedAction<Resource>() {
			@Override
			public Resource run() {
				if (parent == null) 
					return am.getResourceAccess().getResource(newResName); 
				else 
					return parent.getSubResource(newResName);
			}
		});
		if (res != null && res.exists()) // FIXME what if the type of the non-existent but virtual resource is not compatible?
			throw new IllegalArgumentException(LocalisationUtil.resourceExistsMessage(newResName, req.getLocale()));
		Class<? extends Resource> selectedResType = null;
		if (addTypeSelector) {
			DropdownOption selected = typeSelector.getSelected(req);
			if (selected != null) { 
				String type = selected.id();
				try {
					selectedResType = getTypeClass(type);
					if (selectedResType == null) throw new RuntimeException("Class not among the allowed types");
				} catch (Exception e) {
					am.getLogger().error("Selected type "+ type + " could not be found.", e);
				}
			}
		}
		if (parent == null) {
			// pattern management always tries to create pattern generic type resource,
			// so we need to create the subtype resource before appliyng the pattern management method
			if (selectedResType != null && Schedule.class.isAssignableFrom(selectedResType)) {
				FloatResource fl = am.getResourceManagement().createResource(newResName, FloatResource.class);
				fl.addDecorator("schedule", selectedResType);
				if (context == null)
					return am.getResourcePatternAccess().addDecorator(fl,"schedule", pattern);
				else
					return (P) am.getResourcePatternAccess().addDecorator(fl,"schedule", (Class) pattern, context);
			}
			else if (selectedResType != null) 
				am.getResourceManagement().createResource(newResName, selectedResType);  
			if (context == null)
				return am.getResourcePatternAccess().createResource(newResName, pattern);
			else
				return (P) am.getResourcePatternAccess().createResource(newResName, (Class) pattern, context);
		}
		else {
			if (selectedResType != null && Schedule.class.isAssignableFrom(selectedResType)) {
				FloatResource fl = parent.addDecorator(newResName, FloatResource.class); // TODO allow other types than Float?
				fl.addDecorator("schedule", selectedResType);
				if (context == null)
					return am.getResourcePatternAccess().addDecorator(fl,"schedule", pattern);
				else
					return (P) am.getResourcePatternAccess().addDecorator(fl,"schedule", (Class) pattern, context);
			}
			else if (selectedResType != null) 
				parent.addDecorator(newResName, selectedResType);
			if (context == null)
				return am.getResourcePatternAccess().addDecorator(parent, newResName, pattern);
			else
				return (P) am.getResourcePatternAccess().addDecorator(parent, newResName, (Class) pattern, context);
		}
	}
	
	private Class<? extends R> getTypeClass(String className) {
		for (Class<? extends R> type : allowedTypes) {
			if (type.getName().equals(className)) return type;
		}
		return null;
	}
	
	private final void buildPage() {
		append(alert, null);
		int[] sizes = new int[] { 2, 4 };
		int nrRows = labelWidgets.size()+1;
		if (addTypeSelector) 
			nrRows++;
		if (parentSelector != null)
			nrRows++;
		StaticTable table = new StaticTable(nrRows, 2, sizes);
		if (parentSelector != null)
			addLine(table, parentSelectorLabel, parentSelector);
		addLine(table, nameLabel, (OgemaWidgetBase<?>) name);
		if (addTypeSelector) 
			addLine(table, typeSelectorLabel, typeSelector);
		for (Map.Entry<String, OgemaWidgetBase<?>> entry : labelWidgets.entrySet()) {
			String id = entry.getKey();
			OgemaWidgetBase<?> label = entry.getValue();
			OgemaWidgetBase<?> value = valueWidgets.get(id);
			addLine(table, label, value);
		}
		append(table, null);
		append(createButton, null);
	}
	
	private int lineCounter = 0;
	
	private void addLine(StaticTable table, OgemaWidgetBase<?> labelWidget, OgemaWidgetBase<?> valueWidget) {
		table.setContent(lineCounter, 0, labelWidget).setContent(lineCounter++, 1, valueWidget);
	}
	
	private static Method[] getAnnotatedStaticMethods(Class<? extends ResourcePattern<?>> pattern) {
		Method[] methods = pattern.getDeclaredMethods();
		List<Method> methodList = new ArrayList<Method>();
		for (Method method:methods) {
			if (!Modifier.isStatic(method.getModifiers())) continue;
			DisplayValue dv = method.getAnnotation(DisplayValue.class);
			SetValue sv = method.getAnnotation(SetValue.class);
			if (dv != null || sv != null) methodList.add(method);
		}
		Method[] result = new Method[methodList.size()];
		int counter = 0;
		for (Method method: methodList) {
			result[counter++] = method;
		}
  		return result;
	}
	
	private static boolean isValidJavaIdentifier(String s) {
	    if (s == null || s.isEmpty()) {
	        return false;
	    }
	    if (!Character.isJavaIdentifierStart(s.charAt(0))) {
	        return false;
	    }
	    for (int i = 1; i < s.length(); i++) {
	        if (!Character.isJavaIdentifierPart(s.charAt(i))) {
	            return false;
	        }
	    }
	    return true;
	}
	
	/**
	 * @return
	 * 		the submit button, by means of which changes in the selected pattern are saved.
	 */
	public Button getSubmitButton() {
		return createButton;
	}

}
