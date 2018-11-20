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
	 * 		either a TemplateMultiselect&lt;SelectionItem&gt; or TemplateDropdown&lt;SelectionItem&gt;, or null if empty
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
