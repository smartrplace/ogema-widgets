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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.extended.pattern.PatternSelector;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirm;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirmData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.pattern.page.impl.ControllableDropdown;
import de.iwes.widgets.pattern.page.impl.FieldUtils;
import de.iwes.widgets.pattern.page.impl.FilterRange;
import de.iwes.widgets.pattern.page.impl.LocalisationUtil;
import de.iwes.widgets.pattern.page.impl.PatternPageUtilInternal;
import de.iwes.widgets.pattern.widget.dropdown.PatternDropdown;
import de.iwes.widgets.pattern.widget.init.PatternInitDropdown;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.DisplayValue;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.SetValue;

/**
 * A widget that allows the user to edit resource pattern instances (see {@link ResourcePattern}). 
 * This only exists as a global widget, in particular the pattern type cannot be changed for individual 
 * users. Configuration for the individual pattern fields is possible via special annotations defined in
 * {@link PatternPageAnnotations}. 
 *
 * @param <P>
 * @param <D>
 */
public class PatternEditor<P extends ResourcePattern<?>, D extends LocaleDictionary> extends PageSnippet {

	private static final long serialVersionUID = 1L;
//	private final PageSnippet snippet;
	final Class<P> pattern;
	final ApplicationManager am;
	private final NameService nameService;
	private volatile Method patternSelectLabel; // final
	private final Field[] fields;
	private final Method[] methods;
	// keys are always (?) field names or method names
	private final Map<String,OgemaWidgetBase<?>> labelWidgets = new LinkedHashMap<String, OgemaWidgetBase<?>>();
	private final Map<String,OgemaWidgetBase<?>> valueWidgets = new LinkedHashMap<String, OgemaWidgetBase<?>>();
	private final Map<String,Boolean> references = new LinkedHashMap<String, Boolean>();
	 // values are String, Integer, Boolean, Long, Byte for StringResource, IntegerResource, etc.; only relevant for SingleValueResource & ByteArrayResource fields
	private final Map<String,Class<?>> fieldTypes = new LinkedHashMap<String, Class<?>>();
	private final Map<String,String> externalStringFilters = new LinkedHashMap<String, String>();
	private final Map<String,FilterRange> externalRangeFilters = new LinkedHashMap<String, FilterRange>();
	@SuppressWarnings("unused")
	private final WidgetGroup labelGroup;
	private final WidgetGroup valueGroup;
	private final Alert alert;
	private final SubmitButton createButton;
	private final Label selectLabel;
	final PatternDropdown<P> select;
	@SuppressWarnings("unused")
	private final Label deleteLabel;
	private final ButtonConfirm deleteButton;
	private final String widgetID; // TODO use
	
