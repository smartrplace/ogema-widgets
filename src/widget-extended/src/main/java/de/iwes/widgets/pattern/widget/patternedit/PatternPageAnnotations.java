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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.services.NameService;

/**
 * This service allows to create basic user pages for creating and editing {@link ResourcePattern}s. It also specifies
 * a few additional Annotations for a ResourcePattern, which allow to configure the resulting pages.<br>
 * 
 * Inject the service into your application (must use a class which defines a Component annotation
 * itself for this): <br>
 * 
 * <code>
 * &#64;Reference
 * PatternPageUtil patternPageUtil;
 * </code>
 * 
 * or use BundleContext#getService(org.osgi.framework.ServiceReference).
 * 
 * @author cnoelle
 * 
 */
// TODO implement Unmodifiable annotation for booleanREsource and complex resources
public interface PatternPageAnnotations {
	
		
	/************* Annotations ***********/
	
	/**
	 * Additional annotation type for fields of a {@link ResourcePattern}. <br>
	 * If this annotation is not present, it defaults to true for SingleValueResources 
	 * and to false for complex resources.
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Entry {
		
		boolean	show() default true;
		
	}
	
	/**
	 * Additional annotation type for fields of a {@link ResourcePattern}. <br>
	 * If this annotation is not present, {@link #setAsReference()} defaults to false for SingleValueResources 
	 * and to true for complex resources. <br>
	 * Note that the combination @Entry(show=true) with @ValueType(setAsReference=false) does not make sense 
	 * for complex resources; they will be ignored in this case<br>
	 * 
	 * For StringResources, the field {@link #enumType()} specifies a set of allowed values in terms of enum constants.
	 * More generally, when {@link #enumType()} is applied to a {@link SingleValueResource} and the specified Enum class declares a 
	 * method <code>map()</code> (whose return type should match the primitive type corresponding to the resource type, e.g. 
	 * <code>int</code> for a <code>IntegerResource</code>), then the dropdown widget will display the enum constants, and the 
	 * resource value will be the results of applying the <code>map</code> method of the selected enum.
	 * This also works for ArrayResources. 
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface EntryType {
		
		boolean	setAsReference();
		
		@SuppressWarnings("rawtypes")
		Class<? extends Enum> enumType() default Enum.class;
		
		/**
		 * Mainly relevant to fields of type TimeResource. 
		 * If true, data will be displayed as a long value, 
		 * otherwise a datepicker is shown.
		 */
		boolean rawData() default false;
		
	}
	
	/**
	 * {@link #targetPattern()} must be compatible with the resource type of the annotated field.<br>
	 * For a ResourceList field (which is not itself set as a reference) this annotation is compulsory,
	 * for other fields which are set as reference it is optional.<br>
	 * Note that this annotation is only evaluated in case 
	 * &#64;EntryType(setAsReference=true) is also present (explicitly, or by default)
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ReferenceRestriction {
		
		Class<? extends ResourcePattern<?>> targetPattern();
		
	}
	
	public enum PreferredName {

		/**
		 * Uses the resource path as label. Should only be used for development puroposes.
		 */
		RESOURCE_PATH,
		/**
		 * Uses OGEMA name services for determining names, if available. The latter looks for a user given name 
		 * first (subresource <code>name</code>), if this is not available, it looks iteratively for a parent resource
		 * with a given name and a name for the relative path from the respective parent to the given resource (as a fallback
		 * it uses the relative path). If none of the above is successful, the name service returns null, in which case the resource 
		 * path is used as name.		 *  
		 */
		USER_GIVEN_NAME,
		/**
		 * Not implemented yet...
		 */
		CUSTOM_NAME
	}
	
	/**
	 * Defines the strategy for selecting names for resources in the dropdown that lets the user select a reference target for a resource.<br>
	 * This annotation is only evaluated if {@link EntryType#setAsReference()} is true (explicitly or by default).
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface NamingPolicy {
		
		PreferredName policy() default PreferredName.USER_GIVEN_NAME;
		
	}
	
	public enum StringConversion {
		
		/**
		 * Convert an arbitrary object to a String, by applying its {@link Object#toString()} method
		 */
		TO_STRING,
		
		/**
		 * Convert a resource type (i.e. a <code>Class&lt;? extends Resource&gt;</code>) or a {@link Resource} to
		 * a String by using the respective methods of a {@link NameService}.
		 */
		NAME_SERVICE,
		
		/**
		 * Only applicable to methods with return type {@link Resource}. Shows the OGEMA resource path. 
		 */
		RESOURCE_PATH
		
	}
	
	/**
	 * Apply this annotation to a method of a pattern, without any arguments,
	 * and the return value on the specified pattern will
	 * be displayed on the resulting pattern page. The method may return any object 
	 * if stringConversion = TO_STRING, the default value; otherwise any object of a type compatible with the
	 * selected {@link StringConversion} parameter is allowed.<br>
	 * <br>
	 * This annotation has no effect on a pattern create page, it is only relevant for edit pages. 
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DisplayValue {
		
		StringConversion stringConversion() default StringConversion.TO_STRING;
		
		/**
		 * Set to true in order to display a button that triggers execution of the method and 
		 * display of the resulting value. Otherwise the method will be executed on every 
		 * update of the selected pattern.
		 * @return
		 */
		boolean useTriggerButton() default false;

	}
	
	/**
	 * Apply this annotation to a void method with a String argument, in order to
	 * append a text field to the page that triggers the execution of the method. 
	 * The entered value will be passed as argument to the method.
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface SetValue {
		
		/**
		 * If another method exists with the specified name and a {@link DisplayValue}
		 * annotation, the text field will display the value specified by the referenced
		 * method, but allow the user to change the value.
		 * If empty or null, and another method with the same name, signature String and no arguments
		 * exists, which in addition has a {@link DisplayValue} annotation, then this other method 
		 * will be used to retrieve the values for the text field. Otherwise a separate text field 
		 * will be displayed on the page, which does not retrieve any values from the backend.
		 * 
		 * @return
		 */
		String target() default "";
		
	}
	
	/**
	 * Add a default value to a single value resource field of a create page. For edit pages this is ignored. 
	 * TODO add also to edit pages, for optional fields that were not yet present(?)
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface DefaultValue {
		
		String value() default "";
		
	}
	
	/**
	 * For single value resource fields, provide a regular expression that the entered String must conform to. 
	 * Note that this information is not communicated to the page user, by default. 
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface FilterString {
		
		String regexp() default ".*";
		
	}
	
	/**
	 * For numeric single value resource fields, provide a range of allowed values. 
	 * Lower and upper boundary are included, i.e. they are allowed.
	 * Note that this information is not communicated to the page user, by default. 
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface FilterValueLong {

		long lowerBound() default Long.MIN_VALUE;
		long upperBound() default Long.MAX_VALUE;
		
	}
	
	/**
	 * For FloatResource fields provide a range of allowed values.
	 * If one of the boundaries is set to <code>Float.NaN</code>, it is considered as not set.
	 * Note that this information is not communicated to the page user, by default. 
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface FilterValueFloat {
		
		float lowerBound() default Float.NaN;
		float upperBound() default Float.NaN;
		boolean includeLowerBoundary() default true;
		boolean includeUpperBoundary() default true;
		
	}
	
	/**
	 * For edit pages. If a field has this annotation, its value is displayed,
	 * but it cannot be edited. 
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Unmodifiable {}
	
}
