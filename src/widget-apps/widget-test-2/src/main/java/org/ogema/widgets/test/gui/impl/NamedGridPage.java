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
package org.ogema.widgets.test.gui.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.ogema.core.application.ApplicationManager;
import org.osgi.service.component.annotations.Component;

import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.checkbox.Checkbox2;
import de.iwes.widgets.html.form.checkbox.DefaultCheckboxEntry;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.dropdown.EnumDropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.html5.NamedAreaGrid;
import de.iwes.widgets.html.html5.SimpleGrid;
import de.iwes.widgets.html.html5.grid.AlignItems;
import de.iwes.widgets.html.html5.grid.JustifyItems;
import de.iwes.widgets.html.icon.Icon;
import de.iwes.widgets.html.icon.IconType;
import de.iwes.widgets.html.popup.Popup;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE, 
				LazyWidgetPage.RELATIVE_URL + "=areagrid.html",
				LazyWidgetPage.START_PAGE + "=false",
				LazyWidgetPage.MENU_ENTRY + "=Area grid page"
		}
)
public class NamedGridPage implements LazyWidgetPage {

	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new GridPageInit(page);
	}
	
	private static class GridPageInit {
	
		private final static String indent = "&nbsp;&nbsp;&nbsp;&nbsp;";
		private final static String linebreak = "<br>";
		private final WidgetPage<?> page;
		private final Alert info;
		private final Header header;
		private final Header settingsHeader;
		private final Header gridHeader;
		private final Header cssHeader;
		private final NamedAreaGrid grid;
		private final Checkbox2 optionsCheckbox;
		private final SimpleGrid settingsGrid;
		private final EnumDropdown<JustifyItems> justifyItemsDrop;
		private final EnumDropdown<AlignItems> alignItemsDrop;
		private final TextField columnGap;
		private final TextField rowGap;
		private final Label cssField;
		
		private final Button editAreasPopupTrigger;
		private final Popup editAreasPopup;
		private final Dropdown editAreasSelector;
		private final TextField editProperty;
		private final TextField editValue;
		private final Button editSubmit;
		
		@SuppressWarnings("serial")
		GridPageInit(final WidgetPage<?> page) {
			this.page = page;
			this.info = new Alert(page, "info", 
					"This page illustrates the use of the <i>NamedAreaGrid</i> widget. It makes use of the <code>display:grid</code>"
					+ " CSS property. See <a href=\"https://css-tricks.com/snippets/css/complete-guide-grid/\">"
					+ "A complete guide to grid</a> for details.<br>"
					+ "Confer also the <i><a href=\"" + Constants.URL_BASE +"/grid.html\">SimpleGrid</a></i> "
					+ "and <i><a href=\"" + Constants.URL_BASE + "/templategrid.html\">TemplateGrid</a></i> widgets; the <i>NamedAreaGrid</i> is "
					+ "similar to simple grid, but enables the use of the <code>grid-template-areas</code> attribute, by means of which "
					+ "items can cover multiple cells."
			);
			info.setDefaultTextAsHtml(true);
			info.addDefaultStyle(AlertData.BOOTSTRAP_INFO);
			this.header = new Header(page, "header", "Grid test page");
			header.setDefaultColor("blue");
			this.settingsHeader = new Header(page, "settingsHeader", "Settings");
			settingsHeader.setDefaultHeaderType(3);
			settingsHeader.setDefaultColor("blue");
			this.gridHeader = new Header(page, "gridHeader", "Grid");
			gridHeader.setDefaultHeaderType(3);
			gridHeader.setDefaultColor("blue");
			this.cssHeader = new Header(page, "cssHeader", "CSS");
			cssHeader.setDefaultHeaderType(3);
			cssHeader.setDefaultColor("blue");
			this.columnGap = new TextField(page, "colGap", "2em")  {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final String gap = grid.getColumnGap(req);
					if (gap == null)
						setValue("", req);
					else
						setValue(gap, req);
				}
				
			};
			this.rowGap = new TextField(page, "rowGap", "1em") {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final String gap = grid.getRowGap(req);
					if (gap == null)
						setValue("", req);
					else
						setValue(gap, req);
				}
				
			};
			this.justifyItemsDrop = new EnumDropdown<>(page, "justifyItemsDrop", JustifyItems.class);
			justifyItemsDrop.selectDefaultItem(JustifyItems.STRETCH);
			justifyItemsDrop.setDefaultToolTip("Determines the elements' horizontal alignment");
			this.alignItemsDrop = new EnumDropdown<>(page, "alignItemsDrop", AlignItems.class);
			alignItemsDrop.selectDefaultItem(AlignItems.STRETCH);
			alignItemsDrop.setDefaultToolTip("Determines the elements' vertical alignment");
			
			/**
			 * Note: the example is somewhat unusual in that we use a non-global grid here. This
			 * is so because the user can edit the CSS of the grid, and we do not want different users
			 * to interfere. In a typical app, using a static grid to structure the page content, this would 
			 * rather look like
			 * <code>
			 * 		grid = new NamedAreaGrid(page, "gridId", true);
			 * 		grid.setDefaultTemplateAreas(Arrays.asList(
			 *				"header header header",
			 *				"area0  area1  area2",
			 *				"icontoggle icon icon",
			 *				". icon icon",
			 *				"footer footer footer"
			 *	    ));
			 *		grid.addItem("header", "Header text", null).addItem("area0", someWidget, null);
			 * 		...
			 * </code>
			 */
			this.grid = new NamedAreaGrid(page, "grid", false) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					if (getItems(req).isEmpty()) {
						final Icon icon = new Icon(grid, "icon", req) {
							
							private final IconType[] icons = new IconType[] {
									IconType.CHECK_MARK,
									IconType.CLOSE,
									IconType.FAIL,
									IconType.FORBIDDEN,
									IconType.HELP_CONTENT,
									IconType.IMPORTANT,
									IconType.REFRESH,
									IconType.SETTINGS1,
									IconType.SETTINGS2
							};
							private ThreadLocal<Integer> cnt = new ThreadLocal<Integer>() {
								
								@Override
								protected Integer initialValue() {
									return 0;
								}
								
							};
							
							@Override
							public void onGET(OgemaHttpRequest req) {
								final int c = cnt.get() % icons.length;
								cnt.set(c+1);
								setIconType(icons[c], req);
							}
							
						};
						final Button toggleIconBtn = new Button(grid, "toggleIconBtn", "Toggle icon", req);
						toggleIconBtn.triggerAction(icon, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
						
						addItem("header", "Header", req)
							.addItem("area0", "Area 0", req).addItem("area1", "Area 1", req).addItem("area2", "Area2", req)
							.addItem("icontoggle", toggleIconBtn, req).addItem("icon", icon, req)
							.addItem("footer", "Footer", req);
					}
					setJustifyItems(justifyItemsDrop.getSelectedItem(req), req);
					setAlignItems(alignItemsDrop.getSelectedItem(req), req);
					final boolean append = optionsCheckbox.isChecked("fillAppend", req);
					final boolean prepend = optionsCheckbox.isChecked("fillPrepend", req);
					setAppendFillColumn(append, req);
					setPrependFillColumn(prepend, req);
					setColumnGap(columnGap.getValue(req), req);
					setRowGap(rowGap.getValue(req), req);

				}
				
			};
			grid.setDefaultTemplateAreas(Arrays.asList(
					"header header header",
					"area0  area1  area2",
					"icontoggle icon icon",
					". icon icon",
					"footer footer footer"
			));
			Map<String,String> css = Arrays.stream(new String[]{
					"background-color=#DAA520", 
					"color=white",
					"padding=2em",
					"font-weight=bold"
				})
				.map(s -> s.split("="))
				.collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
			grid.setDefaultChildPropertyArea("header", css);
			css = Arrays.stream(new String[]{
					"background-color=lightblue", 
					"color=darkblue",
					"padding=1em"
				})
				.map(s -> s.split("="))
				.collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
			grid.setDefaultChildPropertyArea("footer", css);
			css = Arrays.stream(new String[]{
					"background-color=grey", 
					"color=darkblue",
					"padding=1em"
				})
				.map(s -> s.split("="))
				.collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
			grid.setDefaultChildPropertyArea("area0", css);
			grid.setDefaultChildPropertyArea("area1", css);
			grid.setDefaultChildPropertyArea("area2", css);
			css = Arrays.stream(new String[]{
					"background-color=lightgrey", 
					"padding=1em"
				})
				.map(s -> s.split("="))
				.collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
			grid.setDefaultChildPropertyArea("toggleicon", css);
			grid.setDefaultChildPropertyArea("icon", css);
			
