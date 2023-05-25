package org.smartrplace.gateway.device;

import org.ogema.core.model.ResourceList;
import org.ogema.model.extended.alarming.AlarmGroupDataMajor;
import org.ogema.model.prototypes.Data;
import org.ogema.model.user.NaturalPerson;

public interface GatewaySuperiorData extends Data {
	/** First generation known issue statistics to be transferred from gateway to superior
	 * Initially transferred via Heartbeat
	 */
	KnownIssueDataGw knownIssueStatistics();
	
	/** Known issues to be synchronized with superior and/or shall be stored for
	 * staistical purposes.
	 */
	ResourceList<AlarmGroupDataMajor> majorKnownIssues();
	
	/** The elements in this list are contacts that can be selected as responsible.
	 * See also userAdminData/userData how this is used. The name field contains the
	 * email address, the userName field the String to be displayed.*/
	ResourceList<NaturalPerson> responsibilityContacts();
}
