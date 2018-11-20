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
package de.iwes.widgets.api.extended.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;

import de.iwes.widgets.api.extended.WidgetAdminService;
import de.iwes.widgets.api.extended.WidgetAppImpl;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;

@Component(specVersion = "1.2", immediate = true)
@Properties( { @Property(name = "osgi.command.scope", value = "ogm"),
		@Property(name = "osgi.command.function", value = { "listapps", "listpages", "listwidgets", 
				"getSessions", "deleteAllSessions", "widgetThreadCount", "accessCount", "accessCountSorted" }) })
@Service(ShellCommands.class)
@Descriptor("OGEMA widget commands")
public class ShellCommands {
	
	@Reference
	private WidgetAdminService widgetAdminService;

	@Descriptor("list registered widget apps")
	public void listapps() {
		Map<String,WidgetApp> apps = widgetAdminService.getRegisteredApps();
		for (Map.Entry<String, WidgetApp> entry: apps.entrySet()) {
			WidgetAppImpl app = (WidgetAppImpl) entry.getValue();
			System.out.printf("App: %s, pages: %d%n", entry.getKey() , app.getPages().size());
		}
	}
	
	@Descriptor("list registered widget pages")
	public void listpages(
			@Descriptor("Widget app url (empty for all apps)") @Parameter(names = {
					"-a", "--app" }, absentValue = "")String appUrl) {
		Map<String,WidgetApp> apps = widgetAdminService.getRegisteredApps();
		for (Map.Entry<String, WidgetApp> entry: apps.entrySet()) {
			WidgetAppImpl app = (WidgetAppImpl) entry.getValue();
			if (appUrl != null && !appUrl.trim().isEmpty() && !entry.getKey().equals(appUrl))
				continue;
			System.out.printf("App: %s%n", entry.getKey());
			for (WidgetPage<?> p: app.getPages().values()) {
				System.out.printf("  Page: %s%n",p.getFullUrl());
			}
		}
	}
	
