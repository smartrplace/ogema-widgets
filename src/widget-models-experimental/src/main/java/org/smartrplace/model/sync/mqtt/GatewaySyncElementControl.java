package org.smartrplace.model.sync.mqtt;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.Data;

/** Such a resource may be placed as decorator into any resource in a gateway-synch resource (e.g. _19170) of type
 * {@link GatewaySyncElementControl}
 */
public interface GatewaySyncElementControl extends Data {
	/** 0: normal (as if decorator was not present)
	 *  1: without resource lists: skip any resource lists that are direct sibblings of the decorator.
	 *  	As resource lists often contain subgateway-specific data they may need special handling
	 *  2: resolve-references: Only relevant if the decorator hat a ResourceList as parent. In this
	 *     case any references in the resource list on the superior shall be resolved to direct
	 *     children in the sub gateway with the same structure as the resource referenced. Further
	 *     references are processed as any normal reference (only if pointing into the data set
	 *     synchronized with the same configuration line).<br>
	 *     This can be used to reference the data relevant for a certain subgateway from a resource list
	 *     on superior containing the data for all subgateways.
	 */
	IntegerResource syncMode();
}
