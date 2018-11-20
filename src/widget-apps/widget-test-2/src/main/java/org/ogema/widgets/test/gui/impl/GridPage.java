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
import java.util.Collections;

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
import de.iwes.widgets.html.html5.SimpleGrid;
import de.iwes.widgets.html.html5.grid.AlignItems;
import de.iwes.widgets.html.html5.grid.JustifyItems;
import de.iwes.widgets.html.popup.Popup;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE, 
				LazyWidgetPage.RELATIVE_URL + "=grid.html",
				LazyWidgetPage.START_PAGE + "=false",
				LazyWidgetPage.MENU_ENTRY + "=Grid page"
		}
)
public class GridPage implements LazyWidgetPage {

	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new GridPageInit(page);
	}
	
	private static class GridPageInit {
	
		private final WidgetPage<?> page;
		private final Alert info;
		private final Header header;
		private final Header settingsHeader;
		private final Header gridHeader;
		private final SimpleGrid grid;
		private final Checkbox2 optionsCheckbox;
		private final SimpleGrid settingsGrid;
		private final EnumDropdown<JustifyItems> justifyItemsDrop;
		private final EnumDropdown<AlignItems> alignItemsDrop;
		private final TextField columnGap;
		private final TextField rowGap;
		private final TextField borderRadius;
		
		private final Button removeAllPropsButton;
		private final Button editRowsPopupTrigger;
		private final Popup editRowsPopup;
		private final Dropdown editRowSelector;
		private final TextField editProperty;
		private final TextField editValue;
		private final Button editSubmit;
		
		@SuppressWarnings("serial")
		GridPageInit(final WidgetPage<?> page) {
			this.page = page;
			this.info = new Alert(page, "info", 
					"This page illustrates the use of the <i>SimpleGrid</i> widget. It makes use of the <code>display:grid</code>"
					+ " CSS property. See <a href=\"https://css-tricks.com/snippets/css/complete-guide-grid/\">"
					+ "A complete guide to grid</a> for details.<br>"
					+ "The <i>SimpleGrid</i> is mostly used to position items (widgets) statically on a page, although it "
					+ "supports dynamic content as well. Confer also the <i><a href=\"" + Constants.URL_BASE + "/templategrid.html\">TemplateGrid</a></i>"
					+ " widget for dynamic content. The <i><a href=\"" + Constants.URL_BASE + "/areagrid.html\">NamedAreaGrid</a></i> "
					+ "on the other hand enables names for grid areas, and items that span more than one cell."
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
			this.borderRadius = new TextField(page, "borderRadius", "2px");
			/**
			 * Note: the example is somewhat unusual in that we use a non-global grid here. This
			 * is so because the user can edit the CSS of the grid, and we do not want different users
			 * to interfere. In a typical app, using a static grid to structure the page content, this would 
			 * rather look like
			 * <code>
			 * 		grid = new SimpleGrid(page, "gridId", true);
			 * 		grid.addItem("Some text", false, null).addItem(someWidget, false, null);
			 * 		...
			 * </code>
			 */
			this.grid = new SimpleGrid(page, "grid", false) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					if (getItems(req).isEmpty()) {
						addItem("Col 1", false, req).addItem("Col 2", false, req).addItem("Col 3", false, req);
						
						final Button triggerBtn = new Button(grid, "triggerBtn", "Click me", req);
						final Label triggeredLabel = new Label(grid, "triggeredLabel", "Button has not been clicked yet", req) {
							
							private int cnt = 0;
							
							@Override
							public void onGET(OgemaHttpRequest req) {
								if (triggerBtn.equals(page.getTriggeringWidget(req))) {
									setText("Button has been pressed " + ++cnt + " times.", req);
								}
							}
							
						};
						triggerBtn.triggerAction(triggeredLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
						
						addItem("Button and text example", true, req).addItem(triggerBtn, false, req).addItem(triggeredLabel, false, req);
						
					}
					setJustifyItems(justifyItemsDrop.getSelectedItem(req), req);
					setAlignItems(alignItemsDrop.getSelectedItem(req), req);
					final boolean append = optionsCheckbox.isChecked("fillAppend", req);
					final boolean prepend = optionsCheckbox.isChecked("fillPrepend", req);
					setAppendFillColumn(append, req);
					setPrependFillColumn(prepend, req);
					setColumnGap(columnGap.getValue(req), req);
					setRowGap(rowGap.getValue(req), req);
					if (!editSubmit.equals(page.getTriggeringWidget(req))) {
						if (optionsCheckbox.isChecked("showBorder", req))
							setChildProperty("border", "5px solid #FF8C00", req);
						else
							removeChildProperty("border", req);
						final String r = borderRadius.getValue(req);
						if (r != null && !r.isEmpty())
							setChildProperty("border-radius", r, req);
						else
							removeChildProperty("border-radius", req);
					}

				}
				
			};
			grid.setDefaultChildProperty("background-color", "#DAA520");
			grid.setDefaultChildProperty("color", "white");
			grid.setDefaultChildProperty("padding", "2em");
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
					new DefaultCheckboxEntry("fillPrepend", "Prepend a filling column?", false),
					new DefaultCheckboxEntry("showBorder", "Show border?", true)
			));
			this.settingsGrid = new SimpleGrid(page, "settingsGrid", true);
			settingsGrid.setDefaultAppendFillColumn(true);
			settingsGrid.setDefaultColumnGap("2em");
			settingsGrid.setDefaultRowGap("1em");
			
			this.removeAllPropsButton = new Button(page, "removeAllProps", "Reset all CSS properties") {
				
				@Override
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					grid.setCssMap(Collections.singletonMap(">div", Collections.singletonMap("display", "grid")), req);
				}
				
			};
			this.editRowsPopupTrigger = new Button(page, "editRowsPopupTrigger", "Edit rows");
			this.editRowsPopup = new Popup(page, "editRowsPopup", true);
			this.editRowSelector = new Dropdown(page, "editRowSelector");
			editRowSelector.setDefaultOptions(Arrays.asList(
					new DropdownOption("all", "All rows", true),
					new DropdownOption("even", "Even rows", false),
					new DropdownOption("odd", "Odd rows", false)
				));
			this.editProperty = new TextField(page, "editProperty");
			editProperty.setDefaultToolTip("E.g. \"background-color\", \"padding\" or \"border\"");
			this.editValue = new TextField(page, "editValue");
			editValue.setDefaultToolTip("E.g. \"red\", \"2em\" or \"2px solid green\"");
			this.editSubmit = new Button(page, "editsubmit", "Submit") {
				
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					final String row = editRowSelector.getSelectedValue(req);
					final String property = editProperty.getValue(req).trim();
					if (row == null || property == null || property.isEmpty())
						return;
					final String value = editValue.getValue(req).trim();
					final boolean all = "all".equals(editRowSelector.getSelectedValue(req));
					if (all) {
						if (value.isEmpty())
							grid.removeChildProperty(property, req);
						else
							grid.setChildProperty(property, value, req);
					} else {
						final boolean evenOrOdd = "even".equals(editRowSelector.getSelectedValue(req));
						if (value.isEmpty())
							grid.removeChildPropertyAlternatingRows(evenOrOdd, property, req);
						else
							grid.setChildPropertyAlternatingRows(evenOrOdd, property, value, req);
					}
					
				}
				
			};
			final SimpleGrid popupGrid = new SimpleGrid(page, "popupGrid", true);
			popupGrid
				.addItem("Even or odd rows?", true, null).addItem(editRowSelector, false, null)
				.addItem("CSS property", true, null).addItem(editProperty,  false, null)
				.addItem("CSS value", true, null).addItem(editValue, false, null);
			editRowsPopup.setTitle("Edit alternating rows", null);
			editRowsPopup.setBody(popupGrid, null);
			editRowsPopup.setFooter(editSubmit, null);
			
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
						.addItem("Border radius", true, null).addItem(borderRadius, false, null)
						.addItem(removeAllPropsButton, true, null).addItem(editRowsPopupTrigger, false, null)
				).linebreak().append(gridHeader).linebreak()
				.append(grid).linebreak().append(editRowsPopup);
		}
		
		private final void setDependencies() {
			optionsCheckbox.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			columnGap.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			rowGap.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			borderRadius.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			justifyItemsDrop.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			alignItemsDrop.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);			
			
			grid.triggerAction(optionsCheckbox, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
			grid.triggerAction(columnGap, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
			grid.triggerAction(rowGap, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
			
			removeAllPropsButton.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			editRowsPopupTrigger.triggerAction(editRowsPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
			editSubmit.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
		
	}
}