	@Descriptor("list widgets for a page")
	public void listwidgets(
			@Descriptor("Widget app url (empty for all apps)") 
			@Parameter(names = {"-a", "--app" }, absentValue = "") 
				String appUrl,
			@Descriptor("Relative page url") 
			@Parameter(names = {"-p", "--page" }, absentValue = "") 
				String pageUrl) 
	{
		try {
		WidgetApp app = widgetAdminService.getRegisteredApps().get(appUrl);
		if (app == null) {
			System.out.println("App " + appUrl + " not found");
			return;
		}
		for (Map.Entry<String, WidgetPage<?>> entry: app.getPages().entrySet()) {
			if (pageUrl !=null  && !pageUrl.trim().isEmpty()) {
				if (entry.getKey().equals(pageUrl.trim())) {
					printPageWidgets(entry.getValue());
					break;
				} 
				else {
					continue;
				}
			}
			printPageWidgets(entry.getValue());
		}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private void printPageWidgets(WidgetPage<?> page) {
		System.out.println("Widgets for page " + page.getFullUrl());
		List<OgemaWidget> widgets = widgetAdminService.getPageWidgets(page);
		for (OgemaWidget w: widgets ) {
			// TODO print widget type
			System.out.printf("   Id: %-20s type: %-30s\n", w.getId() + ",", w.getClass() );
		}
		System.out.println();
	}
	
	@Descriptor("Get the number of current user sessions for an app or a page")
	public void getSessions(
			@Descriptor("App url (empty for all apps)") 
			@Parameter(names = {"-a", "--app" }, absentValue = "") 
				String appUrl,
			@Descriptor("Relative page url") 
			@Parameter(names = {"-p", "--page" }, absentValue = "") 
				String pageUrl) 
	{
		if (appUrl == null || appUrl.trim().isEmpty()) {
			int cnt =0;
			for (WidgetApp app : widgetAdminService.getRegisteredApps().values()) {
				int localCnt = widgetAdminService.getNumberOfSessions(app);
				System.out.println("Number of sessions for app " + app.appUrl() + ": " + localCnt);
				cnt += localCnt;
			}
			System.out.println("Total number of sessions: " + cnt);
			return;
		}
		
		WidgetApp app = widgetAdminService.getRegisteredApps().get(appUrl);
		if (app == null) {
			System.out.println("App " + appUrl + " not found");
			return;
		}
		System.out.println("Sessions for app " + appUrl + ": " + widgetAdminService.getNumberOfSessions(app));
		for (Map.Entry<String, WidgetPage<?>> entry: app.getPages().entrySet()) {
			if (pageUrl !=null  && !pageUrl.trim().isEmpty()) {
				if (entry.getKey().equals(pageUrl.trim())) {
					System.out.println("   Page " + entry.getKey() + ": " + widgetAdminService.getNumberOfSessions(entry.getValue()));
					break;
				} 
				else {
					continue;
				}
			}
			System.out.println("   Page " + entry.getKey() + ": " + widgetAdminService.getNumberOfSessions(entry.getValue()));
		}
	}
	
	@Descriptor("Print the number of currently active widget threads")
	public void widgetThreadCount() {
		System.out.printf("Widget threads: %d%n", WidgetThreadFactory.activeCount());
	}
	
	@Descriptor("delete all user sessions")
	public void deleteAllSessions() {
		widgetAdminService.deleteAllSessions();
	}
	
	@Descriptor("Get the access count for a page")
	public void accessCount(
			@Descriptor("App url (empty for all apps)") 
			@Parameter(names = {"-a", "--app" }, absentValue = "") 
				String appUrl,
			@Descriptor("Relative page url") 
			@Parameter(names = {"-p", "--page" }, absentValue = "") 
				String pageUrl) 
	{
		if (appUrl == null || appUrl.trim().isEmpty()) {
			for (WidgetApp app : widgetAdminService.getRegisteredApps().values()) {
				printAccessCount(app);
			}
			return;
		}
		WidgetApp app = widgetAdminService.getRegisteredApps().get(appUrl);
		if (app == null) {
			System.out.println("App " + appUrl + " not found");
			return;
		}
		if (pageUrl == null || pageUrl.trim().isEmpty()) {
			printAccessCount(app);
			return;
		}
		System.out.println(" Access count for app " + appUrl);
		for (Map.Entry<String, WidgetPage<?>> entry: app.getPages().entrySet()) {
			if (!entry.getKey().equals(pageUrl))
				continue;
			System.out.println("   Page " + entry.getKey() + ": " + widgetAdminService.getNumberOfSessions(entry.getValue()));
			break;
		}
	}
	
	@Descriptor("Get the access count for all pages, sorted by the number of page visits")
	public void accessCountSorted() {
		final NavigableMap<Integer, List<String>> pages = new TreeMap<>(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return -o1.compareTo(o2);
			}
		});
		for (WidgetApp wapp : widgetAdminService.getRegisteredApps().values()) {
			for (WidgetPage<?> page : wapp.getPages().values()) {
				final int cnt = widgetAdminService.getAccessCount(page);
				List<String> list = pages.get(cnt);
				if (list == null) {
					list = new ArrayList<>();
					pages.put(cnt,list);
				}
				list.add(page.getFullUrl());
			}
		}
		int cnt = 1;
		for (Map.Entry<Integer, List<String>> entry : pages.entrySet()) {
			System.out.println("Access count position " + cnt++ + ": count: " + entry.getKey());
			for (String s : entry.getValue()) {
				System.out.println(" Page " + s);
			}
		}
	}
	
	private final void printAccessCount(WidgetApp wapp) {
		System.out.println(" Access count for app: " + wapp.appUrl());
		for (Map.Entry<String,WidgetPage<?>> entry : wapp.getPages().entrySet()) {
			System.out.println("  Page " + entry.getKey() + ": count: " + widgetAdminService.getAccessCount(entry.getValue()));
		}
	}
	
}
