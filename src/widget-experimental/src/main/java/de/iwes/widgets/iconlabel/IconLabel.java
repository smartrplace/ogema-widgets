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
package de.iwes.widgets.iconlabel;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.flexbox.AlignContent;
import de.iwes.widgets.html.html5.flexbox.AlignItems;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;
import de.iwes.widgets.html.icon.Icon;
import de.iwes.widgets.html.icon.IconType;

public abstract class IconLabel extends Flexbox {

	private static final long serialVersionUID = 1L;
	protected final Icon icon;
	protected final Label label;

	/**
	 * IconLabel is global, sub widgets are non-global
	 * @param page
	 * @param id
	 */
	public IconLabel(WidgetPage<?> page, String id) {
		super(page, id, true);
		setDefaultJustifyContent(JustifyContent.SPACE_BETWEEN);
		setDefaultAlignItems(AlignItems.CENTER);
		setDefaultAlignContent(AlignContent.FLEX_LEFT);
		this.icon = new Icon(page, id + "_icon");
		this.label = new Label(page, id + "_label");
		this.addItem(icon, null);
		this.addItem(label, null);
		triggerAction(icon, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		triggerAction(label, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	/**
	 * IconLabel and sub widgets are session-specific
	 * @param parent
	 * @param id
	 * @param req
	 */
	public IconLabel(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
		setJustifyContent(JustifyContent.SPACE_BETWEEN, req);
		setAlignItems(AlignItems.CENTER, req);
		setAlignContent(AlignContent.FLEX_LEFT, req);
		this.icon = new Icon(this, id + "_icon", req);
		this.label = new Label(this, id + "_label", req);
		this.addItem(icon, req);
		this.addItem(label, req);
		triggerAction(icon, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST, req);
		triggerAction(label, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST, req);
	}
	
	protected abstract IconType getIcon(OgemaHttpRequest req);
	
	protected abstract String getLabelText(OgemaHttpRequest req);
	
	public Icon getIcon() {
		return icon;
	}
	
	public Label getLabel() {
		return label;
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		icon.setIconType(getIcon(req), req);
		label.setText(getLabelText(req), req);
		label.setHtml("&nbsp;&nbsp;" + label.getText(req), req);
	}
	
}
