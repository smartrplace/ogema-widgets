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
package de.iwes.widgets.object.widget.popup;

import java.util.ArrayList;
import java.util.List;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.popup.Popup;

public class ClosingPopup<T> extends Popup {
	
	private static final long serialVersionUID = -8516529533032957817L;

	PageSnippet popupSnippet;
	public Button closeButton = null;
	public Button okButton = null;
	//only relevant for session-specific widget
	OgemaHttpRequest widgetSession = null;
	OgemaWidget parent = null;
	public void onOK(T selected, OgemaHttpRequest req) {}
	public List<OgemaWidget> providePopupWidgets(OgemaHttpRequest req) {
		return new ArrayList<>();
	}

    private void initGlobalWidget(String id) {
    	if(!isGlobalWidget()) {
    		//popupSnippet = new PageSnippet(getPage(), "popupSnippet"+id, false);
    		return;
    	}
		popupSnippet = new PageSnippet(getPage(), "popupSnippet"+id, true);
		List<OgemaWidget> widgets = providePopupWidgets(null);
		if(widgets != null) for(OgemaWidget w: widgets) {
			popupSnippet.append(w, null);
			registerDependentWidget(w);
		}
		if(closingMode == ClosingMode.CLOSE) {
			closeButton = new Button(getPage(), "closeButton"+id, "Close");
			popupSnippet.append(closeButton, null);			
			closeButton.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		} else if(closingMode == ClosingMode.OK_CANCEL) {
			closeButton = new Button(getPage(), "closeButton"+id, "Close");
			//popupSnippet.append(closeButton, null);			
			okButton = new Button(getPage(), "okButton"+id, "OK") {
				private static final long serialVersionUID = -1403344542218161818L;

				@Override
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					@SuppressWarnings("unchecked")
					ClosingPopupData<T> reqData = (ClosingPopupData<T>) ClosingPopup.this.getData(req);
					onOK(reqData.getItem(), req);
				}
				
			};
			StaticTable bottomTable = new StaticTable(1, 2);
			bottomTable.setContent(0, 0, okButton).setContent(0, 1, closeButton);
			popupSnippet.append(bottomTable, null);			
			closeButton.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
			okButton.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
			
		}
		setBody(popupSnippet, null);
    }

	public enum ClosingMode {
		/**No closing button is provided, closing of the Popup has to be organized by the
		 * application
		 */
		NONE,
		/**Provide close button*/
		CLOSE,
		/**Provide Cancel button that works like the Close button for ClosingMode.CLOSE and
		 * another button that triggers the onOK method
		 */
		OK_CANCEL
	}
	final ClosingMode closingMode;
	protected T defaultItem;
	
	public ClosingPopup(WidgetPage<?> page, String id, ClosingMode closingMode) {
    	super(page, id);
  		this.closingMode = closingMode;
        //initC(id);
        initSnippet();
    }
	
    public ClosingPopup(WidgetPage<?> page, String id, String title, ClosingMode closingMode) {
        super(page, id, title);
  		this.closingMode = closingMode;
        //initC(id);
        initSnippet();
    }
    
    public ClosingPopup(WidgetPage<?> page, String id, String defaultTitle, String defaultHeaderHTML, String defaultBodyHTML, String defaultFooterHTML,
			ClosingMode closingMode) {
        super(page, id, defaultTitle, defaultHeaderHTML,
        		defaultBodyHTML, defaultFooterHTML);
  		this.closingMode = closingMode;
        //initC(id);
        initSnippet();
    }
	
    public ClosingPopup(WidgetPage<?> page, String id, boolean globalWidget, ClosingMode closingMode) {
        super(page,id,globalWidget);
  		this.closingMode = closingMode;
        if(globalWidget) initGlobalWidget(id);
        else initSnippet();
    }

    public ClosingPopup(WidgetPage<?> page, String id, String title, boolean globalWidget, ClosingMode closingMode) {
    	super(page, id, title, globalWidget);
  		this.closingMode = closingMode;
        //initC(id);
        initSnippet();
     }
    
    public ClosingPopup(OgemaWidget parent, String id, String title, ClosingMode closingMode, OgemaHttpRequest req) {
    	super(parent, id, title, req);
  		this.closingMode = closingMode;
  		this.widgetSession = req;
  		this.parent = parent;
        //initC(id);
        initSnippet();
     }


	public PageSnippet getPopupSnippet() {
		return popupSnippet;
	}
	/*@SuppressWarnings("unchecked")
	public PageSnippet getPopupSnippet(OgemaHttpRequest req) {
		return ((ClosingPopupData<T>)getData(req)).popupSnippet;
	}*/
	public Button getCloseButton() {
		return closeButton;
	}
	/*@SuppressWarnings("unchecked")
	public Button getCloseButton(OgemaHttpRequest req) {
		return ((ClosingPopupData<T>)getData(req)).closeButton;
	}*/

	public void setPopupSnippet(PageSnippet popupSnippet) {
		this.popupSnippet = popupSnippet;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ClosingMode getclosingMode() {
		return closingMode;
	}

	public T getDefaultItem() {
		return defaultItem;
	}

	@SuppressWarnings("unchecked")
	public void setItem(T item, OgemaHttpRequest req) {
		((ClosingPopupData<T>)getData(req)).setItem(item);
	}

	@SuppressWarnings("unchecked")
	public T getItem(OgemaHttpRequest req) {
		return ((ClosingPopupData<T>)getData(req)).getItem();
	}

	public void setDefaultItem(T defaultItem) {
		this.defaultItem = defaultItem;
	}

    @Override
	public ClosingPopupData<T> createNewSession() {
    	return new ClosingPopupData<T>(this, defaultItem);
    }
    
	public boolean isVisible(OgemaHttpRequest req) {
		return getData(req).isVisible();
	}
	
	/*@SuppressWarnings("unchecked")
	public void  updateWidgets(List<OgemaWidget> newWidgets, T item, OgemaHttpRequest req) {
		ClosingPopupData<T> data = (ClosingPopupData<T>)getData(req);
		if(item == null) item = getItem(req);
		data.widgetStorage.put(item, newWidgets);
		//data.newWidgetsProvided = true;
	}*/
	
    private void initSnippet() {
		//final ClosingPopup<T> cwidget = (ClosingPopup<T>)widget;
		popupSnippet = new PageSnippet(getPage(), "popupSnippet"+getId()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
	        	@SuppressWarnings("unchecked")
				//final ClosingPopup<T> cwidget = (ClosingPopup<T>)ClosingPopupData.this.widget;
	        	final ClosingPopupData<T> data = (ClosingPopupData<T>)ClosingPopup.this.getData(req);
	        	//List<OgemaWidget> subwidgets = providePopupWidgets(req);

	        	/*boolean doUpdate = false;
				if((data.item!=null)&&(!data.item.equals(data.lastItemProcessed)))
					doUpdate = true;*/
				/*
	        	if(doUpdate)
					popupSnippet.clear(req);
	        	for(OgemaWidget w: subwidgets) {
	        		popupSnippet.append(w, req);
					//TODO dependency
				}
	        	if(doUpdate) {
	        		List<OgemaWidget> newWidgets = data.widgetStorage.get(data.item);
		        	if(newWidgets != null) for(OgemaWidget w: newWidgets) {
		        		popupSnippet.append(w, req);
						//TODO dependency
					}
	        	}
	        	*/
		        if(data.doUpdate) {
					List<OgemaWidget> widgets = providePopupWidgets(req);
					if(widgets != null) for(OgemaWidget w: widgets) {
						popupSnippet.append(w, req);
						registerDependentWidget(w);
					}
		        	
		    		if(closingMode == ClosingMode.CLOSE) {
		   	    		closeButton = new Button(getPage(), "closeButton"+getId(), "Close") {
							private static final long serialVersionUID = 1L;

							public void onGET(OgemaHttpRequest req) {
		   	    				disable(req);
		   	    			};
		   	    		};
		   	    		popupSnippet.append(closeButton, req);			
		   	    		closeButton.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		    		} else if(closingMode == ClosingMode.OK_CANCEL) {
		    			closeButton = new Button(this, "closeButton"+getId(), "Close", req);
		   	    		popupSnippet.append(ClosingPopup.this.closeButton, null);			
		   	    		closeButton.triggerAction(ClosingPopup.this, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		    			Button okButton;
		   	    		okButton = new Button(this, "okButton"+getId(), "OK", req) {
		        			private static final long serialVersionUID = -1403344542218161818L;
			    			@Override
		    				public void onPOSTComplete(String data, OgemaHttpRequest req) {
		    					onOK(ClosingPopup.this.getItem(req), req);
		    				}
		    			
		   	    		};
		   	    		okButton.triggerAction(ClosingPopup.this, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		   	    		popupSnippet.append(okButton, req);	
		    		}
		    		//setBody(cwidget.popupSnippet);
		    		//newWidgetsProvided = false;
	        	} //if(doUpdate)
				
				
				
			}
		};
		setBody(popupSnippet, null);
	}

}
