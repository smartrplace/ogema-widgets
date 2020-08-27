package de.iwes.widgets.api.extended.util;

import org.ogema.core.administration.UserAccount;
import org.ogema.core.administration.UserConstants;
import org.ogema.core.application.ApplicationManager;

public class UserLocaleUtil {
	public static String getLocaleString(String userName, ApplicationManager appMan) {
		UserAccount userAccount = appMan.getAdministrationManager().getUser(userName);
		if(userAccount == null)
			return null;
		return userAccount.getProperties().getOrDefault(UserConstants.PREFERRED_LOCALE, "EN").toString();
	}
	
	public static void setLocaleString(String userName, String localeString, ApplicationManager appMan) {
		UserAccount userAccount = appMan.getAdministrationManager().getUser(userName);
		if(userAccount == null)
			return;
		userAccount.getProperties().put(UserConstants.PREFERRED_LOCALE, localeString);
	}
}
