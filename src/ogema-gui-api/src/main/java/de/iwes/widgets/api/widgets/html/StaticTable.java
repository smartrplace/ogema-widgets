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

package de.iwes.widgets.api.widgets.html;

import de.iwes.widgets.api.widgets.OgemaWidget;

/**
 * A table of fixed size (rows and columns). The content
 * of the table should not be modified after being set once.
 * Use a widget instead for more flexible tables. See for instance
 * {@see org.ogema.tools.widget.html.complextable.DynamicTable}, 
 * or {@see org.ogema.tools.widget.html.datatable.DataTable}.
 */
public class StaticTable extends HtmlItem {
	
	public static final HtmlStyle TABLE_STRIPED = new HtmlStyle("class","table-striped");
	
	private static final HtmlStyle rowStyle = new HtmlStyle("class","row");
	private static final HtmlStyle tableStyle = new HtmlStyle("class","table");
	private static final HtmlStyle noBorder = new HtmlStyle("style","border:none;");
	
	public StaticTable(int rows, int columns) {
		this(rows, columns, new int[] {});
	}
	
	/** 
	 * 
	 * @param rows
	 * 		nr of rows
	 * @param columns
	 * 		nr of columns
	 * @param sizes
	 * 		sizes of columns; should add up to 12; sizes.length must be equal to columns 
	 */
	public StaticTable(int rows, int columns, int[] sizes) {
		super("table");
		addStyle(tableStyle);
		if (sizes.length == 0) {
			sizes = new int[columns];
			int newSize = 1;
			if (columns < 12) {
				newSize = 12 / columns;
			}
			for (int i=0;i<columns;i++) {
				sizes[i] = newSize;
			}
		}
		else if (sizes.length != columns) {
			throw new RuntimeException("Invalid column sizes provided");
		} 	
		for (int i=0;i<rows;i++) {
			HtmlItem row = new HtmlItem("tr");
			row.addStyle(rowStyle);
			for (int j=0;j<columns;j++) {
				HtmlItem col = new HtmlItem("td");
				col.addStyle(new HtmlStyle("class", "col col-sm-" + sizes[j]));
				row.addSubItem(col);
			}
//			rowsList.add(row);
			this.addSubItem(row);
		}
	}
	
	public StaticTable setContent(int row, int col, String text) {
		HtmlItem cell = getCell(row,col);
		cell.addSubItem(text);
		return this;
	}
	
	public StaticTable setContent(int row, int col, HtmlItem item) {
		HtmlItem cell = getCell(row,col);
		cell.addSubItem(item);
		return this;
	}
	
	public StaticTable setContent(int row,int col, OgemaWidget widget) {
		HtmlItem cell = getCell(row,col);
		cell.addSubItem(widget);
		return this;
	}
	
	public StaticTable addStyle(int row, int col, HtmlStyle style) {
		HtmlItem cell = getCell(row,col);
		cell.addStyle(style);
		return this;
	}
	
	public StaticTable removeBorder() {   // ugly
		int rwNr = 0;
		while (true) {
			Object row;
			try {
				row = getSubItem(rwNr);  // may throw an IllegalArgumentException			
			} catch (IllegalArgumentException e) {
				break;
			}
			if (!(row instanceof HtmlItem))
				break;
//			if (row == null || !(row instanceof HtmlItem)) break;
			HtmlItem rw = (HtmlItem) row;
			int clNr = 0;
			while (true) {
				Object col;
				try {
					col = rw.getSubItem(clNr);  // may throw an IllegalArgumentException			
				} catch (IllegalArgumentException e) {
					break;
				}
//				if (col == null || !(col instanceof HtmlItem)) break;
				if (!(col instanceof HtmlItem))
					break;
				HtmlItem cl = (HtmlItem) col;
				cl.addStyle(noBorder);
				clNr++;
			}
			rwNr++;
		}
		return this;
	}
	
	
	private HtmlItem getCell(int row,int col) {
		HtmlItem rw = (HtmlItem) this.getSubItem(row);
//		HtmlItem rw = rowsList.get(row);
		return (HtmlItem) rw.getSubItem(col);	
	}
	
	@Override
	public String toString() {
		return getHtml();
	}
	
}
