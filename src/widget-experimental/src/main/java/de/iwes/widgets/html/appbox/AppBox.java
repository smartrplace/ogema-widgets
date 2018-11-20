/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iwes.widgets.html.appbox;

import java.util.Collection;
import java.util.List;

import org.ogema.core.administration.AdminApplication;
import org.ogema.core.administration.AdministrationManager;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A widget that displays an overview of a set of apps. 
 * The apps to be displayed can be set explicitly via
 * {@link #setDefaultAdminApps(Collection)} or 
 * {@link #setAdminApps(Collection, OgemaHttpRequest)}; alternatively, 
 * the set of apps are determined by the widget.
 */
public class AppBox extends OgemaWidgetBase<AppBoxData> {

	private static final long serialVersionUID = 1L;
	final AdministrationManager am;
	private Collection<AdminApplication> defaultAdminApps = null; 
	private String defaultBackgroundColor = "#EEEEEE";;
	private String defaultTextColor = null;
//	private boolean defaultPassMetaInfo = false;
	
	/*
	 ********* Constructors ********** 
	 */

	/**
	 * 
	 * @param page
	 * @param id
	 * @param adminManager
	 * 		may be null, if apps are set explicitly via {@link AppBox#setDefaultAdminApps(Collection)}.
	 */
	public AppBox(WidgetPage<?> page, String id, AdministrationManager adminManager) {
		super(page, id);
		this.am = adminManager;
	}

	/**
	 * 
	 * @param page
	 * @param id
	 * @param globalWidget
	 * 		default: false
	 * @param adminManager
	 * 		may be null, if apps are set explicitly via {@link AppBox#setDefaultAdminApps(Collection)}.
	 */
	public AppBox(WidgetPage<?> page, String id, boolean globalWidget, AdministrationManager adminManager) {
		super(page, id, globalWidget);
		this.am = adminManager;
	}
	
	/**
	 * 
	 * @param parent
	 * @param id
	 * @param req
	 * @param adminManager
	 * 		may be null, if apps are set explicitly via {@link AppBox#setDefaultAdminApps(Collection)}.
	 */
	public AppBox(OgemaWidget parent, String id, OgemaHttpRequest req, AdministrationManager adminManager) {
		super(parent, id, req);
		this.am = adminManager;
	}
	
	/*
	 ***** Override if required
	 */
	
	protected String getDescription(AdminApplication app) {
		return null;
	}
	
	/*
	 ********* Internal methods ********** 
	 */
	
	@Override
	public AppBoxData createNewSession() {
		return new AppBoxData(this);
	}
	
	@Override
	protected void setDefaultValues(AppBoxData opt) {
		super.setDefaultValues(opt);
//		opt.setPassMetaInfo(defaultPassMetaInfo);
		opt.setAdminApps(defaultAdminApps);
		opt.setBackgroundColor(defaultBackgroundColor);
		opt.setTextColor(defaultTextColor);
	}
	
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return AppBox.class;
	}
	
	/*
	 ********* Public methods ********** 
	 */
	
	public void setDefaultAdminApps(Collection<AdminApplication> adminApps) {
		this.defaultAdminApps = adminApps;
	}
	
	public void setDefaultBackgroundColor(String backgroundColor) {
		this.defaultBackgroundColor = backgroundColor;
	}
	
	public void setDefaultTextColor(String textColor) {
		this.defaultTextColor = textColor;
	}
	
//	public void setDefaultPassMetaInfo(boolean passMetaInfo) {
//		this.defaultPassMetaInfo = passMetaInfo;
//	}
//	
//	public void setPassMetaInfo(boolean passMetaInfo, OgemaHttpRequest req) {
//		getOptions(req).setPassMetaInfo(passMetaInfo);
//	}
//	
//	public boolean isPassMetaInfo(OgemaHttpRequest req) {
//		return getOptions(req).isPassMetaInfo();
//	}
	
	public List<AdminApplication> getAdminApps(OgemaHttpRequest req) {
		return getData(req).getAdminApps();
	}

	public void setAdminApps(Collection<AdminApplication> adminApps, OgemaHttpRequest req) {
		getData(req).setAdminApps(adminApps);
	}
	
	public String getBackgroundColor(OgemaHttpRequest req) {
		return getData(req).getBackgroundColor();
	}

	public void setBackgroundColor(String backgroundColor, OgemaHttpRequest req) {
		getData(req).setBackgroundColor(backgroundColor);
	}
	
	public String getTextColor(OgemaHttpRequest req) {
		return getData(req).getTextColor();
	}

	public void setTextColor(String textColor, OgemaHttpRequest req) {
		getData(req).setTextColor(textColor);
	}
	
}