//			grid.setDefaultChildProperty("background-color", "#DAA520");
//			grid.setDefaultChildProperty("color", "white");
//			grid.setDefaultChildProperty("padding", "2em");
			grid.setDefaultAlignItems(AlignItems.CENTER);
			this.optionsCheckbox = new Checkbox2(page, "optionsCheckbox") {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final boolean append = grid.isAppendFillColumn(req);
					final boolean prepend = grid.isPrependFillColumn(req);
					setState("fillAppend", append, req);
					setState("fillPrepend", prepend, req);
				}
				
			};
			optionsCheckbox.setDefaultCheckboxList(Arrays.asList(
					new DefaultCheckboxEntry("fillAppend", "Append a filling column?", true),
					new DefaultCheckboxEntry("fillPrepend", "Prepend a filling column?", false)
			));
			this.settingsGrid = new SimpleGrid(page, "settingsGrid", true);
			settingsGrid.setDefaultAppendFillColumn(true);
			settingsGrid.setDefaultColumnGap("2em");
			settingsGrid.setDefaultRowGap("1em");
			
			this.cssField = new Label(page, "cssField") {
				
				public void onGET(OgemaHttpRequest req) {
					final Map<String, Map<String, String>> map = grid.getCssMap(req);
					if (map == null || map.isEmpty()) {
						setText("{}", req);
						return;
					}
					final StringBuilder sb = new StringBuilder();
					for (Map.Entry<String, Map<String,String>> outerEntry : map.entrySet()) {
						sb.append(outerEntry.getKey()).append(": {");
						for (Map.Entry<String, String> innerEntry: outerEntry.getValue().entrySet()) {
							sb.append(linebreak)
								.append(indent).append(indent).append(innerEntry.getKey()).append(':')
									.append(' ').append(innerEntry.getValue()).append(';');
						}
						sb.append(linebreak).append('}').append(linebreak);
					}
					setHtml(sb.toString(), req);	
				}
				
			};
			
			this.editAreasPopupTrigger = new Button(page, "editRowsPopupTrigger", "Edit CSS");
			this.editAreasPopup = new Popup(page, "editRowsPopup", true);
			this.editAreasSelector = new Dropdown(page, "editRowSelector");
			editAreasSelector.setDefaultOptions(Arrays.asList(
						new DropdownOption("header", "header", true),
						new DropdownOption("area0", "area0", false),
						new DropdownOption("area1", "area1", false),
						new DropdownOption("area2", "area2", false),
						new DropdownOption("icontoggle", "icontoggle", false),
						new DropdownOption("icon", "icon", false),
						new DropdownOption("footer", "footer", false),
						new DropdownOption("all", "All children", false)
				));
			this.editProperty = new TextField(page, "editProperty");
			editProperty.setDefaultToolTip("E.g. \"background-color\", \"padding\" or \"border\"");
			this.editValue = new TextField(page, "editValue");
			editValue.setDefaultToolTip("E.g. \"red\", \"2em\" or \"2px solid green\"");
			this.editSubmit = new Button(page, "editsubmit", "Submit") {
				
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					final String row = editAreasSelector.getSelectedValue(req);
					final String property = editProperty.getValue(req).trim();
					final String value = editValue.getValue(req).trim();
					if (row == null || property.isEmpty())
						return;
					if ("all".equals(row)) {
						if (value.isEmpty())
							grid.removeChildProperty(property, req);
						else
							grid.setChildProperty(property, value, req);
					}
					else {
						if (value.isEmpty())
							grid.removeChildPropertyArea(row, property, req);
						else
							grid.setChildPropertyArea(row, property, value, req);
					}
				}
				
			};
			final SimpleGrid popupGrid = new SimpleGrid(page, "popupGrid", true);
			popupGrid
				.addItem("Select an area", true, null).addItem(editAreasSelector, false, null)
				.addItem("CSS property", true, null).addItem(editProperty,  false, null)
				.addItem("CSS value", true, null).addItem(editValue, false, null);
			editAreasPopup.setTitle("Edit area CSS", null);
			editAreasPopup.setBody(popupGrid, null);
			editAreasPopup.setFooter(editSubmit, null);
			
			buildPage();
			setDependencies();
		}
	
		private final void buildPage() {
			page.append(header).linebreak().append(info).append(settingsHeader).linebreak()
				.append(settingsGrid
						.addItem("Settings", false, null).addItem(optionsCheckbox, false, null)
						.addItem("Column gap", true, null).addItem(columnGap, false, null)
						.addItem("Row gap", true, null).addItem(rowGap, false, null)
						.addItem("Justify items", true, null).addItem(justifyItemsDrop, false, null)
						.addItem("Align items", true, null).addItem(alignItemsDrop, false, null)
				).linebreak().append(gridHeader).linebreak()
				.append(grid).linebreak()
				.append(cssHeader).linebreak().append(editAreasPopupTrigger).linebreak().append(cssField)
				.linebreak().append(editAreasPopup);
		}
		
		private final void setDependencies() {
			optionsCheckbox.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			optionsCheckbox.triggerAction(cssField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
			columnGap.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			columnGap.triggerAction(cssField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
			rowGap.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			rowGap.triggerAction(cssField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
			justifyItemsDrop.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			justifyItemsDrop.triggerAction(cssField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
			alignItemsDrop.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);			
			alignItemsDrop.triggerAction(cssField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, 1);
			
			grid.triggerAction(optionsCheckbox, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
			grid.triggerAction(columnGap, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
			grid.triggerAction(rowGap, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
			
			editAreasPopupTrigger.triggerAction(editAreasPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
			editSubmit.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			editSubmit.triggerAction(cssField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			
		}
		
	}
}
