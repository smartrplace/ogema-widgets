package de.iwes.widgets.api.extended.util;

import javax.servlet.http.HttpSession;

import org.ogema.accesscontrol.Constants;
import org.ogema.accesscontrol.SessionAuth;
import org.ogema.core.administration.UserAccount;
import org.ogema.core.administration.UserConstants;
import org.ogema.core.application.ApplicationManager;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

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
	
	public static String getUserLoggedIn(OgemaHttpRequest req) {
        HttpSession session = req.getReq().getSession();
        return getUserLoggedInBase(session);
	}
	public static String getUserLoggedInBase(HttpSession session) {
        SessionAuth sauth = (SessionAuth) session.getAttribute(Constants.AUTH_ATTRIBUTE_NAME);
        if(sauth == null)
        	return null;
        return sauth.getName();
	}
}
