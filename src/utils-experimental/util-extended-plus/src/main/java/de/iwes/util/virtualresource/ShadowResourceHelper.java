/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.util.virtualresource;

import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.PatternListener;

import de.iwes.pattern.management.backup.ManagedResourcePattern;
import de.iwes.util.resource.ResourceHelper;
import de.iwes.util.resource.ResourceOperationCallback;

/** Helpers for handling shadow resources
 * @author dnestle
 */
public class ShadowResourceHelper {
	/**Call this in init/accept method of driver that uses a pattern to handle the resources
	 * it controls. A driver should stick with a shadow resource even if it is set inactive and should
	 * reject the respective visible active resource.
	 * @param pattern provided by framework
	 * @param patternContainer reference to patternContainer, usually this
	 * TODO: Change this to ContextSensitivePattern, then this will not be required anymore
	 * @param patternContructorCallback: Callback must just call new <Pattern>(resource)
	 * 			and return the newly created resource
	 * @param listener if not null the patternAvailable method will be called
	 * @param if not null the new pattern will be added to this list
	 * @return shadow pattern if it was detected and a replacement pattern was created
	 * 			otherwise null.<br>
	 * 			If null is returned, the pattern given as argument can be used normally as
	 * 			it has no shadow resource.
	 */
	@Deprecated
	public static <M extends ManagedResourcePattern<?, ?>> M  handleShadowingInDriverInit(
			M pattern,
			//ResourcePatternContainer patternContainer,
			ResourceOperationCallback patternContructorCallback,
			List<M> patternList, PatternListener<M> listener,
			ApplicationManager am) {
		
		Resource shadowResRef = pattern.checkForShadowResource();
		if(shadowResRef != null) {
			Resource shadowRes = ResourceHelper.localizeResource(shadowResRef, am.getResourceAccess());
			@SuppressWarnings("unchecked")
			M shadowPattern = (M)patternContructorCallback.callback(shadowRes, null);
			shadowPattern.accept();
			//shadowPattern.init(patternContainer);
			if(listener != null) {
				listener.patternAvailable(shadowPattern);
			}
			if(patternList != null) {
				patternList.add(shadowPattern);
			}
			return shadowPattern;
		}
		return null;
	}
}
