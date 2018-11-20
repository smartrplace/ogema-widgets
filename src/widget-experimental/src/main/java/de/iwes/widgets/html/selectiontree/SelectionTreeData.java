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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.ogema.tools.resource.util.ResourceUtils;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.plus.MultiSelectorTemplate;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.container.Container;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.html5.FlexboxData;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.template.DisplayTemplate;

public class SelectionTreeData extends FlexboxData {

	private final JSONObject json = new JSONObject();
	private final Map<LinkingOption, ItemField> pageSnippet = new LinkedHashMap<>();
	private ItemField terminalField;
	
	public SelectionTreeData(SelectionTree flexbox) {
		super(flexbox);
	}
	
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		return json;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Map<LinkingOption, MultiSelectorTemplate<SelectionItem>> getSelectionOptions() {
		readLock();
		try {
			return new LinkedHashMap(pageSnippet);
		} finally {
			readUnlock();
		}
	}
	
	protected void setSelectionOptions(final Collection<LinkingOption> opts, final OgemaHttpRequest req) {
		writeLock();
		try {
			final Iterator<ItemField> it = pageSnippet.values().iterator();
			while (it.hasNext()) {
				final ItemField i = it.next();
				if (!opts.contains(i.opt)) {
					removeItem(i);
					it.remove();
				}
			}
//			this.pageSnippet.clear();
			final List<LinkingOption> list = new ArrayList<>(opts);
			Collections.sort(list);
			for (LinkingOption l : list) {
				if (pageSnippet.keySet().contains(l))
					continue;
				final ItemField ifi;
				if (widget.isGlobalWidget())
					ifi = new ItemField(widget.getPage(), widget.getId() + "_" + ResourceUtils.getValidResourceName(l.id()), l);
				else
					ifi = new ItemField(widget, widget.getId() + "_" + ResourceUtils.getValidResourceName(l.id()), req, l);
				pageSnippet.put(l, ifi);
				terminalField = ifi;
				triggerTransitiveDependencies(l, ifi);
				addItem(ifi);
				setMargin(null, "8px", ifi);
			}
		} finally {
			writeUnlock();
		}
	}
	
	protected OgemaWidget getTerminalSelectWidget() {
		readLock();
		try {
			return terminalField != null ? terminalField.getSelectWidget() : null;
		} finally {
			readUnlock();
		}
	}
	
	protected TerminalOption<?> getTerminalOption() {
		readLock();
		try {
			return (TerminalOption<?>) (terminalField != null && terminalField.opt instanceof TerminalOption<?> ? 
					terminalField.opt : null);
		} finally {
			readUnlock();
		}
	}
	
	
	protected List<OgemaWidget> getSelectWidgets() {
		final List<OgemaWidget> widgets = new ArrayList<>();
		readLock();
		try {
			for (ItemField ifi: pageSnippet.values()) {
				widgets.add(ifi.getSelectWidget());
			}
		} finally {
			readUnlock();
		}
		return widgets;
	}
	
