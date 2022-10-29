package org.ogema.tools.app.useradmin.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.action.Action;
import org.ogema.model.prototypes.Data;
import org.ogema.model.user.NaturalPerson;

public interface UserAdminData extends Data {
	ResourceList<NaturalPerson> userData();
	
	ResourceList<RESTUserData> restUserData();
	
	/** If zero, negative or not existing then no invite messages are sent when a new user is created. This is the
	 * default for system setup. Only after finishing setup then this shall be activated and missing invites
	 * are sent out automatically.<br>
	 * A negative value indicates a temporary switch off. Invites will not be sent out automatically when enabled again.
	 */
	IntegerResource enableInviteMessagesForUserCreation();
	
	/** Deeplink for the savings page of the gateway including ssik*/
	StringResource ssik_facilityDeepLink();
	
	/** Comma-separated list of email addresses that shall be used for customer maintenance requests in
	 * addition to the admin email addresses received from CMS (or customer addresses directly created on the gateway)
	 */
	StringResource additionalAdminEmailAddresses();
	
	/** Salutations for email. Shall contain entries like "Frau Müller" or "Herr Maier". The rest of the template shall
	 * be filled automatically. The first entries are for additionalAdminEmailAddresses followed by salutations for addresses
	 * received from CMS.
	 */
	StringResource personalSalutations();
	
	/** If true then no user addresses will be evaluated*/
	BooleanResource useOnlyAdditionalAddresses();
	
	Action triggerUpdateFromCMS();
}
