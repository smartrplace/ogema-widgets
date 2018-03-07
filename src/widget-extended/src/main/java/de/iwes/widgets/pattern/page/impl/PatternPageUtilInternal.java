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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.ValueResource;
import org.ogema.core.model.array.ArrayResource;
import org.ogema.core.model.array.BooleanArrayResource;
import org.ogema.core.model.array.ByteArrayResource;
import org.ogema.core.model.array.FloatArrayResource;
import org.ogema.core.model.array.IntegerArrayResource;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.array.TimeArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.resourcemanager.ResourceNotFoundException;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePattern.CreateMode;
import org.ogema.core.resourcemanager.pattern.ResourcePattern.Existence;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.multiselect.Multiselect;
import de.iwes.widgets.pattern.widget.dropdown.PatternDropdown;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.DefaultValue;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.DisplayValue;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.Entry;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.EntryType;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.FilterString;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.FilterValueFloat;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.FilterValueLong;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.NamingPolicy;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.PreferredName;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.ReferenceRestriction;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.SetValue;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.StringConversion;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.Unmodifiable;

public class PatternPageUtilInternal {

	/** throws various exceptions, which will be displayed on UI */
	public static void applyInternalAndExternalFilters(Map<String,Class<?>> fieldTypes, Map<String,OgemaWidgetBase<?>> valueWidgets, 
				Map<String,String> externalFilters, Map<String,FilterRange> externalRangeFilters, OgemaHttpRequest req) {
		OgemaLocale locale = req.getLocale();
		for (Map.Entry<String, Class<?>> entries : fieldTypes.entrySet()) {
			String id = entries.getKey();
			Class<?> type = entries.getValue();
			if (type.equals(Boolean.class)) {
				continue; // in this case one can only select from predefined options anyway
			}
			OgemaWidgetBase<?> widget = valueWidgets.get(id);
			if (!(widget instanceof TextField)) // unmodifiable fields // TODO catch empty Dropdowns/Multiselects for non-optional fields or context entries
				continue;
			TextField tf = (TextField) widget; 
			String value = tf.getValue(req);
			FilterRange filterRange = externalRangeFilters.get(id);
			if (type.equals(Float.class)) {
				float val;
				try {
					val = Float.parseFloat(value);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(LocalisationUtil.filterMsgNotAFloatingPointNr(id, locale));
				}
				if (filterRange instanceof LongRange) {
					long lowerBound = ((LongRange) filterRange).getLowerValue();
					long upperBound = ((LongRange) filterRange).getUpperValue();
					if (lowerBound != Long.MIN_VALUE && val < lowerBound) throw new IllegalArgumentException(LocalisationUtil.valueTooSmall(id, val, lowerBound, locale));
					if (upperBound != Long.MAX_VALUE && val > upperBound) throw new IllegalArgumentException(LocalisationUtil.valueTooLarge(id, val, upperBound, locale));	
				} else if (filterRange instanceof FloatRange) {
					float lowerBound = ((FloatRange) filterRange).getLowerValue();
					float upperBound = ((FloatRange) filterRange).getUpperValue();
					if (!Float.isNaN(lowerBound)) {
						boolean violated = ((FloatRange) filterRange).isLowerIncluded() ? val < lowerBound : val <= lowerBound;
						if (violated) throw new IllegalArgumentException(LocalisationUtil.valueTooSmall(id, val, lowerBound, locale));
					}
					if (!Float.isNaN(upperBound)) {
						boolean violated = ((FloatRange) filterRange).isUpperIncluded() ? val > upperBound : val >= upperBound;
						if (violated) throw new IllegalArgumentException(LocalisationUtil.valueTooLarge(id, val, upperBound, locale));
					}
				}

			}
			else if (type.equals(Integer.class) || type.equals(Long.class)) {
				long val;
				try {
					val = Long.parseLong(value);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(LocalisationUtil.filterMsgNotAnInteger(id, locale));
				}
				if (filterRange instanceof LongRange) {
					long lowerBound = ((LongRange) filterRange).getLowerValue();
					long upperBound = ((LongRange) filterRange).getUpperValue();
					if (val < lowerBound) throw new IllegalArgumentException(LocalisationUtil.valueTooSmall(id, val, lowerBound, locale));
					if (val > upperBound) throw new IllegalArgumentException(LocalisationUtil.valueTooLarge(id, val, upperBound, locale));	
				}
			} else if(type.equals(Byte.class)) {
				// check for valid HEX string
				try {
					DatatypeConverter.parseHexBinary(value);
				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException(LocalisationUtil.filterMsgNotAHexValue(id, locale));
				}
			}
			
			String externalFilter = externalFilters.get(id);
			if (externalFilter != null && !value.matches(externalFilter)) {
				throw new IllegalArgumentException(LocalisationUtil.filterMsgExternal(id, externalFilter, locale));
			}
		}
	}
	