	// this construction is required for permission reasons
	public static <P extends ResourcePattern<?>, D extends LocaleDictionary> PatternEditor<P,D> getInstance(final WidgetPage<D> page, 
				final String widgetID, final Class<P> pattern, final ApplicationManager am, final Class<D> dictionary, final boolean forceUnmodifiable) {
		return AccessController.doPrivileged(new PrivilegedAction<PatternEditor<P,D>>() {

			@Override
			public PatternEditor<P,D> run() {
				return new PatternEditor<P,D>(page, widgetID, pattern, am, dictionary, forceUnmodifiable);
			}
		});
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PatternEditor(final WidgetPage<D> page, String widgetID, Class<P> pattern, ApplicationManager am, Class<D> dictionary, boolean forceUnmodifiable) {
		super(page, widgetID, true);
		
		this.pattern = pattern;
//		if (widgetID != null && !widgetID.isEmpty())
			this.widgetID = widgetID;
//		else
//			this.widgetID = pattern.getSimpleName(); 
		this.nameService = getNameService();
		this.am = am;
		if (dictionary == null)
			dictionary = (Class<D>) LocaleDictionary.class;
		this.alert = new Alert(page, this.widgetID + "_alert", "");
		alert.setDefaultVisibility(false);
		
		this.selectLabel = new Label(page, this.widgetID + "_selectLabel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (patternSelectLabel == null) return;
				D dict = page.getDictionary(req.getLocaleString());
				try {
					String result = patternSelectLabel.invoke(dict).toString();
					setText(result, req);
				} catch (Exception e) {
					// FIXME
					e.printStackTrace();
				}
			}
			
		};
		selectLabel.setDefaultText("Select a pattern");

//		String selectID;
//		if (widgetID == null || widgetID.isEmpty())
//			selectID = this.widgetID + "_patternSelectionDropdown";
//		else 
//			selectID = widgetID;
		this.select = new PatternInitDropdown(page,widgetID + "_selectionDropdown", pattern, am.getResourcePatternAccess()) {

			private static final long serialVersionUID = 1L;

			@Override 
			public void onPOSTComplete(String data, OgemaHttpRequest req) { 
				String selected = getSelectedPath(req);
				setDependentWidgetValues(selected,getItems(req),req);
			}
			
			@Override
			public void init(OgemaHttpRequest req) {
				super.init(req);
				String selected = getSelectedPath(req);
				setDependentWidgetValues(selected,getItems(req),req);
			}
//			
//			// this is only called when data is retrieved for the first time... in this case the selected value is set server-side (if at all),
//			// hence onPOSTComplete must be triggered manually, in order to report the selected value to dependent widgets
//			// -> moved to PatternDropdown
//			@Override
//			public void appendWidgetInformation(OgemaHttpRequest req) {
//				super.appendWidgetInformation(req); 
//				onPOSTComplete("{}", req); // set dependent widgets
//			}
			
		};
		select.setDefaultAddEmptyOption(true);
				
				
//				new PatternSelectionDropdown(page,"patternSelectionDropdown", this, pattern, am.getResourcePatternAccess());
		if (!forceUnmodifiable) {
			this.createButton = new SubmitButton(page, this.widgetID + "_createButton","Save") {
			
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					OgemaLocale loc = req.getLocale();
					if (loc == null) loc = OgemaLocale.ENGLISH;
					setText(LocalisationUtil.saveButtonText(loc), req);
				}
				
			};
			createButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			// TODO
//			if (page instanceof WidgetPageSimpleSelectEditImpl) 
//				((WidgetPageSimpleSelectEditImpl) page).button = createButton;
//			select.button = createButton;
			
			this.deleteLabel = new Label(page,this.widgetID + "_deleteLabel","Delete pattern") {
				
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					OgemaLocale loc = req.getLocale();
					if (loc == null) loc = OgemaLocale.ENGLISH;
					setText(LocalisationUtil.deleteButtonText(loc), req);
				};
				
			};
			this.deleteButton = new DeleteButton(page, this.widgetID + "_deleteButton", "Delete pattern");
		}
		else {
			this.deleteLabel = null;
			this.deleteButton = null;
			this.createButton = null;
		}
