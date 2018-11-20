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
package de.iwes.widgets.html.html5;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.template.LabelledItem;

/**
 * A helper class for setting several CSS properties of an {@link AbstractGrid}.
 * See AbstractGrid#setGridStyle // TODO
 */
// TODO text color, ...
public class GridStyle implements LabelledItem {
	
	public static final GridStyle TABULAR_EMPTY = GridStyle.newBuilder().build("tab_empty", "Table empty");
	public static final GridStyle TABULAR_GREY = GridStyle.newBuilder()
			.setBackgroundColor("#ECEFF1", "#CFD8DC")
			.build("tab_grey", "Table grey");
	public static final GridStyle TABULAR_RED = GridStyle.newBuilder()
			.setBackgroundColor("#FFEBEE", "#FFCDD2")
			.setBorder("1px solid darkred")
			.build("tab_red", "Table red");
	public static final GridStyle TABULAR_GREEN = GridStyle.newBuilder()
			.setBackgroundColor("#E8F5E9", "#C8E6C9")
			.setBorder("1px solid darkgreen")
			.build("tab_green", "Table green");
	public static final GridStyle TABULAR_BLUE = GridStyle.newBuilder()
			.setBackgroundColor("#E1F5FE", "#B3E5FC")
			.setBorder("1px solid darkblue")
			.build("tab_blue", "Table blue");
	public static final GridStyle TABULAR_YELLOW = GridStyle.newBuilder()
			.setBackgroundColor("#FFFDE7", "#FFF59D")
			.setBorder("1px solid orange")
			.build("tab_ylw", "Table yellow");
	
	
	private final String id;
	private final String label;
	private final String backgroundColor0;
	private final String backgroundColor1;
	private final String border;
	private final String padding;
	
	protected GridStyle(String id, String label, String backgroundColor0, String backgroundColor1, String border, String padding) {
		this.id = id;
		this.label = label;
		this.backgroundColor0 = backgroundColor0;
		this.backgroundColor1 = backgroundColor1;
		this.border = border;
		this.padding = padding;
	}
	
	public static GridStyleBuilder newBuilder() {
		return new GridStyleBuilder();
	}

	public static class GridStyleBuilder {
		
		// default values
		private String backgroundColor0;
		private String backgroundColor1;
		private String border = "1px solid darkgrey";
		private String padding = "1em";
		
		GridStyleBuilder() {}
		
		public GridStyle build(String id, String label) {
			return new GridStyle(
					id,
					label,
					backgroundColor0, 
					backgroundColor1, 
					border, 
					padding);
		}
		
		/**
		 * Set the CSS <i>background-color</i> property for the grid cells.
		 * @param backgroundColor0
		 *  	background color for even rows
		 * @param backgroundColor1
		 * 		background color for odd rows
		 * @return
		 */
		public GridStyleBuilder setBackgroundColor(String backgroundColor0, String backgroundColor1) {
			this.backgroundColor0 = backgroundColor0;
			this.backgroundColor1 = backgroundColor1;
			return this;
		}
		
		/**
		 * Set the CSS <i>border</i> property for the grid cells. Default value: "1px solid darkgrey"
		 * @param border
		 * @return
		 */
		public GridStyleBuilder setBorder(String border) {
			this.border = border;
			return this;
		}
		
		/**
		 * Set the CSS <i>padding</i> property for the grid cells. Default: "1em"
		 * @param padding	
		 * 		a valid CSS size, such as "2px", "3em" or "3vw", or null to remove the property
		 * @return
		 */
		public GridStyleBuilder setPadding(String padding) {
			this.padding = padding;
			return this;
		}
		
	}
	
	public String getBackgroundColor0() {
		return backgroundColor0;
	}
	
	public String getBackgroundColor1() {
		return backgroundColor1;
	}
	
	public String getBorder() {
		return border;
	}
	
	public String getPadding() {
		return padding;
	}
	
	@Override
	public String id() {
		return id;
	}
	
	@Override
	public String label(OgemaLocale locale) {
		return label;
	}
}