	/**
	 * Set resource values, when user clicks Save button
	 * @return
	 * 		null, if no exceptions occured.
	 * 		if there were some exceptions setting any fields, they are reported in the return value.
	 * 		Map<field name, exception msg>
	 */
	public static <P extends ResourcePattern<? extends Resource>> Map<String,String> setValues(P newPattern, Field[] fields,
			Map<String,Boolean> references, Map<String,OgemaWidgetBase<?>> valueWidgets, OgemaHttpRequest req, ApplicationManager am) {
		Boolean[] targetState = FieldUtils.setAccessiblePrivileged(fields);
		Map<String,String> exceptions = null; 
		try {
			for (Field field : fields) {
				try {
					if (!Resource.class.isAssignableFrom(field.getType())) continue;   
					Resource res = (Resource) field.get(newPattern);
					String name = field.getName();
					Boolean reference = references.get(name);
					OgemaWidgetBase<?> widget = valueWidgets.get(name);
					if (widget == null  || widget instanceof Label) continue; // unmodifiable fields
					if (!reference) {
						// we do not want to create optional String fields for which no value is set
						if (!res.isActive() && res instanceof StringResource && widget instanceof TextField && ((TextField) widget).getValue(req).trim().isEmpty())
							continue;
						res.create(); // required for optional fields
						setValue(res,widget,req,am.getResourceAccess());
					}
					else if (!(widget instanceof ResourceLocationLabel)){ // filter out ReadOnly fields
						 setReference(res,widget,req, am);
					}
				} catch (Exception e) {
					am.getLogger().error("A pattern value could not be set",e);
					// this is problematic; if there is an exception in some resource, all the others won't be dealt with any more, either.
//					throw new RuntimeException(LocalisationUtil.errorSettingValue(field.getName(), req.getLocale()), e);
					if (exceptions == null)
						exceptions = new HashMap<String, String>();
					exceptions.put(field.getName(), LocalisationUtil.errorSettingValue(field.getName(), req.getLocale()) + ", " + e.getMessage());
				}
			}
		} finally {
			FieldUtils.setUnaccessiblePrivileged(fields, targetState);
		}
		return exceptions;
	}
	
	public static <P extends ResourcePattern<? extends Resource>> void setValues(Class<P> patternType, P newPattern, Method[] methods,
			Map<String,OgemaWidgetBase<?>> valueWidgets, OgemaHttpRequest req, ApplicationManager am) {
		FieldUtils.setAccessiblePrivileged(methods);
		for (Method method: methods) {
			SetValue sv = method.getAnnotation(SetValue.class);
			if (sv == null) continue;
			String targetMethod  = sv.target();
			if (targetMethod == null || targetMethod.isEmpty())
				targetMethod = method.getName();
			OgemaWidgetBase<?> widget = valueWidgets.get(targetMethod);
			if (!(widget instanceof TextField)) continue; // should not be necessary
			String value = ((TextField) widget).getValue(req);
			try {
				method.invoke(newPattern, value);
			} catch (Exception e) {
				am.getLogger().warn("Could not invoke pattern method " + method.getName(),e);
			}
		}
	}
	
	public static <P extends ResourcePattern<? extends Resource>> void setMethodValues(P newPattern, Method[] methods,
			Map<String,OgemaWidgetBase<?>> valueWidgets, OgemaHttpRequest req, ApplicationManager am, NameService nameService) {
		Boolean[] targetState = FieldUtils.setAccessiblePrivileged(methods); 
		try {
			for (Method method: methods) {
				try {
					DisplayValue dv = method.getAnnotation(DisplayValue.class);
					if (dv == null) continue;
					String name = method.getName();
					OgemaWidgetBase<?> widget = valueWidgets.get(name);
					if (widget instanceof TriggerableMethodLabel) {
						((TriggerableMethodLabel) widget).setPattern(newPattern,req);
						continue;
					}
					Object result = method.invoke(newPattern);
					StringConversion sc = getConversionMethod(method); 
					setStaticValue(widget,result, sc, nameService, req);
				} catch (Exception e) {
					am.getLogger().error("A pattern value could not be set",e);
					throw new RuntimeException(LocalisationUtil.errorSettingValue(method.getName(), req.getLocale()), e);
				}
			}
		} finally {
			FieldUtils.setUnaccessiblePrivileged(methods, targetState); 
		}
	}
	
	private static StringConversion getConversionMethod(Method method) {
		DisplayValue dv = method.getAnnotation(DisplayValue.class);
		if (dv == null) return StringConversion.TO_STRING;
		return dv.stringConversion();		
	}
	
