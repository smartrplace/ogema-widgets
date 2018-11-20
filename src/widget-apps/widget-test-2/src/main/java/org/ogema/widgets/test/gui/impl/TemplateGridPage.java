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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ogema.core.application.ApplicationManager;
import org.ogema.tools.resource.util.ResourceUtils;
import org.osgi.service.component.annotations.Component;

import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.checkbox.Checkbox2;
import de.iwes.widgets.html.form.checkbox.DefaultCheckboxEntry;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.dropdown.EnumDropdown;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.html5.GridStyle;
import de.iwes.widgets.html.html5.SimpleGrid;
import de.iwes.widgets.html.html5.TemplateGrid;
import de.iwes.widgets.html.html5.grid.AlignItems;
import de.iwes.widgets.html.html5.grid.JustifyItems;
import de.iwes.widgets.html.icon.Icon;
import de.iwes.widgets.html.icon.IconType;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.template.DisplayTemplate;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE, 
				LazyWidgetPage.RELATIVE_URL + "=templategrid.html",
				LazyWidgetPage.START_PAGE + "=false",
				LazyWidgetPage.MENU_ENTRY + "=Template grid page"
		}
)
public class TemplateGridPage implements LazyWidgetPage {

	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new TemplateGridPageInit(page);
	}
	
	private static class Vehicle {
		
		final String type;
		final IconType iconType;
		final ThreadLocal<Integer> users = new ThreadLocal<Integer>() {
			
			@Override
			protected Integer initialValue() {
				return 0;
			}
			
		};
		
		Vehicle(String type, IconType iconType) {
			this.type = Objects.requireNonNull(type);
			this.iconType = iconType;
		}
		
		@Override
		public String toString() {
			return "Vehicle [" + type + "]";
		}
		
	}
	
	private static class TemplateGridPageInit {

		private static List<Vehicle> vehicles = Arrays.asList(
				new Vehicle("Bicycle", IconType.CHECK_MARK),
				new Vehicle("Car", IconType.FORBIDDEN),
				new Vehicle("Boat", IconType.IMPORTANT),
				new Vehicle("Bobby Car", IconType.HELP_CONTENT),
				new Vehicle("Mule", IconType.FAIL)
		);
		
		private final WidgetPage<?> page;
		private final Alert info;
		private final Header header;
		private final Header settingsHeader;
		private final Header gridHeader;
		private final TemplateGrid<Vehicle> grid;
		private final Checkbox2 optionsCheckbox;
		private final EnumDropdown<JustifyItems> justifyItemsDrop;
		private final EnumDropdown<AlignItems> alignItemsDrop;
		private final SimpleGrid settingsGrid;
		private final TextField columnGap;
		private final TextField rowGap;
		private final TemplateDropdown<GridStyle> styleSelector;
		private final TemplateMultiselect<Vehicle> vehicleSelector;
		
		private final Button editRowsPopupTrigger;
		private final Popup editRowsPopup;
		private final Dropdown editRowsOrColumns;
		private final Dropdown editRowSelector;
		private final TextField editProperty;
		private final TextField editValue;
		private final Button editSubmit;
		
		private final RowTemplate<Vehicle> template = new RowTemplate<TemplateGridPage.Vehicle>() {
			
			private final Map<String, Object> header;
			
			{
				header = Collections.unmodifiableMap(Arrays.stream(new String[] {
						"type=Type",
						"cnt=Users",
						"increment=Increment",
						"icon=Icon"
					})
					.map(str -> str.split("="))
					.collect(Collectors.toMap(arr -> arr[0], arr -> arr[1], (u,v) -> null, LinkedHashMap::new)));
			}
			
			@SuppressWarnings("serial")
			@Override
			public Row addRow(Vehicle object, OgemaHttpRequest req) {
				final Row row = new Row();
				final String id = getLineId(object);
				final Label typeLabel = new Label(grid, id + "_type", req) {
					
					public void onGET(OgemaHttpRequest req) {
						setText(object.type, req);
					}
					
				};
				row.addCell("type", typeLabel);
				final Label cntLabel = new Label(grid, id + "_cnt", req) {
					
					public void onGET(OgemaHttpRequest req) {
						setText(object.users.get() + "", req);
					}
					
				};
				row.addCell("cnt", cntLabel);
				final Button increment = new Button(grid, id + "_increment", "Increment", req) {
					
					public void onPOSTComplete(String data, OgemaHttpRequest req) {
						object.users.set(object.users.get() + 1);
					}
					
				};
				row.addCell("increment",increment);
				increment.triggerAction(cntLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
				final Icon icon = new Icon(grid, id + "_icon", req);
				icon.setDefaultIconType(object.iconType);
				row.addCell("icon", icon);
				return row;
			}

			@Override
			public String getLineId(Vehicle object) {
				return ResourceUtils.getValidResourceName(object.type);
			}

			@Override
			public Map<String, Object> getHeader() {
				return header;
			}
			
		};
		
		@SuppressWarnings("serial")
		TemplateGridPageInit(final WidgetPage<?> page) {
			this.page = page;
			this.info = new Alert(page, "info", 
					"This page illustrates the use of the <i>TemplateGrid</i> widget. It makes use of the <code>display:grid</code>"
					+ " CSS property. See <a href=\"https://css-tricks.com/snippets/css/complete-guide-grid/\">"
					+ "A complete guide to grid</a> for details.<br>"
					+ "It uses the grid display type to create tabular-like grids. The rows are modeled on a template class, "
					+ "with one row per instance of the class."
					+ "Confer also the <i><a href=\"" + Constants.URL_BASE +"/grid.html\">SimpleGrid</a></i> "
					+ " and <i><a href=\"" + Constants.URL_BASE + "/areagrid.html\">NamedAreaGrid</a></i> widgets."
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
			this.vehicleSelector = new TemplateMultiselect<>(page, "vehicleSelector");
			vehicleSelector.setDefaultMinWidth("25vw");
			vehicleSelector.selectDefaultItems(vehicles);
			vehicleSelector.setTemplate(new DisplayTemplate<TemplateGridPage.Vehicle>() {
				
				@Override
				public String getLabel(Vehicle object, OgemaLocale locale) {
					return object.type;
				}
				
				@Override
				public String getId(Vehicle object) {
					return ResourceUtils.getValidResourceName(object.type);
				}
			});
			vehicleSelector.setDefaultSelectedItems(vehicles);
			this.columnGap = new TextField(page, "colGap", "")  {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final String gap = grid.getColumnGap(req);
					if (gap == null)
						setValue("", req);
					else
						setValue(gap, req);
				}
				
			};
			columnGap.setDefaultToolTip("Size, such as \"2em\", \"5px\" or \"3vw\"");
			this.rowGap = new TextField(page, "rowGap", "") {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final String gap = grid.getRowGap(req);
					if (gap == null)
						setValue("", req);
					else
						setValue(gap, req);
				}
				
			};
			rowGap.setDefaultToolTip("Size, such as \"2em\", \"5px\" or \"3vh\"");
			this.justifyItemsDrop = new EnumDropdown<>(page, "justifyItemsDrop", JustifyItems.class);
			justifyItemsDrop.selectDefaultItem(JustifyItems.STRETCH);
			justifyItemsDrop.setDefaultToolTip("Determines the elements' horizontal alignment");
			this.alignItemsDrop = new EnumDropdown<>(page, "alignItemsDrop", AlignItems.class);
			alignItemsDrop.selectDefaultItem(AlignItems.STRETCH);
			alignItemsDrop.setDefaultToolTip("Determines the elements' vertical alignment");
			this.grid = new TemplateGrid<Vehicle>(page, "grid", false, template) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					update(vehicleSelector.getSelectedItems(req), req);
					final boolean append = optionsCheckbox.isChecked("fillAppend", req);
					final boolean prepend = optionsCheckbox.isChecked("fillPrepend", req);
					setAppendFillColumn(append, req);
					setPrependFillColumn(prepend, req);
					setColumnGap(columnGap.getValue(req), req);
					setRowGap(rowGap.getValue(req), req);
					setJustifyItems(justifyItemsDrop.getSelectedItem(req), req);
					setAlignItems(alignItemsDrop.getSelectedItem(req), req);
					GridStyle style = styleSelector.getSelectedItem(req);
					if (style == null) 
						style = GridStyle.newBuilder().setPadding(null).setBorder(null).build("none", "None");
					setGridStyle(style, req);
				}
				
			};
			grid.setDefaultChildPropertyRow(DynamicTable.HEADER_ROW_ID, "font-weight", "bold");
			grid.setDefaultChildPropertyRow(DynamicTable.HEADER_ROW_ID, "color", "darkslategrey");
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
			this.styleSelector = new TemplateDropdown<>(page, "styleSelector");
			styleSelector.setDefaultItems(Arrays.asList(
					GridStyle.TABULAR_EMPTY,
					GridStyle.TABULAR_GREY,
					GridStyle.TABULAR_RED,
					GridStyle.TABULAR_BLUE,
					GridStyle.TABULAR_GREEN,
					GridStyle.TABULAR_YELLOW
			));
			styleSelector.selectDefaultItem(GridStyle.TABULAR_BLUE);
			styleSelector.setDefaultAddEmptyOption(true, "None");
			
			this.settingsGrid = new SimpleGrid(page, "settingsGrid", true);
			settingsGrid.setDefaultAppendFillColumn(true);
			settingsGrid.setDefaultColumnGap("4em");
			settingsGrid.setDefaultRowGap("0.5em");
			
			this.editRowsPopupTrigger = new Button(page, "editRowsPopupTrigger", "Edit rows/columns");
			this.editRowsPopup = new Popup(page, "editRowsPopup", true);
			this.editRowsOrColumns = new Dropdown(page, "editRowsOrColumns");
			editRowsOrColumns.setDefaultOptions(Arrays.asList(
					new DropdownOption("row", "Row", true),
					new DropdownOption("col", "Column", false)
			));
			this.editRowSelector = new Dropdown(page, "editRowSelector") {
				
				private final DropdownOption headerOpt = new DropdownOption(DynamicTable.HEADER_ROW_ID, "Header", false);
				
				private final List<DropdownOption> colOptions = Arrays.asList(
						new DropdownOption("type", "Type", true),
						new DropdownOption("cnt", "Counter", false),
						new DropdownOption("increment", "Increment button", false),
						new DropdownOption("icon", "Icon", false)
				);
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final boolean isRow = "row".equals(editRowsOrColumns.getSelectedValue(req));
					if (isRow) {
						setOptions(Stream.concat(Stream.of(headerOpt),
									vehicleSelector.getSelectedItems(req).stream()
									.map(vehicle -> new DropdownOption(ResourceUtils.getValidResourceName(vehicle.type), vehicle.type, false)))
								.collect(Collectors.toList()), req);
					} else {
						setOptions(colOptions, req);
					}
				}
				
			};
			this.editProperty = new TextField(page, "editProperty");
			editProperty.setDefaultToolTip("E.g. \"background-color\", \"padding\" or \"border\"");
			this.editValue = new TextField(page, "editValue");
			editValue.setDefaultToolTip("E.g. \"red\", \"2em\" or \"2px solid green\"");
			this.editSubmit = new Button(page, "editsubmit", "Submit") {
				
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					final boolean isRow = "row".equals(editRowsOrColumns.getSelectedValue(req));
					final String row = editRowSelector.getSelectedValue(req);
					final String property = editProperty.getValue(req).trim();
					final String value = editValue.getValue(req).trim();
					if (row == null || property.isEmpty() || value.isEmpty())
						return;
					if (isRow)
						grid.setChildPropertyRow(row, property, value, req);
					else
						grid.setChildPropertyCol(row, property, value,req );
				}
				
			};
			final SimpleGrid popupGrid = new SimpleGrid(page, "popupGrid", true);
			popupGrid.addItem("Row or column?", false, null).addItem(editRowsOrColumns, false, null)
				.addItem("Select a row/column", true, null).addItem(editRowSelector, false, null)
				.addItem("CSS property", true, null).addItem(editProperty,  false, null)
				.addItem("CSS value", true, null).addItem(editValue, false, null);
			editRowsPopup.setTitle("Edit rows/columns", null);
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
						.addItem("Select grid style", true, null).addItem(styleSelector, false, null)
						.addItem("Selected items", true, null).addItem(vehicleSelector, false, null)
						.addItem(editRowsPopupTrigger, true, null)
				).linebreak().append(gridHeader).linebreak()
				.append(grid).linebreak().append(editRowsPopup);
		}
		
		private final void setDependencies() {
			optionsCheckbox.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			columnGap.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			rowGap.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			justifyItemsDrop.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			alignItemsDrop.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);			
			styleSelector.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			
			vehicleSelector.triggerAction(editRowSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			vehicleSelector.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			
			grid.triggerAction(optionsCheckbox, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
			grid.triggerAction(columnGap, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
			grid.triggerAction(rowGap, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
			
			editRowsPopupTrigger.triggerAction(editRowsPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
			editRowsOrColumns.triggerAction(editRowSelector, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//			editSubmit.triggerAction(editRowsPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
			editSubmit.triggerAction(grid, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
		
	}
}
