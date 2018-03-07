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
package de.iwes.widgets.html.selectiontree;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.iwes.widgets.api.extended.plus.MultiSelectorTemplate;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.flexbox.AlignItems;
import de.iwes.widgets.html.html5.flexbox.FlexWrap;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;

public class SelectionTree extends Flexbox {

	private static final long serialVersionUID = 1L;

	public SelectionTree(WidgetPage<?> page, String id) {
		super(page, id);
		initSettings();
	}
	
	public SelectionTree(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
		initSettings();
	}
	
	public SelectionTree(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
		initSettings();
	}
	
	/**
	 * Overwrite if required. This function is executed every time the terminal select 
	 * widget is updated.
	 */
	protected void onTerminalFieldGetComplete(OgemaHttpRequest req) {}
	
	// can still be overwritten
	private final void initSettings() {
		setDefaultJustifyContent(JustifyContent.CENTER);
		setDefaultAlignItems(AlignItems.FLEX_LEFT); // here: vertical alignment: top (since flex-direction is row by default)
		setDefaultFlexWrap(FlexWrap.WRAP);
	}
	
	@Override
	public SelectionTreeData createNewSession() {
		return new SelectionTreeData(this);
	}
	
	@Override
	public SelectionTreeData getData(OgemaHttpRequest req) {
		return (SelectionTreeData) super.getData(req);
	}
	
	public void setSelectionOptions(Collection<LinkingOption> opts, OgemaHttpRequest req) {
		getData(req).setSelectionOptions(opts, req);
	}
	
	public Map<LinkingOption, MultiSelectorTemplate<SelectionItem>> getSelectionOptions(OgemaHttpRequest req) {
		return getData(req).getSelectionOptions();
	}
	
	/**
	 * May return null, if either the terminal selection option is not of type
	 * {@link TerminalOption}, or no options have been set at all.
	 * @param req
	 * @return
	 */
	public TerminalOption<?> getTerminalOption(OgemaHttpRequest req) {
		return getData(req).getTerminalOption();
	}
	
	/**
	 * The last select widget in the hierarchie (the one with the most dependencies)
	 * @param req
	 * @return
	 * 		either a TemplateMultiselect<SelectionItem> or TemplateDropdown<SelectionItem>, or null if empty
	 */
	public OgemaWidget getTerminalSelectWidget(OgemaHttpRequest req) {
		return getData(req).getTerminalSelectWidget();
	}
	
	/**
	 * The list of select widgets, which are all either Multiselects or Dropdowns.
	 * @param req
	 * @return
	 */
	public List<OgemaWidget> getSelectWidgets(OgemaHttpRequest req) {
		return getData(req).getSelectWidgets();
	}
	
}