	private final void triggerTransitiveDependencies(final LinkingOption l, final ItemField ifi) {
		final LinkingOption[] dependencies = l.dependencies();
		if (dependencies != null) {
			for (LinkingOption dep: dependencies) {
				if (dep == null)
					continue;
				final ItemField depF = pageSnippet.get(dep);
				if (depF != null) {
					// TODO levels?
					depF.getSelectWidget().triggerAction(ifi.getSelectWidget(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
				}
				triggerTransitiveDependencies(dep, ifi);
			}
		}
	}
	
//	@Override
//	public JSONObject retrieveGETData(OgemaHttpRequest req) {
//		// TODO update items
////		getItems();
////		addItem();
//		return super.retrieveGETData(req);
//	}
	
	protected List<SelectionItem> getSelectedLinkingItems(final LinkingOption opt, final OgemaHttpRequest req) {
		if (opt == null)
			return Collections.emptyList();
		final ItemField ifi = pageSnippet.get(opt);
		if (ifi == null)
			return Collections.emptyList();
		return ifi.getSelectedItems(req);
	}
	
	private final class ItemField extends Container implements MultiSelectorTemplate<SelectionItem> {
		
		private static final long serialVersionUID = 1L;
		private final LinkingOption opt;
		private final Label label;
		
		private final TemplateMultiselect<SelectionItem> multiselect;
		private final TemplateDropdown<SelectionItem> singleselect;
 
		public ItemField(WidgetPage<?> page, String id, LinkingOption opt) {
			super(page, id, true);
			this.opt = opt;
			this.label = new ItemLabel(page, id+ "_label", opt);
			if (opt.multipleSelectionsAllowed()) {
				this.singleselect = null;
				this.multiselect = new ItemMultiselect(page, id + "_select", opt);
				multiselect.triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST);
			} else {
				this.multiselect = null;
				this.singleselect = new ItemSingleselect(page, id + "_select", opt);
				singleselect.triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST);
			}
			final StaticTable tab = new StaticTable(2, 1)
					.setContent(0, 0, label);
			if (multiselect != null)
				tab.setContent(1, 0, multiselect);
			else
				tab.setContent(1, 0, singleselect);
			setDefaultFullscreen(true); // misleading property name -> leads to smaller box
			append(tab, null);
		}
		
		public ItemField(OgemaWidget parent, String id, OgemaHttpRequest req, LinkingOption opt) {
			super(parent, id, req);
			this.opt = opt;
			this.label = new ItemLabel(parent, id+ "_label", req, opt);
			if (opt.multipleSelectionsAllowed()) {
				this.singleselect = null;
				this.multiselect = new ItemMultiselect(parent, id + "_select", opt, req);
				multiselect.triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST, req);
			} else {
				this.multiselect = null;
				this.singleselect = new ItemSingleselect(parent, id + "_select", opt, req);
				singleselect.triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST, req);
			}
			final StaticTable tab = new StaticTable(2, 1)
					.setContent(0, 0, label);
			if (multiselect != null)
				tab.setContent(1, 0, multiselect);
			else
				tab.setContent(1, 0, singleselect);
			setDefaultFullscreen(true); // misleading property name -> leads to smaller box
			append(tab, req);
		}
		
		@Override
		public final List<SelectionItem> getSelectedItems(OgemaHttpRequest req) {
			if (multiselect != null) 
				return multiselect.getSelectedItems(req);
			else
				return Arrays.asList(singleselect.getSelectedItem(req));
		}
		
		@Override
		public void selectItems(Collection<SelectionItem> items, OgemaHttpRequest req) {
			if (multiselect != null) 
				multiselect.selectItems(items, req);
			else {
				if (items == null || items.isEmpty())
					singleselect.selectItem(null, req);
				else
					singleselect.selectItem(items.iterator().next(), req);
			}
		}
		
		@Override
		public void selectDefaultItems(Collection<SelectionItem> items) {
			if (multiselect != null) 
				multiselect.selectDefaultItems(items);
			else {
				if (items == null || items.isEmpty())
					singleselect.selectDefaultItem(null);
				else
					singleselect.selectDefaultItem(items.iterator().next());
			}
		}
		
		public final OgemaWidget getSelectWidget() {
			return multiselect != null ? multiselect : singleselect;
		}
		
	}
	
	private final class ItemMultiselect extends TemplateMultiselect<SelectionItem> {

		private static final long serialVersionUID = 1L;
		private final LinkingOption opt;

		public ItemMultiselect(WidgetPage<?> page, String id, LinkingOption opt) {
			super(page, id);
			this.opt = opt;
			setTemplate(TEMPLATE);
		}
		
		public ItemMultiselect(OgemaWidget parent, String id, LinkingOption opt, OgemaHttpRequest req) {
			super(parent, id, req);
			this.opt = opt;
			setTemplate(TEMPLATE);
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			try {
				final LinkingOption[] los = opt.dependencies();
				final List<Collection<SelectionItem>> dependencyItems;
				if (los != null) {
					dependencyItems = new ArrayList<>();
					SelectionTreeData.this.readLock();
					try {
						for (LinkingOption lo : los) {
							dependencyItems.add(getSelectedLinkingItems(lo, req));
						}
					} finally {
						SelectionTreeData.this.readUnlock();
					}
				} else
					dependencyItems = Collections.emptyList();
				update(opt.getOptions(dependencyItems), req);
			} catch (Exception e) { // the LinkingOption implementation may be buggy
				LoggerFactory.getLogger(SelectionTreeData.class).warn("Error updating selection tree widget",e);
				update(Collections.emptyList(), req);
			}
			if (terminalField != null && terminalField.multiselect == this) {
				((SelectionTree) widget).onTerminalFieldGetComplete(req);;
			}
		}
		
	}
	
	private final class ItemSingleselect extends TemplateDropdown<SelectionItem> {

		private static final long serialVersionUID = 1L;
		private final LinkingOption opt;

		public ItemSingleselect(WidgetPage<?> page, String id, LinkingOption opt) {
			super(page, id);
			this.opt = opt;
			setTemplate(TEMPLATE);
			setDefaultAddEmptyOption(!opt.selectionRequired());
		}
		
		public ItemSingleselect(OgemaWidget parent, String id, LinkingOption opt, OgemaHttpRequest req) {
			super(parent, id, req);
			this.opt = opt;
			setTemplate(TEMPLATE);
			setDefaultAddEmptyOption(!opt.selectionRequired());
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final LinkingOption[] los = opt.dependencies();
			final List<Collection<SelectionItem>> dependencyItems;
			if (los != null) {
				dependencyItems = new ArrayList<>();
				SelectionTreeData.this.readLock();
				try {
					for (LinkingOption lo : los) {
						dependencyItems.add(getSelectedLinkingItems(lo, req));
					}
				} finally {
					SelectionTreeData.this.readUnlock();
				}
			} else
				dependencyItems = Collections.emptyList();
			update(opt.getOptions(dependencyItems), req);
			if (terminalField != null && terminalField.singleselect == this) {
				((SelectionTree) widget).onTerminalFieldGetComplete(req);;
			}
		}
		
	}
	
	private final static class ItemLabel extends Label {
		
		private static final long serialVersionUID = 1L;
		private final LinkingOption opt;

		public ItemLabel(WidgetPage<?> page, String id, LinkingOption opt) {
			super(page, id);
			this.opt = opt;
		}
		
		public ItemLabel(OgemaWidget parent, String id, OgemaHttpRequest req, LinkingOption opt) {
			super(parent, id, req);
			this.opt = opt;
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			setText(opt.label(req.getLocale()), req);
		}
		
	}
	
	private final static DisplayTemplate<SelectionItem> TEMPLATE = new DisplayTemplate<SelectionItem>() {
		
		@Override
		public String getLabel(SelectionItem object, OgemaLocale locale) {
			return object.label(locale);
		}
		
		@Override
		public String getId(SelectionItem object) {
			return object.id();
		}
	}; 

}
