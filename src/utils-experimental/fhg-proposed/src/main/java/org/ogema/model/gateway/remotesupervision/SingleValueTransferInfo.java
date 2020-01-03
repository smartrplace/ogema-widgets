package org.ogema.model.gateway.remotesupervision;

import org.ogema.core.model.ModelModifiers.NonPersistent;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
	
/** Single value to be transferred continuously from client to master. This transfer method does not
 * store/repeat missing values due to communication failures etc. This is suitable meter counter values
 * where intermediate missing values are integrated in a new value or when the master just needs the current
 * value and intermediate missing values are no problem.
*/
public interface SingleValueTransferInfo extends ValueTransferInfo {
	/**This element shall be updated by the client continuously*/
	@NonPersistent
	FloatResource value();
	/** Timestamp, value and quality should be written in a transaction. Timestamp and quality may not
	 * always be provided by client
	 */
	@NonPersistent
	TimeResource timeStamp();
	@NonPersistent
	IntegerResource quality();
}