	@SuppressWarnings("unchecked")
	private static void setStaticValue(OgemaWidgetBase<?> widget, Object value, StringConversion conversionMethod, 
				NameService nameService, OgemaHttpRequest req) {
		if (!(widget instanceof Label) && !(widget instanceof TextField)) {
			LoggerFactory.getLogger(PatternPageUtilInternal.class).error("A static pattern entry (method) is assigned a non-Label widget");
			return;
		}
//		Label label = (Label) widget;
		String val = null;
		switch (conversionMethod) {
		case TO_STRING:
			val = value.toString();
			break;
		case NAME_SERVICE:
			if (value instanceof Resource) {
				Resource resource = (Resource) value;
				val = nameService.getName(resource, req.getLocale(), true, true);
				if (val == null) val = resource.getLocation();
			} else if (value instanceof Class) { // expecting Class<? extends Resource>, otherwise the simple class name is returned
				@SuppressWarnings("rawtypes")
				Class clazz = (Class) value;
				try {
					val = nameService.getName(clazz, req.getLocale(), true);
				} catch (Exception e) {
				}
				if (val == null) val = clazz.getSimpleName();
			} else {
				LoggerFactory.getLogger(PatternPageUtilInternal.class).error
					("Method return type " + value.getClass().getSimpleName() + " not compatible with StringConversion.NAME_SERVICE");
				return;
			}		
			break;
		case RESOURCE_PATH:
			if (!(value instanceof Resource)) {
				LoggerFactory.getLogger(PatternPageUtilInternal.class).error
					("Method return type " + value.getClass().getSimpleName() + " not compatible with StringConversion.RESOURCE_PATH");
				return;
			}
			Resource resource = (Resource) value;
			val = resource.getPath();
			break;
		default:
			LoggerFactory.getLogger(PatternPageUtilInternal.class).error("String conversion method " + conversionMethod + " not yet implemented");			
			return;
		}
		if (widget instanceof Label)
			((Label) widget).setText(val, req);
		else if (widget instanceof TextField)
			((TextField) widget).setValue(val, req);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setValue(final Resource res,OgemaWidgetBase<?> widget, OgemaHttpRequest req, final ResourceAccess ra) {
		if (widget instanceof TextField && (res instanceof SingleValueResource || res instanceof ByteArrayResource)) {
			String text= ((TextField) widget).getValue(req);
			setValue((ValueResource) res,text);
		}
		else if (widget instanceof EnumDropdown && res instanceof SingleValueResource) {
			EnumDropdown ed = (EnumDropdown) widget;
			String text = ed.getSelectedValue(req);		
			Class<? extends Enum> enType = ed.getEnumType();
			Method map = null;
			try {
				if (text != null && !text.isEmpty()) {
					map = enType.getMethod("map", new Class<?>[]{});  // privileged call required?
					// read selected enum value and apply the map method to it, to obtain the desired resource value
					if (map != null) {
						Enum en = Enum.valueOf(enType, text);
					    text = map.invoke(en).toString();
					}
				}
			} catch (Exception e) {}
			
			if (text == null) text = "";
			setValue((SingleValueResource) res,text);
		}
		else if (widget instanceof BooleanDropdown && res instanceof BooleanResource) {
			boolean status = Boolean.parseBoolean(((BooleanDropdown) widget).getSelected(req).id().toLowerCase());
			((BooleanResource) res).setValue(status);
		}
		else if (widget instanceof Datepicker && res instanceof TimeResource) {
			long date = ((Datepicker) widget).getDateLong(req);
			((TimeResource) res).setValue(date);
		}
		else if (widget instanceof Multiselect && res instanceof ResourceList<?>) {
			Collection<DropdownOption> selected = (( Multiselect) widget).getSelected(req);
			Class<? extends Resource> clazz = ((ResourceList) res).getElementType();
			List<? extends Resource> oldValues = res.getSubResources(clazz, false);
			final Set<String> newValues = new LinkedHashSet<String>();
			Iterator<DropdownOption> it = selected.iterator();
			while (it.hasNext()) {
				DropdownOption opt  =it.next();
				String path = opt.getValue();
				boolean contained = isResourceContained((List<Resource>) oldValues,path);
				if (!contained)
					newValues.add(path);
			}
			Iterator<? extends Resource> itRes = oldValues.iterator();
			while (itRes.hasNext()) {
				Resource rs = itRes.next();
				boolean contained = isResourceSelected(selected,rs);
				if (!contained) {
					((ResourceList) res).remove(ra.getResource(rs.getLocation())); 
				}
			}
			if (!newValues.isEmpty()) {
				// FIXME privileged execution probably not required any more (tbc)
				AccessController.doPrivileged(new PrivilegedAction<Void>() {
	
					@Override
					public Void run() {
						for (String path: newValues) {
							final Resource newRes = ra.getResource(path);
							if (newRes == null) continue;
							try {
	//							((ResourceList) res).add(newRes);  // FIXME NoSuchMethodError at runtime; with reflections it works -> ??
								Method addMethod = ResourceList.class.getMethod("add", new Class<?>[]{Resource.class});
								addMethod.invoke(res, newRes);
							} catch  (Exception e) {
								e.printStackTrace();
							}
						}
						return null;
					}
				});
			}
		}
		else if (widget instanceof EnumMultiselect && res instanceof ArrayResource) {
			Collection<String> labels = ((EnumMultiselect) widget).getSelectedValues(req);
			try {
				setValues((ArrayResource) res, labels);
			} catch (Exception e) {
				LoggerFactory.getLogger(PatternPageUtilInternal.class).error("Could not set ArrayResource values",e);
			}
		}
		else 
			throw new IllegalArgumentException("Widget/Value pair could not be handled... resource " + res + "; widget type: " + widget.getClass().getSimpleName());
	}
	
	private static void setValues(ArrayResource array, Collection<String> selected) {
		int counter= 0;
		if (array instanceof IntegerArrayResource) {
			int[] arr = new int[selected.size()];
			for (String sel: selected) {
				arr[counter++] = Integer.parseInt(sel); // TODO exceptions? 
			}
			((IntegerArrayResource) array).setValues(arr);
		}
		else if (array instanceof StringArrayResource) {
			String[] arr = new String[selected.size()];
			for (String sel: selected) {
				arr[counter++] = sel; // TODO exceptions? 
			}
			((StringArrayResource) array).setValues(arr);
		}
		else if (array instanceof FloatArrayResource) {
			float[] arr = new float[selected.size()];
			for (String sel: selected) {
				arr[counter++] = Float.parseFloat(sel); // TODO exceptions? 
			}
			((FloatArrayResource) array).setValues(arr);
		}
		else if (array instanceof BooleanArrayResource) {
			boolean[] arr = new boolean[selected.size()];
			for (String sel: selected) {
				arr[counter++] = Boolean.parseBoolean(sel); // TODO exceptions? 
			}
			((BooleanArrayResource) array).setValues(arr);
		}
		else if (array instanceof TimeArrayResource) {
			long[] arr = new long[selected.size()];
			for (String sel: selected) {
				arr[counter++] = Long.parseLong(sel); // TODO exceptions? 
			}
			((TimeArrayResource) array).setValues(arr);
		}
		
	}
	 
	private static void setValue(ValueResource svr, String text) {
		if (svr instanceof StringResource) {
			((StringResource) svr).setValue(text);
		}
		else if (svr instanceof FloatResource) {
			((FloatResource) svr).setValue(Float.parseFloat(text));
		} else if (svr instanceof IntegerResource) {
			((IntegerResource) svr).setValue(Integer.parseInt(text));
		} else if (svr instanceof TimeResource) {
			((TimeResource) svr).setValue(Long.parseLong(text));
		} else if (svr instanceof BooleanResource) {
			((BooleanResource) svr).setValue(Boolean.parseBoolean(text));
		} else if (svr instanceof ByteArrayResource) {
			((ByteArrayResource) svr).setValues(DatatypeConverter.parseHexBinary(text));
		}
		else 
			throw new RuntimeException("Invalid resource type " + svr.getResourceType().getSimpleName());
	}
	
	private static void setReference(Resource res,OgemaWidgetBase<?> widget, OgemaHttpRequest req, ApplicationManager am) {
		if (!(widget instanceof ReferenceDropdown)) {
			throw new IllegalArgumentException("ReferenceDropdown widget expected");
		}
		@SuppressWarnings("rawtypes")
		ReferenceDropdown refWidget  = (ReferenceDropdown) widget;
		DropdownOption opt = refWidget.getSelected(req);
		if (opt == null)
			throw new IllegalArgumentException("Please select an item for " + widget.getId()); 
		String path = opt.id();
		if (path.equals(DropdownData.EMPTY_OPT_ID)) {
			if (res.exists() && res.isReference(false)) {
				res.delete(); // remove reference 
			}

			return;
		}
		Resource target  = am.getResourceAccess().getResource(path);
		if (target == null)
			throw new ResourceNotFoundException("Resource " + path + " not found");
		res.setAsReference(target);
	}
	
	/*
	 * methods only required for EditPage, can be null or empty for PageCreator
	 * mainSelector: null for PageCreator
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <D extends LocaleDictionary> void createWidgets(final WidgetPage<D> page, String widgetID, Field[] fields, Method[] methods, Field[] contextFields, Map<String,OgemaWidgetBase<?>> labelWidgets, 
			Map<String,OgemaWidgetBase<?>> valueWidgets, Map<String,Boolean> references, Map<String,Class<?>> fieldTypes, Map<String,String> externalFilters, Map<String,FilterRange> externalRangeFilters,
			PatternDropdown<?> mainSelector,
			boolean setDefaults, final ApplicationManager am, final Class<D> dictionary, boolean forceEditable, boolean forceUnmodifiable) {
		if (forceEditable && forceUnmodifiable)
			throw new IllegalArgumentException("forceEditable and forceUnmodifiable must not both be true");
		// set method widgets first, then field widgets
		Boolean[] accessibleStates;
//		D dict = page.getDictionary("en"); // FIXME at this point the localisations are not registered yet;  
										   // need to generate labels dynamically anyways...
		if (methods != null && methods.length > 0) {
			accessibleStates = FieldUtils.setAccessiblePrivileged(methods);
			try {
				List<String> referencedMethods = getReferencedMethods(methods);
				DisplayValue dv;
				SetValue sv;
				for (Method method: methods) {
					dv = method.getAnnotation(DisplayValue.class);
					sv = method.getAnnotation(SetValue.class);
					if (dv == null && sv == null) continue; // should not happen; a selection has taken place previously; note: this filters out
								// methods with a @SetValue annotation
					final String id = method.getName();
					Class returnType = method.getReturnType();
					OgemaWidgetBase<?> methodLabel;
					OgemaWidgetBase<?> methodValue;
					if (dv != null && dv.useTriggerButton() && !referencedMethods.contains(id)) {
						methodValue = new TriggerableMethodLabel(page, widgetID + "_" + id + "_valueWidget", method);
						methodLabel = new MethodTriggerButton<D>(page, widgetID + "_" +  id + "_labelWidget", id, (TriggerableMethodLabel) methodValue, dictionary);
						methodLabel.triggerAction(methodValue, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
					}
					else {
						if (dv != null) {
							StringConversion sc = dv.stringConversion();
							if (sc == StringConversion.NAME_SERVICE) {
								
								if (!Resource.class.isAssignableFrom(returnType) && !Class.class.isAssignableFrom(returnType)) {
									am.getLogger().error("Method return type of " + id + " not applicable to NameService");
									continue;
								}
							}			
							if (referencedMethods.contains(id))
								methodValue = new TextField(page, widgetID + "_" + id + "_valueWidget", "");
							else
								methodValue = new Label(page, widgetID + "_" + id + "_valueWidget", "");
						}
						else { //if (sv != null) {
							String target = sv.target();
							if (hasReferencedMethod(method, methods)) continue;
							methodValue = new TextField(page, widgetID + "_" + id + "_valueWidget","");
						}

						methodLabel = new PatternEntryLabel<D>(page,widgetID + "_" +  id + "_labelWidget",id,dictionary);
					}
					labelWidgets.put(id, methodLabel);
					valueWidgets.put(id, methodValue);
				}
			} finally {
				FieldUtils.setUnaccessiblePrivileged(methods, accessibleStates);  	
			}
		}
		// Map<Field, is context field?>
		final Map<Field, Boolean> allFields = new LinkedHashMap<>();
		if (contextFields != null) {
			FieldUtils.setAccessiblePrivileged(contextFields);
			for (Field f: contextFields) {
				allFields.put(f, true);
			}
		}
		for (Field f: fields) {
			allFields.put(f, false);
		}
		
		// field widgets
		accessibleStates = FieldUtils.setAccessiblePrivileged(fields);
		try {
			Class<?> clazz;
			Entry entry;
			EntryType type;
			String id;
			OgemaWidgetBase<?> value;
			Label label;
			boolean editable;
			Unmodifiable unmod;
			Field field;
			for (Map.Entry<Field, Boolean> mentry: allFields.entrySet()) {
				field = mentry.getKey();
				clazz = field.getType();
				if (!Resource.class.isAssignableFrom(clazz) && !mentry.getValue())
					continue;
				entry  = field.getAnnotation(Entry.class);
				if (entry == null && !ValueResource.class.isAssignableFrom(clazz) && !ResourceList.class.isAssignableFrom(clazz)) continue; // default for complex resources: not shown
				else if (entry != null && !entry.show()) continue;
				type = field.getAnnotation(EntryType.class);
				boolean reference;
				boolean rawData = false;
				if (type == null) {		// use default setting
					if (ValueResource.class.isAssignableFrom(clazz) || ResourceList.class.isAssignableFrom(clazz)) 
						reference = false;
					else
						reference = true;
				}
				else {
					reference = type.setAsReference();  // FIXME does it makes sense for complex resources not to set them as references?
					rawData = type.rawData();
				}
				
				id = field.getName();
				value = null;
				editable = true;
				if (forceUnmodifiable)
					editable = false;
				else if (!forceEditable) {
					unmod = field.getAnnotation(Unmodifiable.class);
					if (unmod != null)
						editable = false;
				}
				if ((!reference && ValueResource.class.isAssignableFrom(clazz)) || !Resource.class.isAssignableFrom(clazz)) { // the latter case covers the context widgets 
					String defaultString = null;
					if (setDefaults) {
						DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
						if (defaultValue != null)
							defaultString = defaultValue.value();
					}
					Class<? extends Enum> enumType = null;
					if (type != null) 
						enumType = type.enumType();
					if (enumType != null && !enumType.equals(Enum.class)) {
						if (SingleValueResource.class.isAssignableFrom(clazz) || ByteArrayResource.class.isAssignableFrom(clazz)) {
							value = new EnumDropdown(page, widgetID + "_" + id, enumType);
							if (defaultString != null && ((EnumDropdown) value).getValidOptions().contains(defaultString)) 
								((EnumDropdown) value).selectDefault(defaultString);
						}
						else { // ArrayResource
							value = new EnumMultiselect(page,  widgetID + "_" + id, enumType);
							// TODO select defaults based on defaultString
						}
					}
					else 
						value = createSimpleWidget(page, (Class<? extends ValueResource>) clazz, id, defaultString, editable, widgetID, rawData);
					references.put(id, false);
					setType(id,clazz, fieldTypes);
					FilterString filter = field.getAnnotation(FilterString.class);
					if (filter != null) 
						externalFilters.put(id, filter.regexp());
					FilterValueLong filterValue = field.getAnnotation(FilterValueLong.class);
					if (filterValue != null) 
						externalRangeFilters.put(id, new LongRange(filterValue.lowerBound(), filterValue.upperBound()));
					FilterValueFloat filterValueFloat = field.getAnnotation(FilterValueFloat.class);
					if (filterValueFloat != null) 
						externalRangeFilters.put(id, new FloatRange(filterValueFloat.lowerBound(), filterValueFloat.upperBound(), filterValueFloat.includeLowerBoundary(), filterValueFloat.includeUpperBoundary()));
				}
				else if (!reference && ResourceList.class.isAssignableFrom(clazz)) {
					ReferenceRestriction restr = field.getAnnotation(ReferenceRestriction.class);
					if (restr == null) continue; // cannot infer type of the ResourceList
					Class<? extends ResourcePattern<?>> targetPattern  =restr.targetPattern();
					NamingPolicy naming = field.getAnnotation(NamingPolicy.class);
					PreferredName preferredName;
					if (naming != null) 
						preferredName = naming.policy();
					else
						preferredName = PreferredName.USER_GIVEN_NAME;
					value = new ResourceListMultiselect(page, widgetID + "_" + id, targetPattern, preferredName, am);
					references.put(id, false);
					setType(id,clazz, fieldTypes);
				}
				else if (reference) {
					ReferenceRestriction restr = field.getAnnotation(ReferenceRestriction.class);
					Class<? extends ResourcePattern> targetRestr = null;
					if (restr != null) {
						targetRestr = restr.targetPattern();
					}
					NamingPolicy naming = field.getAnnotation(NamingPolicy.class);
					PreferredName preferredName;
					if (naming != null) 
						preferredName = naming.policy();
					else
						preferredName = PreferredName.USER_GIVEN_NAME;
					Existence existence = field.getAnnotation(Existence.class);
					boolean required = true;
					if (existence != null && existence.required() == CreateMode.OPTIONAL) 
						required = false;
					value = createComplexWidget(page,(Class<? extends Resource>) clazz, targetRestr, mainSelector, field, id, preferredName, required, am, editable, widgetID);
					references.put(id, true);
						
				}
				// TODO others
				
				if (value != null) {
//					Label label = new Label(page, id + "_label",true);
					label = new PatternEntryLabel<D>(page, id + "_label", id, dictionary);
					labelWidgets.put(id, label);		
					valueWidgets.put(id, value);
				}
			}		
		} finally {
			FieldUtils.setUnaccessiblePrivileged(fields, accessibleStates);
		}		
		
	}
	
	private static List<String> getReferencedMethods(Method[] methods) {
		List<String> list = new ArrayList<String>();
		for (Method method: methods) {
			SetValue sv = method.getAnnotation(SetValue.class);
			if (sv ==null) continue;
			String target = sv.target();
			if (target != null && !target.isEmpty()) 
				list.add(target);
			else
				list.add(method.getName()); // use the method's name as default
		}
		return list;
	}
	
	private static boolean hasReferencedMethod(Method method, Method[] methods) {
		SetValue sv = method.getAnnotation(SetValue.class);
		if (sv ==null) return false;
		String target = sv.target();
		if (target == null || target.isEmpty()) 
			target = method.getName();
		for (Method mthd : methods) {
			if (mthd.equals(method)) continue;
			if (mthd.getName().equals(target) && mthd.getParameterTypes().length == 0) 
				return true;
		}
		return false;
	}
	
	private static OgemaWidgetBase<?> createSimpleWidget(WidgetPage<?> page, Class<? extends ValueResource> type, String name, String defaultValue, boolean editable, String widgetID, boolean rawData) {
		if (editable && BooleanResource.class.isAssignableFrom(type)) { // TODO implement editable for booleanREsource
			BooleanDropdown dd = new BooleanDropdown(page, widgetID + "_" + name, defaultValue);
			// TODO set default value
			return dd;
		}
		else if (TimeResource.class.isAssignableFrom(type) && !rawData) { // TODO !editable
			Datepicker dp = new Datepicker(page, widgetID + "_" + name);
			dp.setDefaultDate(defaultValue); // TODO
			return dp;
		}
		else {
			if (editable) {
				TextField tf =  new TextField(page,widgetID + "_" +  name);
				if (defaultValue != null) { // TODO filter for admissible values?
					tf.setDefaultValue(defaultValue);
				}
				return tf;
			}
			else { // TODO ensure this can be handled in POST of selector
				Label lab = new Label(page,widgetID + "_" +  name);
				return lab;
			}
		}
	}
	
	// patternType may be null
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <R extends Resource> OgemaWidgetBase<?> createComplexWidget(WidgetPage<?> page, Class<? extends Resource> clazz, Class<? extends ResourcePattern> targetRestr,
			PatternDropdown<?> mainSelector, Field targetField,
			String name, PreferredName preferredName, boolean required, ApplicationManager am, boolean editable, String widgetID) { 
		if (editable) {
			Dropdown referencesDropdown = new ReferenceDropdown(page,widgetID + "_" +  name, clazz, preferredName, am, mainSelector, targetField, !required, targetRestr);
			return referencesDropdown;
		}
		else {
			Label label = new ResourceLocationLabel(page,widgetID + "_" + name, preferredName, am.getResourceAccess());
			return label;
		}
	}
	
	private static void setType(String id, Class<?> clazz, Map<String,Class<?>> fieldTypes) {
//		if (!SingleValueResource.class.isAssignableFrom(clazz))
//			return;
		if (StringResource.class.isAssignableFrom(clazz))
			fieldTypes.put(id, String.class);
		else if (IntegerResource.class.isAssignableFrom(clazz))
			fieldTypes.put(id, Integer.class);
		else if (FloatResource.class.isAssignableFrom(clazz))
			fieldTypes.put(id, Float.class);
		else if (TimeResource.class.isAssignableFrom(clazz))
			fieldTypes.put(id, Long.class);
		else if (BooleanResource.class.isAssignableFrom(clazz))
			fieldTypes.put(id, Boolean.class);
		else if (ResourceList.class.isAssignableFrom(clazz))
			fieldTypes.put(id, List.class);
		else if (ByteArrayResource.class.isAssignableFrom(clazz))
			fieldTypes.put(id, Byte.class);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setWidgetValue(OgemaWidgetBase<?> widget, Resource resource, OgemaHttpRequest req) {
		if (widget instanceof ControllableDropdown) {
			((ControllableDropdown) widget).setActive(true, req);
		}		
		if (widget instanceof ResourceLocationLabel) { // read-only reference field
			((ResourceLocationLabel) widget).setValue(resource, req);
		}
		else if (widget instanceof Datepicker) {
			((Datepicker) widget).setDate(((TimeResource) resource).getValue(), req);
		}
		else if ((widget instanceof TextField || widget instanceof Label || widget instanceof EnumDropdown) && (resource instanceof SingleValueResource || resource instanceof ByteArrayResource) ) { 
			setWidgetValueSimple(widget,(ValueResource) resource,req);
		}
		else if  (widget instanceof BooleanDropdown && resource instanceof BooleanResource) {
			boolean bool = ((BooleanResource) resource).getValue();
			((BooleanDropdown) widget).selectSingleOption(bool ? "TRUE" : "FALSE", req);
		}
		else if (widget instanceof ReferenceDropdown<?>) {
			ReferenceDropdown<?> dd = (ReferenceDropdown<?>) widget;
			if (!resource.isActive()) {
				dd.selectSingleOption(DropdownData.EMPTY_OPT_ID, req);
			}
			else {
				String loc = resource.getLocation();
				dd.selectSingleOption(loc, req);
			}
		}
		else if (widget instanceof ResourceListMultiselect<?> && resource instanceof ResourceList<?>) {
			((ResourceListMultiselect) widget).setActive(true, req);
			setWidgetValueMulti((ResourceListMultiselect) widget,(ResourceList) resource, req);
		}
		else if (widget instanceof EnumMultiselect && resource instanceof ArrayResource) {
			((EnumMultiselect) widget).setActive(true, req);
			setWidgetValueArray((EnumMultiselect) widget, (ArrayResource) resource,req);
		}
	}
	
	private static List<String> getArrayValues(ArrayResource resource) {
		List<String> values = new ArrayList<String>();
		if (resource instanceof IntegerArrayResource) {
			int[] vals = ((IntegerArrayResource) resource).getValues();
			for (int val: vals) {
				values.add(String.valueOf(val));
			}
		}
		else if (resource instanceof StringArrayResource) {
			String[] vals = ((StringArrayResource) resource).getValues();
			for (String val: vals) {
				values.add(val);
			}
		}
		else if (resource instanceof FloatArrayResource) {
			float[] vals = ((FloatArrayResource) resource).getValues();
			for (float val: vals) {
				values.add(String.valueOf(val));
			}
		}
		else if (resource instanceof BooleanArrayResource) {
			boolean[] vals = ((BooleanArrayResource) resource).getValues();
			for (boolean val: vals) {
				values.add(String.valueOf(val));
			}
		}
		else if (resource instanceof TimeArrayResource) {
			long[] vals = ((TimeArrayResource) resource).getValues();
			for (long val: vals) {
				values.add(String.valueOf(val));
			}
		}
		return values;
	}
	
	private static void setWidgetValueArray(EnumMultiselect widget, ArrayResource resource, OgemaHttpRequest req) {
//		widget.setSelectedValues(getArrayValues(resource), req);
		widget.selectMultipleOptions(getArrayValues(resource), req);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setWidgetValueSimple(OgemaWidgetBase<?> widget,ValueResource res, OgemaHttpRequest req) {
		String value;
		if (res.isActive())
			value = getResourceValueSimple(res);
		else 
			value = "";
		if (widget instanceof TextField)
			((TextField) widget).setValue(value, req);
		else if (widget instanceof Label) 
			((Label) widget).setText(value, req);
		else if (widget instanceof EnumDropdown) {
			EnumDropdown ed = (EnumDropdown) widget;
			Class<? extends Enum> enType = ed.getEnumType();
			Method map = null;
//			Class<?> returnType = null;
			try {
				if (value != null && !value.isEmpty()) {
					map = enType.getMethod("map", new Class<?>[]{});  // privileged call required?
//					returnType = map.getReturnType();
					// read widget value and try to find a corresponding enum that is mapped to this value
					if (map != null)
						value = getEnumNameForMapResult(value, map, enType.getEnumConstants());
				}
			} catch (Exception e) {}
			// TODO remove
				// this is the inverse method, required to set the widget value
//				try {
//					Enum selected = Enum.valueOf(enType, value);
//					Object obj = map.invoke(selected)
//				} catch (Exception e) {
//					
//				}

			
			if (!ed.getValidOptions().contains(value)) 
				LoggerFactory.getLogger(PatternPageUtilInternal.class).warn("Resource value " + value 
						+ " does not correspond to enum type " + ed.getEnumType().getSimpleName());	
			else 
				ed.selectSingleOption(value, req);
		}
		else 
			throw new IllegalArgumentException("Unsupported widget type + " + widget.getWidgetClass());
	}
	
	@SuppressWarnings("rawtypes")
	private static String getEnumNameForMapResult(String widgetValueAsString, Method map, Enum[] enums) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Enum en: enums) {
			String mapResult = map.invoke(en).toString();
			if (mapResult != null && mapResult.equals(widgetValueAsString))
				return en.name();
		}
		return null;
	}
	
	private static String getResourceValueSimple(ValueResource res) {
		String value;
		if (res instanceof StringResource)
			value = ((StringResource) res).getValue();
		else if (res instanceof IntegerResource) 
			value = String.valueOf(((IntegerResource) res).getValue());
		else if (res instanceof FloatResource) 
			value = String.valueOf(((FloatResource) res).getValue());
		else if (res instanceof TimeResource) 
			value = String.valueOf(((TimeResource) res).getValue());
		else if (res instanceof BooleanResource) 
			value = String.valueOf(((BooleanResource) res).getValue());
		else if (res instanceof ByteArrayResource)
			value = DatatypeConverter.printHexBinary(((ByteArrayResource) res).getValues()); 
		else throw new IllegalArgumentException("Unexpected resource type");
		return value;
	}
	
	private static void setWidgetValueMulti(ResourceListMultiselect<?> widget,ResourceList<? extends Resource> list, OgemaHttpRequest req) {
		Set<String> selectedOptions = new LinkedHashSet<String>();
		List<? extends Resource> entries = list.getAllElements();
		for (Resource res :entries) {
			selectedOptions.add(res.getLocation());
		}
		widget.selectMultipleOptions(selectedOptions, req);
	}
	
	public static void clearWidgets(Map<String,OgemaWidgetBase<?>> widgets, OgemaHttpRequest req) {
		for (OgemaWidgetBase<?> widget: widgets.values()) {
			if (widget instanceof TextField) {
				((TextField) widget).setValue("", req);
			}
			else if (widget instanceof ReferenceDropdown) { 
				((ReferenceDropdown<?>) widget).selectSingleOption(DropdownData.EMPTY_OPT_ID, req);
			}
			else if (widget instanceof ControllableDropdown) {
				((ControllableDropdown) widget).setActive(false, req);
			}
			else if (widget instanceof ResourceListMultiselect<?>) {
				((ResourceListMultiselect<?>) widget).setActive(false, req);
			}
			else if (widget instanceof TriggerableMethodLabel) {
				((TriggerableMethodLabel) widget).setMessage("", req);
				((TriggerableMethodLabel) widget).setPattern(null, req);
			}
			else if (widget instanceof Label) {
				((Label) widget).setText("", req);
			}
			else if (widget instanceof Datepicker) {
				((Datepicker) widget).setDate("", req);
			}
			else if (widget instanceof EnumMultiselect) {
				((EnumMultiselect) widget).setActive(false, req);
			}
		}
	}
	
	private static boolean isResourceContained(List<Resource> list,String path) {
		for (Resource res: list) {
			if (res.getPath().equals(path) || res.getLocation().equals(path)) 
				return true;
		}
		return false;
		
	}
	
	private static boolean isResourceSelected(Collection<DropdownOption> list,Resource res) {
		for (DropdownOption opt: list) {
			String path = opt.id();
			if (res.getPath().equals(path) || res.getLocation().equals(path)) 
				return true;
		}
		return false;
		
	}
		
}
