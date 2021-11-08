package de.iwes.widgets.api.extended.util;

import javax.servlet.http.HttpSession;

import org.ogema.accesscontrol.Constants;
import org.ogema.accesscontrol.SessionAuth;
import org.ogema.core.administration.AdministrationManager;
import org.ogema.core.administration.UserAccount;
import org.ogema.core.administration.UserConstants;
import org.ogema.core.application.ApplicationManager;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class UserLocaleUtil {
	private static String systemDefaultLocaleString = "en";
	private static AdministrationManager adminMan = null;
	public static void setSystemDefaultLocale(String localeString, AdministrationManager adminMan) {
		setUserAdmin(adminMan);
		systemDefaultLocaleString = localeString;
	}
	public static String getSystemDefaultLocaleString() {
		return systemDefaultLocaleString;
	}
	public static OgemaLocale getSystemDefaultLocale() {
		OgemaLocale result = OgemaLocale.getLocale(systemDefaultLocaleString);
		if(result != null)
			return result;
		return OgemaLocale.ENGLISH;
	}
	
	public static void setUserAdmin(AdministrationManager adminManIn) {
		if(adminMan == null)
			adminMan = adminManIn;
	}
	public static void setUserAdmin(ApplicationManager appMan) {
		if(appMan != null)
			setUserAdmin(appMan.getAdministrationManager());
	}	
	
	public static String getLocaleStringRaw(String userName, ApplicationManager appMan) {
		UserAccount userAccount = appMan.getAdministrationManager().getUser(userName);
		if(userAccount == null)
			return null;
		Object result = userAccount.getProperties().get(UserConstants.PREFERRED_LOCALE);
		if(result == null)
			return null;
		return result.toString();
	}

	public static String getLocaleString(String userName, ApplicationManager appMan) {
		UserAccount userAccount = appMan.getAdministrationManager().getUser(userName);
		if(userAccount == null)
			return null;
		return userAccount.getProperties().getOrDefault(UserConstants.PREFERRED_LOCALE, systemDefaultLocaleString).toString();
	}
	
	public static OgemaLocale getLocale(String userName, ApplicationManager appMan) {
		return OgemaLocale.getLocale(getLocaleString(userName, appMan));
	}

	public static String getLocaleString(OgemaHttpRequest req, ApplicationManager appMan) {
		String user = getUserLoggedIn(req);
		if (user == null) return null;
		return getLocaleString(user, appMan);
	}
	
	/** TODO: This value shall be returned by {@link OgemaHttpRequest#getLocale()} directly
	 */
	public static OgemaLocale getLocale(OgemaHttpRequest req) {
		return OgemaLocale.getLocale(getLocaleString(req, null));
	}
	
	public static boolean hasLocaleStringStored(String userName, ApplicationManager appMan) {
		UserAccount userAccount = appMan.getAdministrationManager().getUser(userName);
		return userAccount.getProperties().containsKey(UserConstants.PREFERRED_LOCALE);
	}

	public static boolean hasLocaleStringStored(OgemaHttpRequest req, ApplicationManager appMan) {
		boolean r = hasLocaleStringStoredTmp(req, appMan);
		System.out.println(getUserLoggedIn(req) + " has language stored? " + (r ? "yes" : "no"));
		return r;
	}
	public static boolean hasLocaleStringStoredTmp(OgemaHttpRequest req, ApplicationManager appMan) {
		String user = getUserLoggedIn(req);
		if (user == null) return false;
		return hasLocaleStringStored(user, appMan);
	}

	public static void setLocaleString(String userName, String localeString, ApplicationManager appMan) {
		setUserAdmin(appMan);
		UserAccount userAccount = adminMan.getUser(userName);
		if(userAccount == null)
			return;
		userAccount.getProperties().put(UserConstants.PREFERRED_LOCALE, localeString);
	}

	public static void setLocaleString(OgemaHttpRequest req, String localeString, ApplicationManager appMan) {
		String user = getUserLoggedIn(req);
		if (user != null)
			setLocaleString(user, localeString, appMan);
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
