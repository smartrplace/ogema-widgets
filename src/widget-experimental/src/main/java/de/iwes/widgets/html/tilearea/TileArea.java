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
package de.iwes.widgets.html.tilearea;

import java.util.Collection;
import java.util.List;

import org.ogema.core.administration.AdminApplication;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.appbox.AppBox;
import de.iwes.widgets.html.appbox.AppBoxData;

/**
 * A widget that displays an overview of a set of apps. 
 * The apps to be displayed can be set explicitly via
 * {@link #setDefaultAdminApps(Collection)} or 
 * {@link #setAdminApps(Collection, OgemaHttpRequest)}; alternatively, 
 * the set of apps are determined by the widget.
 */
public class TileArea extends AppBox {
	public class ApplicationTileData {
		public String iconPath; //TODO: This option requires adaptation of Javascript
		public long bundleId; //id; use this as alternative to iconPath
		//Note: base for icon request: https://localhost:8443/apps/ogema/framework/gui/installedapps?action=getIcon&id=132
		
		String bundleLocation; //remove

		public String startUrl; //startPage
		public String name; //metainfo.Bundle_Name
		public String metainfo_Bundle_Description;
		public String metainfo_Bundle_Version;
	}
	
	private static final long serialVersionUID = 1L;
	//final AdministrationManager am;
	private List<ApplicationTileData> defaultAdminApps = null; 
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
	 */
	public TileArea(WidgetPage<?> page, String id) {
		super(page, id, null);
		//this.am = adminManager;
	}

	/**
	 * 
	 * @param page
	 * @param id
	 * @param globalWidget
	 * 		default: false
	 */
	public TileArea(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget, null);
		//this.am = adminManager;
	}
	
	/**
	 * 
	 * @param parent
	 * @param id
	 * @param req
	 */
	public TileArea(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req, null);
		//this.am = adminManager;
	}
	
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return TileArea.class;
	}

	/*
	 ********* Internal methods ********** 
	 */
	
	@Override
	public TileAreaData createNewSession() {
		return new TileAreaData(this);
	}
	
	@Override
	protected void setDefaultValues(AppBoxData opt) {
//		opt.setPassMetaInfo(defaultPassMetaInfo);
		((TileAreaData)opt).setApps(defaultAdminApps);
		opt.setBackgroundColor(defaultBackgroundColor);
		opt.setTextColor(defaultTextColor);
	}
	
	/*
	 ********* Public methods ********** 
	 */
	@Override
	public void setDefaultAdminApps(Collection<AdminApplication> adminApps) {
		throw new UnsupportedOperationException();
	}
	public void setDefaultApps(List<ApplicationTileData> apps) {
		this.defaultAdminApps = apps;
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
	@Override
	public List<AdminApplication> getAdminApps(OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	public List<ApplicationTileData> getApps(OgemaHttpRequest req) {
		return ((TileAreaData)getData(req)).getApps();
	}

	@Override
	public void setAdminApps(Collection<AdminApplication> adminApps, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	public void setApps(List<ApplicationTileData> adminApps, OgemaHttpRequest req) {
		((TileAreaData)getData(req)).setApps(adminApps);
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
