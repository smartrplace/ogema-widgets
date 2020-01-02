package org.ogema.model.viewpattern;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

public interface PatternElement extends Data {
	/**Path of resource below pattern model resource. Usually this point to a 
	 * {@link SingleValueResource} or to {@link Action}.*/
	StringResource path();
	
	/**Resource type of the element. For non-decorating child resources this is
	 * informative.*/
	StringResource type();
	
	BooleanResource required();
	
	/**If the element is not required the element may be created via the view page if this
	 * is true (true default)
	 */
	BooleanResource offerCreate();
	
	BooleanResource acceptInactive();
	
	/**Informative: The name element already defines whether it is decorating or not,
	 * but the information may be stored here also
	 */
	BooleanResource isDecorating();
	
	/**Set allowed range, special values, default value etc.*/
	SingleValueConfiguration valueConfiguration();
	
	/**Format into which value is put with String.format - if not given a standard format for
	 * each resource type will be used*/
	StringResource format();
	
	@Override
	/**Heading for the column (or title fiven before value in status bar)*/
	StringResource name();
}
