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

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.popup.PopupData;

public class ClosingPopupData<T> extends PopupData {
	protected T item;
	//protected ClosingMode closingMode;
	protected boolean isOpen = false;
	//protected PageSnippet popupSnippet = null;
	//public boolean newWidgetsProvided = false;
	//public List<OgemaWidget> newWidgets;
	//public Map<T, List<OgemaWidget>> widgetStorage = new HashMap<>();
	//T lastItemProcessed = null;
	//public Button closeButton = null;
	public boolean doUpdate = false;
	/**TODO: This is only relevant in combination with PatternEdit-like format created by ValueReceiverHelper*/
	//public Map<T, List<WidgetEntryData>> existingWidgetsInTable;
	//public Map<T, DynamicTable<WidgetEntryData>> popTable;
	
    /************* constructor **********************/
    
    public ClosingPopupData(ClosingPopup<T> popup, T item) {
        super(popup);
        this.item = item;
        //this.closingMode = closingMode;
    }

    public ClosingPopupData(ClosingPopup<T> popup, String title, String headerHTML, String bodyHTML, String footerHTML,
    		T item) {
        super(popup, title, headerHTML, bodyHTML, footerHTML);
        this.item = item;
        //this.closingMode = closingMode;
    }
    
    public T getItem() {
		return item;
	}

	public void setItem(T item) {
		this.item = item;
	}
	
	/******* Inherited methods ******/

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result = super.retrieveGETData(req);
 		/*if(!widget.isGlobalWidget()) {
			@SuppressWarnings("unchecked")
			final ClosingPopup<T> cwidget = (ClosingPopup<T>)widget;
    		setBody(cwidget.popupSnippet);
           	//end: DOUBLE
        }*/
        return result;
      	//List<OgemaWidget> subwidgets = cwidget.providePopupWidgets(req);
			/*if(cwidget.popupSnippet == null) {
				cwidget.popupSnippet = new PageSnippet(cwidget.getPage(), "popupSnippet"+widget.getId(), false);
				setBody(cwidget.popupSnippet);
			} else {
				cwidget.popupSnippet.clear(req);
			}*/
				//newWidgetsProvided = true;
			
			//TODO: DOUBLE
			/*
			boolean doUpdate = false;
			if((item!=null)&&(!item.equals(lastItemProcessed)))
				doUpdate = true;
			if(doUpdate)
				popupSnippet.clear(req);
        	for(OgemaWidget w: subwidgets) {
				popupSnippet.append(w, req);
				//TODO dependency
			}
        	if(doUpdate) {
        		List<OgemaWidget> newWidgets = widgetStorage.get(item);
	        	if(newWidgets != null) for(OgemaWidget w: newWidgets) {
					popupSnippet.append(w, req);
					//TODO dependency
				}
	        	
	    		if(cwidget.closingMode == ClosingMode.CLOSE) {
	    			Button closeButton;
	   	    		closeButton = new Button(widget, "closeButton"+widget.getId(), "Close", req);
	   	    		popupSnippet.append(closeButton, req);			
	   	    		closeButton.triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
	    		} else if(cwidget.closingMode == ClosingMode.OK_CANCEL) {
	    			Button closeButton;
	   	    		closeButton = new Button(widget, "closeButton"+widget.getId(), "Close", req);
	   	    		popupSnippet.append(closeButton, null);			
	    			closeButton.triggerAction(widget, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
	    			Button okButton;
	   	    		okButton = new Button(widget, "okButton"+widget.getId(), "OK", req) {
	        			private static final long serialVersionUID = -1403344542218161818L;
	
	    				@Override
	    				public void onPOSTComplete(String data, OgemaHttpRequest req) {
	    					cwidget.onOK(getItem(), req);
	    				}
	    			
	   	    		};
	   	    		popupSnippet.append(okButton, req);	
	    		}
	    		setBody(popupSnippet);
	    		//newWidgetsProvided = false;
        	}
        	*/
    }
}
