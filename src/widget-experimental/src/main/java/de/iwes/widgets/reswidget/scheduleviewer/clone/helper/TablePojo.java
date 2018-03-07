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
package de.iwes.widgets.reswidget.scheduleviewer.clone.helper;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.html.form.label.Label;

public class TablePojo<T extends OgemaWidget> { 

	private final Label label;
	private final String id;
	private final PageSnippet snippet;
	private static int COUNT = 0;
	
	public TablePojo(String id, Label label, OgemaWidgetBase<?> widget, WidgetPage<?> page){		
		this(id, label, widget, null, page);
	}
	
	public TablePojo(String id, Label label, OgemaWidgetBase<?> widget, OgemaWidgetBase<?> widgetOptional, WidgetPage<?> page){		
		this.label = label;	
		this.id= "id_"+TablePojo.COUNT++ +"_table_"+id;
		this.snippet = initSnippet(page, widget, widgetOptional);
	}

	public Label getLabel() {
		return label;
	}


	public String getId() {
		return id;
	}
	
	public PageSnippet getSnippet() {
		return snippet;
	}

	private PageSnippet initSnippet(WidgetPage<?> page, OgemaWidget w1, OgemaWidget w2) {
		PageSnippet snippet = new PageSnippet(page, id, true);
		if(w1 != null) {
			snippet.append(w1, null);
		}
		if(w2 != null) {
			snippet.append(w2, null);
		}
		return snippet;		
	}
	
}