//		fields = pattern.getDeclaredFields();
		fields = PatternPageUtilImpl.getResourceInfoRecursively(pattern);
		if (!dictionary.equals(LocaleDictionary.class)) {
//			methods = getAnnotatedMethods(pattern);
 			try {
				patternSelectLabel = dictionary.getDeclaredMethod("patternSelectLabel");
			} catch (NoSuchMethodException | SecurityException e) {
				patternSelectLabel = null;
			}
		}
		else {
//			methods = new Method[0]; // in this case there are no dictionary entries for the methods anyway
			patternSelectLabel = null;
		}
		methods = getAnnotatedMethods(pattern);
		PatternPageUtilInternal.createWidgets(page, this.widgetID, fields, methods, null, labelWidgets, valueWidgets, references, fieldTypes, externalStringFilters, externalRangeFilters, select, false, am, dictionary, false, forceUnmodifiable);
		if (labelWidgets.isEmpty()) {
			labelGroup = null;
			valueGroup = null;
			return; // ?
		}
		buildPage(!forceUnmodifiable);	
		labelGroup = page.registerWidgetGroup(this.widgetID + "_labelWidgets", (Collection) labelWidgets.values());  // FIXME does this work??
		valueGroup = page.registerWidgetGroup(this.widgetID + "_valueWidgets", (Collection) valueWidgets.values());
		select.triggerAction(valueGroup, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		if (!forceUnmodifiable) {
			select.triggerAction(deleteButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			createButton.triggerAction(valueGroup, TriggeringAction.PRE_POST_REQUEST, TriggeredAction.POST_REQUEST);
			deleteButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			deleteButton.triggerAction(select, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			deleteButton.triggerAction(valueGroup, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
		setInitialDropdownStatus();
	}
	
	class SubmitButton extends Button {

		private static final long serialVersionUID = 1L;

		public SubmitButton(WidgetPage<?> page, String id, String defaultText) {
			super(page, id, defaultText);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			OgemaLocale locale = req.getLocale();
			try {
				DropdownOption selected = select.getSelected(req);
				if (selected == null || selected.id().equals(DropdownData.EMPTY_OPT_ID)) return;
				String selectedModel  = selected.id();
				PatternPageUtilInternal.applyInternalAndExternalFilters(fieldTypes, valueWidgets, externalStringFilters, externalRangeFilters, req);	// throws exceptions that will be displayed on UI
				P newPattern = getPattern(selectedModel, req);
				PatternPageUtilInternal.setValues(newPattern, fields, references, valueWidgets, req, am);
				PatternPageUtilInternal.setValues(pattern,newPattern, methods, valueWidgets, req, am);

				am.getResourcePatternAccess().activatePattern(newPattern);
				alert.setText(LocalisationUtil.patternModified(selectedModel, locale), req);
				alert.setStyle(AlertData.BOOTSTRAP_SUCCESS, req);
			} catch (Exception e) {
				alert.setText(LocalisationUtil.patternModificationFailed(locale) + ": " + e, req);
				alert.setStyle(AlertData.BOOTSTRAP_DANGER, req);
			}
			alert.setWidgetVisibility(true, req);
			alert.autoDismiss(6000, req);
		}
	}
	

	
	final static DropdownOption EMPTY_OPT_SELECTED  =new DropdownOption(DropdownData.EMPTY_OPT_ID, "", true);
	final static DropdownOption EMPTY_OPT_DESELECTED  =new DropdownOption(DropdownData.EMPTY_OPT_ID, "", false);
	
	private P getPattern(final String path, OgemaHttpRequest req) throws NoSuchMethodException, SecurityException, 
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {		
		Resource res = AccessController.doPrivileged(new PrivilegedAction<Resource>() {
			@Override
			public Resource run() {
				return am.getResourceAccess().getResource(path); 
			}
		});
		if (res == null)
			throw new RuntimeException("Pattern resource is null... this should not happen;");
		if (res.isTopLevel())
			return am.getResourcePatternAccess().createResource(path, pattern);
		else {
			Resource parent = res.getParent();
			return am.getResourcePatternAccess().addDecorator(parent, res.getName(), pattern);
		}
	}
	
	private void buildPage(boolean editable) {
		append(alert, null);
		int[] sizes = new int[] { 2, 4 };
		StaticTable table = new StaticTable(labelWidgets.size()+2, 2, sizes);
		addLine(table, selectLabel, select);
		for (Map.Entry<String, OgemaWidgetBase<?>> entry : labelWidgets.entrySet()) {
			String id = entry.getKey();
			OgemaWidgetBase<?> label = entry.getValue();
			OgemaWidgetBase<?> value = valueWidgets.get(id);
			addLine(table, label, value);
		}
		append(table, null);
		if (editable) {
			append(createButton, null).linebreak(null);
			append(deleteButton, null);
		}
	}
	
	private int lineCounter = 0;
	
	private void addLine(StaticTable table, OgemaWidgetBase<?> labelWidget, OgemaWidgetBase<?> valueWidget) {
		table.setContent(lineCounter, 0, labelWidget).setContent(lineCounter++, 1, valueWidget);
	}
	
	private class DeleteButton extends ButtonConfirm {

		private static final long serialVersionUID = 1L;

		public DeleteButton(WidgetPage<?> page, String id, String defaultText) {
			super(page, id, defaultText);
			Set<WidgetStyle<?>> styles = new HashSet<WidgetStyle<?>>();
			styles.add(ButtonConfirmData.CANCEL_LIGHT_BLUE);
			styles.add(ButtonConfirmData.CONFIRM_RED);
			setDefaultStyles(styles);
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			String selected = getSelectedPath(req);
			OgemaLocale locale = req.getLocale();
			if (selected != null) {
				setCancelBtnMsg(LocalisationUtil.cancelMsg(locale),req);
				setConfirmBtnMsg(LocalisationUtil.deleteButtonText(locale),req);
				setConfirmPopupTitle(LocalisationUtil.deleteConfirmationPopupTitle(locale),req);	
				setConfirmMsg(LocalisationUtil.deleteConfirmationMsg(selected, locale), req);
				enable(req); 
			}
			else {
				setCancelBtnMsg("Cancel",req);
				setConfirmBtnMsg("Cancel",req);
				setConfirmPopupTitle("I should be inactive!",req);	
				setConfirmMsg("Nothing to be seen here.", req);	
				disable(req); // TODO not working yet
			}
			setText(LocalisationUtil.deleteResource(locale), req);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			String selected = getSelectedPath(req);
			if (selected == null) return;
			OgemaLocale locale = req.getLocale();
			try {
				Resource res = am.getResourceAccess().getResource(selected);
				if (res == null) return;
				res.delete();	
				alert.setText(LocalisationUtil.deletionConfirmed(selected, locale),req);  // TOOO localise
				alert.setStyle(AlertData.BOOTSTRAP_SUCCESS, req);
			}
			catch (Exception e) {
				alert.setText(LocalisationUtil.deletionFailed(selected, locale) + "; " + e,req);
				alert.setStyle(AlertData.BOOTSTRAP_DANGER, req);
			}
			alert.setWidgetVisibility(true, req);
			alert.autoDismiss(6000, req);
			select.selectSingleOption(DropdownData.EMPTY_OPT_ID, req);
			PatternPageUtilInternal.clearWidgets(valueWidgets, req);
		}
		
	};
	
	String getSelectedPath(OgemaHttpRequest req) {
		DropdownOption opt  =select.getSelected(req);
		if (opt == null || opt.id().equals(DropdownData.EMPTY_OPT_ID)) return null;
		return opt.id();
	}
	
	void setDependentWidgetValues(String path, Collection<P> patterns, OgemaHttpRequest req) {
		P pattern = null;
		if (path != null) {
			for (P pt: patterns) {
				if (pt.model.getPath().equals(path)) {
					pattern  =pt;
					break;
				}
			}
		}
		if (pattern == null) {
			PatternPageUtilInternal.clearWidgets(valueWidgets, req);
		}
		else {
			Boolean[] accessible = FieldUtils.setAccessiblePrivileged(fields);
			for (Map.Entry<String,OgemaWidgetBase<?>> entry : valueWidgets.entrySet()) {
				String name =entry.getKey();
				Field field = getField(name);
				Method method = getMethod(name);
				if (field != null) {
					Resource res = FieldUtils.getResource(field, pattern);	
					if (res == null) {
						am.getLogger().error("Resource " + name + " not found in pattern... ?");
						continue;
					}
					PatternPageUtilInternal.setWidgetValue(entry.getValue(), res, req);
				}
				else if (method != null) {
//					PatternPageUtilInternal.setMethodValue(entry.getValue(), res, req); // see below... all set at once 
																						// do not continue yet, since labels are all set below
				}
				else {
					am.getLogger().error("Field of method " + name + " not found... ?");
					continue;
				}
			}
			FieldUtils.setUnaccessiblePrivileged(fields, accessible);
			
			PatternPageUtilInternal.setMethodValues(pattern, methods, valueWidgets, req, am, nameService);
		}
		
	}
	
	private Field getField(String name) {
		for (Field field: fields) {
			if (field.getName().equals(name))
				return field;
		}
		return null;
	}
	
	private Method getMethod(String name) {
		for (Method method: methods) {
			if (method.getName().equals(name)) 
				return method;
		}
		return null;
	}
	
	private void setInitialDropdownStatus() {
		for (OgemaWidgetBase<?> widget: valueWidgets.values()) {
			if (widget instanceof ControllableDropdown) {
				((ControllableDropdown) widget).setInitialActiveStatus(false);
			}
		}
	}
	
	private static Method[] getAnnotatedMethods(Class<? extends ResourcePattern<?>> pattern) {
		Method[] methods = pattern.getDeclaredMethods();
		List<Method> methodList = new ArrayList<Method>();
		for (Method method:methods) {
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
	
	/**
	 * @return
	 * 		the submit button, by means of which changes in the selected pattern are saved.
	 */
	public Button getSubmitButton() {
		return createButton;
	}

	/**
	 * @return
	 * 		the dropdown widget that offers the user the available patterns for selection.
	 */
	public PatternSelector<P> getSelector() {
		return select;
	}
	
	
}
