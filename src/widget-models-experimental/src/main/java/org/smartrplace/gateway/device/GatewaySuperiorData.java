package org.smartrplace.gateway.device;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.extended.alarming.AlarmGroupDataMajor;
import org.ogema.model.prototypes.Data;
import org.ogema.model.user.NaturalPerson;
import org.smartrplace.external.accessadmin.config.SubCustomerData;

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
	
	
	/** If no tenants exist or the same facility/IT contact information applies to all
	 * tenants all information is stored here. This information shall also be considered
	 * relevant for all tenants that have no special entry in {@link #tenantData()}
	 */
	SubCustomerData buildingData();

	/** Special information per tenant if different from {@link #buildingData()}. Note that
	 * initially administration of tenant data may only take place on gateway level.
	 * */
	ResourceList<SubCustomerData> tenantData();
	
	/** Number of tenants with own contacts for facility management and/or IT.
	 * Note that this number may be different from number of tenants defined on CMS
	 * as sometimes tenants are used on CMS level to represent departments etc. that
	 * usually have no own technical contacts.<br>
	 * If {@link #tenantData()} exists, the information there should be checked even
	 * if this resource is zero. If this resource is positive and tenantData is empty,
	 * then check on gateway or in other documentation for more information.
	 * */
	IntegerResource numberOfTenantsWithTechnicalContact();
}
