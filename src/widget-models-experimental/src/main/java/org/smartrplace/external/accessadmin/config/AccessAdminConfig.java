package org.smartrplace.external.accessadmin.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.locations.BuildingPropertyUnit;
import org.ogema.model.prototypes.Configuration;

/** 
 * The global configuration resource type for this app.
 */
public interface AccessAdminConfig extends Configuration {

	/** Permissions for users and user groups*/
	ResourceList<AccessConfigUser> userPermissions();
	
	/** The list contains not only real property units, but all kinds of room groups.
	 * These groups can be used also for other purposes besides permission management
	 */
	ResourceList<BuildingPropertyUnit> roomGroups();
	
	AccessConfigBase userStatusPermission();
	AccessConfigBase userStatusPermissionWorkingCopy();
	BooleanResource userStatusPermissionChanged();
	
	ResourceList<SubCustomerData> subCustomers();
	
	/** Allow to make building-wide eco mode and subcustomer configuration accessible for all subcustomers/tenants.
	 * This is important for the case that user-tenant configuration is not available/made for a gateway. Then all users must be
	 * able to access all tenants, otherwise no user can access any tenant.
	 * 0 = all users access building-wide eco mode (if allowed to see eco mode at all), all users can configure and tenants
	 * 		if able to access to configuration.
	 * 1 = standard mode, users see eco mode depending on tenant and only see tenants for which they are configured.
	 *      This is activated when a user is configured to the aggregating tenant
     */
	IntegerResource subcustomerUserMode();
}
