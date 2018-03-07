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

package de.iwes.widgets.resource.widget.textfield;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
import org.ogema.core.model.array.ArrayResource;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.tools.resource.util.ValueResourceUtils;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;
import de.iwes.widgets.html.icon.Icon;
import de.iwes.widgets.html.icon.IconType;
import de.iwes.widgets.html.listgroup.ListGroupData;

// the String keys are ResourcePath + "__" + idx
public class ArrayResourceListGroupData<A extends ArrayResource> extends ListGroupData<String> {

	private A array;
	private final WidgetGroup arrayEntries;
	
	protected ArrayResourceListGroupData(ArrayResourceListGroup<A> widget, WidgetGroup arrayEntries) {
		super(widget, null);
		this.arrayEntries = arrayEntries;
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		writeLock(); 
		try {
			List<String> ints;
			if (array == null || !array.exists()) {
	//			update(Collections.<String> emptyList());
				ints = Collections.<String> emptyList();
			}
			else {
				String path = array.getPath();
				int sz = ValueResourceUtils.getSize(array);
				ints = new ArrayList<>();
				for (int i=0;i< Math.min(sz, 50);i++) { // TODO paging
					ints.add(path + "__" + i);
				}
				ints.add(path + "__" + sz); // we allow adding a field as well
			}
			update(ints); // this is the only part where it is important to have the resource path in the line id; 
						  // if the resource changes, this will remove old lines; if the id was only the index, this would fail
			return super.retrieveGETData(req);
		} finally {
			writeUnlock();
		}
	}

	// we have to override this, since the template is null here 
	@Override
	public boolean addItem(String resource) {
		if (array == null || elements.contains(resource)) 
			return false;
		int finalInt = getTrailingInt(resource);
		if (finalInt > ValueResourceUtils.getSize(array))
			return false;
		Flexbox snippet;
		ArrayEditTextField textField;
		ArrayEntryRemovalIcon icon;
		String baseId = widget.getId() + "__" + ResourceUtils.getValidResourceName(resource);
		if (widget.isGlobalWidget()) {
			snippet = new Flexbox(widget.getPage(), baseId + "__snippet", true);
			textField = new ArrayEditTextField(widget.getPage(), baseId + "__textfield", true, array, finalInt);
			icon = new ArrayEntryRemovalIcon(widget.getPage(), baseId + "__icon", true, array, finalInt);
			textField.triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			textField.triggerAction(textField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			arrayEntries.addWidget(textField);
			icon.triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			icon.triggerAction(arrayEntries, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
		else {
			snippet = new Flexbox(widget, baseId + "__snippet",getInitialRequest());
			textField = new ArrayEditTextField(widget,  baseId + "__textfield", getInitialRequest(), array, finalInt);
			icon = new ArrayEntryRemovalIcon(widget, baseId + "__icon", getInitialRequest(), array, finalInt);
			textField.triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, getInitialRequest());
			textField.triggerAction(textField, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, getInitialRequest());
			arrayEntries.addWidget(textField); // FIXME we need a session-widget version for this!
			icon.triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, getInitialRequest());
			icon.triggerAction(arrayEntries, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, getInitialRequest());
		}
		snippet.setDefaultJustifyContent(JustifyContent.SPACE_AROUND);
		icon.setDefaultScale(0.5F);
		snippet.addItem(textField, getInitialRequest()).addItem(icon, getInitialRequest());
//		StaticTable tab = new StaticTable(1, 2);
//		tab.setContent(0, 0, textField).setContent(0, 1, icon);
//		snippet.append(tab, getInitialRequest());
		snippets.add(snippet);
		elements.add(resource);
		return true;
	}
	
	public A getSelectedItem() {
		readLock();
		try {
			return array;
		} finally {
			readUnlock();
		}
	}

	public void selectItem(A item) {
		writeLock();
		try {
			this.array = item;
		} finally {
			writeUnlock();
		}
		
	}
	
	protected static class ArrayEntryRemovalIcon extends Icon {

		private static final long serialVersionUID = 1L;
		private final int idx;
		private final ArrayResource targetResource;

		public ArrayEntryRemovalIcon(WidgetPage<?> page, String id, boolean globalWidget, ArrayResource targetResource, int idx) {
			super(page, id, globalWidget);
			this.idx = idx;
			this.targetResource = targetResource;
			enable();
			setDefaultIconType(IconType.CLOSE);
		}
		
		public ArrayEntryRemovalIcon(OgemaWidget parent, String id, OgemaHttpRequest req, ArrayResource targetResource, int idx) {
			super(parent, id, req);
			this.idx = idx;
			this.targetResource = targetResource;
			enable();
			setDefaultIconType(IconType.CLOSE);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			ValueResourceUtils.removeElement(targetResource, idx);
		}
		
	}
	
	protected static class ArrayEditTextField extends TextField {

		private static final long serialVersionUID = 1L;
		private final int idx;
		private final ArrayResource targetResource;
		
		public ArrayEditTextField(WidgetPage<?> page, String id, boolean globalWidget, ArrayResource targetResource, int idx) {
			super(page, id, globalWidget);
			this.idx = idx;
			this.targetResource = targetResource;
		}
		
		public ArrayEditTextField(OgemaWidget parent, String id, OgemaHttpRequest req, ArrayResource targetResource, int idx) {
			super(parent, id, req);
			this.idx = idx;
			this.targetResource = targetResource;
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			if (!targetResource.exists() || idx >= ValueResourceUtils.getSize(targetResource)) { // the latter case is the "Add one entry" field
				setValue("", req);
			}
			else {
				setValue(ValueResourceUtils.getValue(targetResource,idx).toString(), req);
			}
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			if (!targetResource.exists()) {
				return;
			}
			String value = getValue(req);
			try {
				ValueResourceUtils.setValue(targetResource, idx, value);
			} catch (NumberFormatException e) { /* TODO report */ } 
		}
		
	}

	/**
	 * @param string
	 * 		Must end with an integer
	 * @return
	 */
	protected static int getTrailingInt(String string) {
	    int i = string.length();
	    while (i > 0 && Character.isDigit(string.charAt(i - 1))) {
	        i--;
	    }
	    return Integer.parseInt(string.substring(i));
	}
	

}
