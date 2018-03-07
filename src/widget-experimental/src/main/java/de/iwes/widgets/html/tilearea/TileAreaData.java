/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package de.iwes.widgets.html.tilearea;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ogema.core.administration.AdminApplication;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.appbox.AppBoxData;
import de.iwes.widgets.html.tilearea.TileArea.ApplicationTileData;

public class TileAreaData extends AppBoxData {
	
	private List<ApplicationTileData> apps = null; 
//	private boolean passMetaInfo = false;

	public TileAreaData(TileArea box) {
		super(box);
		//this.am = box.am;
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		Map<String,String> css = new HashMap<>();
		css.put("display", "flex");
		css.put("flex-wrap", "wrap");
		addCssItem(">div", css);		
		JSONObject obj = new JSONObject();
		obj.put("apps", appsList2JSON(req.getReq()));
		if (backgroundColor != null) {
			Map<String,String> backgroundColor = new HashMap<String, String>();
			backgroundColor.put("background-color", this.backgroundColor);
			addCssItem(".app-container", backgroundColor);
		}
		if (textColor != null) {
			Map<String,String> textColor = new HashMap<String, String>();
			textColor.put("color", this.textColor);
			addCssItem(".app-container", textColor);
		}
		return obj;
	}
	
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		throw new UnsupportedOperationException();
	}
	
//	public void setPassMetaInfo(boolean passMetaInfo) {
//		this.passMetaInfo = passMetaInfo;
//	}
//	
//	public boolean isPassMetaInfo() {
//		return passMetaInfo;
//	}
	
	public List<ApplicationTileData> getApps() {
		return apps;
	}

	public void setApps(List<ApplicationTileData> apps) {
		this.apps = apps;
	}
	
	@Override
	public List<AdminApplication> getAdminApps() {
		throw new UnsupportedOperationException();
	}
	@Override
	public void setAdminApps(Collection<AdminApplication> adminApps) {
		throw new UnsupportedOperationException();
	}
	
	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	private final JSONArray appsList2JSON(HttpServletRequest req) {
		List<ApplicationTileData> apps;
		//if (adminApps == null)
		//	apps = am.getAllApps();  // note: this may return more than one app per bundle; if the bundle registers multiple applications
									 // maybe we should filter these, except if multiple of those apps define different start pages?
		//else
			apps = this.apps;

		
		// this causes problems if there is more than one app in a single bundle...
		// they have the same name, hence will be displayed the same way on the GUI -> filter by using a map
		//	Map<String, Map> = Map<App Id, App info>		
		JSONArray array = new JSONArray();

		for (ApplicationTileData entry : apps) {
			//if (!entry.isWebAccessAllowed(req))
			//	continue;
			String name = entry.name;
			/*String fileName = entry.bundleLocation;
			int lastSeperator = fileName.lastIndexOf("/");
			fileName = fileName.substring(lastSeperator + 1, fileName.length());
			boolean needFilter = false;
			for (String filter : Settings.FILTERED_APPS) {
				if (name.startsWith(filter)) {
					needFilter = true;
					for (String exception : Settings.FILTER_EXCEPTIONS) {
						if (name.contains(exception)) {
							needFilter = false;
							break;
						}
					}
					break;
				}
			}
			if (needFilter) {
				continue;
			}
			*/
			//long id = entry.getBundleRef().getBundleId();
			long id = entry.bundleId;
			
			JSONObject singleApp = new JSONObject();
			//singleApp.put("name",name);
			singleApp.put("id",id);
			singleApp.put("url",entry.iconPath);
			/* Note: For conventional app icons the url is
			 * url = "/apps/ogema/framework/gui/installedapps?action=getIcon&id=" + appData.id;
			 */
			//Bundle bundle = entry.getBundleRef();
			
			// Map<String,String>
			JSONObject metainfo = new JSONObject();
			//metainfo.put("File_Name", "FN:"+name);
			metainfo.put("Bundle_Name", name);
			
			metainfo.put("Bundle_Description", entry.metainfo_Bundle_Description);
			metainfo.put("Bundle_Version", entry.metainfo_Bundle_Version);

			/*Dictionary<String, String> bundleDictionary = bundle.getHeaders();
			Enumeration<String> dictionaryEnums = bundleDictionary.keys();
			while (dictionaryEnums.hasMoreElements()) {
				String key = dictionaryEnums.nextElement();
				String element = bundleDictionary.get(key);

				if (!("Import-Package".equals(key) || "Export-Package".equals(key))) {
					String formattedKey = key.replace('-', '_');
					metainfo.put(formattedKey, element);
				}
			}
			*/
			singleApp.put("metainfo",metainfo);
			
			String startPage = entry.startUrl;			
			if (startPage != null)
				singleApp.put("startPage", startPage);
			array.put(singleApp);
		}
		return array;
	}
	
}
