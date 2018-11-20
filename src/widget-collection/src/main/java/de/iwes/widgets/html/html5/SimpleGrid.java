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

import java.util.List;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.html5.grid.Content;

/**
 * A special grid type, typically used to position elements on a page in a
 * static way. Example usage:<br>
 * <code>
 * 		new SimpleGrid(page, gridId, true)
 * 			.addItem(text0, true, null).addItem(widget0, false, null)
 * 			.addItem(text1, true, null).addItem(widget1, false, null);
 * </code>
 * See {@link AbstractGrid} for more grid types.
 */
public class SimpleGrid extends AbstractGrid {

	private static final long serialVersionUID = 1L;

	public SimpleGrid(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		setDefaultColumnGap("2em");
		setDefaultRowGap("1em");
	}

	public SimpleGrid(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
		setDefaultColumnGap("2em");
		setDefaultRowGap("1em");
	}

	public SimpleGrid addItem(OgemaWidget item, boolean newRow, OgemaHttpRequest req) {
		getData(req).addItem(item, newRow);
		return this;
	}

	public SimpleGrid addItem(String item, boolean newRow, OgemaHttpRequest req) {
		getData(req).addItem(item, newRow);
		return this;
	}

	public SimpleGrid addItem(HtmlItem item, boolean newRow, OgemaHttpRequest req) {
		getData(req).addItem(item, newRow);
		return this;
	}

	public List<List<Content>> getItems(OgemaHttpRequest req) {
		return getData(req).getItems();
	}

	/**
	 * Insert a new item to the grid. Existing items are not replaced, but may be moved to the right.
	 * @param row
	 * @param col
	 * @param item
	 * @throws IndexOutOfBoundsException if row is larger than
	 */
	public SimpleGrid addItem(int row, int col, OgemaWidget item, OgemaHttpRequest req) {
		getData(req).addItem(row, col, item);
		return this;
	}

	/**
	 * Insert a new item to the grid. Existing items are not replaced, but may be moved to the right.
	 * @param row
	 * @param col
	 * @param item
	 * @throws IndexOutOfBoundsException if row is larger than
	 */
	public SimpleGrid addItem(int row, int col, String item, OgemaHttpRequest req) {
		getData(req).addItem(row, col, item);
		return this;
	}

	/**
	 * Insert a new item to the grid. Existing items are not replaced, but may be moved to the right.
	 * @param row
	 * @param col
	 * @param item
	 * @throws IndexOutOfBoundsException if row is larger than
	 */
	public SimpleGrid addItem(int row, int col, HtmlItem item, OgemaHttpRequest req) {
		getData(req).addItem(row, col, item);
		return this;
	}

	/**
	 * @param row
	 * @param col
	 * @throws IndexOutOfBoundsException
	 */
	public void deleteItem(int row, int col, OgemaHttpRequest req) {
		getData(req).deleteItem(row, col);
	}

	public boolean deleteItem(OgemaWidget item, OgemaHttpRequest req) {
		return getData(req).deleteItem(item);
	}
	
	@Override
	public void setDefaultGridStye(GridStyle style) {
		super.setDefaultGridStye(style);
		setDefaultColumnGap("0em");
		setDefaultRowGap("0em");
	}

}
