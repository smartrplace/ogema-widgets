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
package de.iwes.widgets.multiselect.extended;

import java.util.Collections;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;

public class MultiSelectExtended<T> extends PageSnippet {
	/**Overwrite this method to perform actions when selectAllButtion / deselectAllButton
	 * is pressed
	 * @param isSelection if true the selectAllButton is pressed, otherwise the
	 * 		deselectAllButton
	 * @param req
	 */
	protected void onSelectionEvent(boolean isSelection, OgemaHttpRequest req) {}
	
	private static final long serialVersionUID = 1L;
	public TemplateMultiselect<T> multiSelect;
	public Button selectAllButton;
	public Button deselectAllButton;
	
	private boolean buttonsOnTop = false;
	private boolean useGlyphicons = false;
	private String CHECK_ALL = "++";
	private String UNCECK_ALL = "--";
	private String buttonStyle = "";
	private boolean registerDependentWidget = false;
	
	public MultiSelectExtended(WidgetPage<?> page, String id, TemplateMultiselect<T> multiSelect, boolean useGlyphicons, String buttonStyle) {
		this(page, id, multiSelect, useGlyphicons, buttonStyle, false);
	}
	
	public MultiSelectExtended(WidgetPage<?> page, String id, TemplateMultiselect<T> multiSelect, boolean useGlyphicons, String buttonStyle, boolean buttonsOnTop) {
		this(page, id, multiSelect, useGlyphicons, buttonStyle, false, false);
	}
	

	/**
	 * 
	 * @param page
	 * @param id
	 * @param multiSelect
	 * @param useGlyphicons
	 * @param buttonStyle
	 * @param buttonsOnTop
	 * @param registerDependentWidget if true --> the registerDependentWidget-Method is called, if false --> the triggerAction-Method must called from extern
	 */
	public MultiSelectExtended(WidgetPage<?> page, String id, TemplateMultiselect<T> multiSelect, boolean useGlyphicons, String buttonStyle, boolean buttonsOnTop, boolean registerDependentWidget) {
		super(page, id, true);
		this.useGlyphicons = useGlyphicons;
		this.buttonStyle = buttonStyle;
		this.buttonsOnTop = buttonsOnTop;
		if(useGlyphicons) {
			CHECK_ALL = " Check All";
			UNCECK_ALL = " Uncheck All";
		}
		this.registerDependentWidget = registerDependentWidget;
		initSnippet(page, null, id, multiSelect, null);
	}
	
	public MultiSelectExtended(WidgetPage<?> page, String id, TemplateMultiselect<T> multiSelect) {
		this(page, id, multiSelect, false, "");
	}

	public MultiSelectExtended(WidgetPage<?> page, String id, boolean globalPage, TemplateMultiselect<T> multiSelect) {
		super(page, id, globalPage);
		initSnippet(page, null, id, multiSelect, null);
	}
	
	/** Create session-specific subwidget */
	public MultiSelectExtended(OgemaWidget widget, String id, OgemaHttpRequest req, TemplateMultiselect<T> multiSelect) {
		this(widget, id, req, multiSelect, false);
	}
	public MultiSelectExtended(OgemaWidget widget, String id, OgemaHttpRequest req, TemplateMultiselect<T> multiSelect,
			boolean bottonsOnTop) { // or use page instead of widget?
		super(widget, id, req);
		this.buttonsOnTop = bottonsOnTop;
		initSnippet(null, widget, id, multiSelect, null);
	}
	
	private void initSnippet(WidgetPage<?> page, OgemaWidget widget, String id,
			final TemplateMultiselect<T> multiSelect, OgemaHttpRequest req) {
		this.multiSelect = multiSelect;
		if(page != null) selectAllButton = new Button(page, id+"selectAllButton", CHECK_ALL) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				if(multiSelect.isDisabled(req)) {
					return;
				}
				multiSelect.selectItems(multiSelect.getItems(req), req);
				onSelectionEvent(true, req);
			}
			@Override
			public void onGET(OgemaHttpRequest req) {
				if(useGlyphicons) setGlyphicon("glyphicon glyphicon-plus-sign", req);
				setCss(buttonStyle, req);
			}
		};
		else selectAllButton = new Button(widget, id+"selectAllButton", CHECK_ALL, req) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				if(multiSelect.isDisabled(req)) {
					return;
				}
				multiSelect.selectItems(multiSelect.getItems(req), req);
				setCss(buttonStyle, req);
				onSelectionEvent(true, req);
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if(useGlyphicons)  setGlyphicon("glyphicon glyphicon-plus-sign", req);
				setCss(buttonStyle, req);
			}
		};
		
		if(page != null) deselectAllButton = new Button(page, id+"deselectAllButton", UNCECK_ALL) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				if(multiSelect.isDisabled(req)) {
					return;
				}
				multiSelect.selectItems(Collections.<T>emptyList(), req);
				onSelectionEvent(false, req);
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if(useGlyphicons)  setGlyphicon(" glyphicon glyphicon-minus-sign", req);
				setCss(buttonStyle, req);
			}
		};
		else deselectAllButton = new Button(widget, id+"deselectAllButton", UNCECK_ALL, req) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				if(multiSelect.isDisabled(req)) {
					return;
				}
				multiSelect.selectItems(Collections.<T>emptyList(), req);
				onSelectionEvent(false, req);
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if(useGlyphicons)  setGlyphicon(" glyphicon glyphicon-minus-sign", req);;
				setCss(buttonStyle, req);
			}
		};
		
		if(registerDependentWidget) {
			selectAllButton.registerDependentWidget(multiSelect);
			deselectAllButton.registerDependentWidget(multiSelect);
		}else {
			selectAllButton.triggerAction(multiSelect, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			deselectAllButton.triggerAction(multiSelect, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);			
		}
		
		
		if(buttonsOnTop) {
			StaticTable topTable = new StaticTable(1, 2);
			topTable.setContent(0, 0, selectAllButton).setContent(0, 1, deselectAllButton);
			append(topTable, req);
			append(multiSelect, req);
		} else {
			StaticTable totalTable = new StaticTable(1, 2, new int[] {10, 2});
			totalTable.setContent(0, 0, multiSelect).setContent(0, 1, selectAllButton).setContent(0, 1, deselectAllButton);
			//StaticTable buttonTable = new StaticTable(1,1);
			//buttonTable.setContent(0, 0, selectAllButton).setContent(0, 0, deselectAllButton);
			//Flexbox totalTable = getFlexBox(page, multiSelect, buttonTable, id+"totalTable");
			//totalTable.append(buttonTable);
			append(totalTable, req);
		}
	}
	
	/*public static Flexbox getFlexBox(WidgetPage<?> page, OgemaWidget w1, OgemaWidget w2, String id) {
		Flexbox flex = new Flexbox(page, id, true);
		flex.addItem(w1, null).addItem(w2, null);
		flex.setJustifyContent(JustifyContent.SPACE_AROUND, null);
		flex.setDefaultFlexWrap(FlexWrap.NOWRAP);
		return flex;
	}*/

	public boolean isButtonsOnTop() {
		return buttonsOnTop;
	}

	/**If true the buttons are placed over the multiselect, otherwise behind the multiselect. Default
	 * ist false
	 */
	//TODO: buttonsOnTop is only processed in constructor
	//public void setButtonsOnTop(boolean buttonsOnTop) {
	//	this.buttonsOnTop = buttonsOnTop;
	//}
	

	
	

}
