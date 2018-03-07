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

package de.iwes.widgets.html.buttonconfirm;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;

/**
 * 
 * Like a {@link Button}, except that a confirmation window is shown before a 
 * submit (POST) is actually performed
 * 
 * @author cnoelle
 *
 */
public class ButtonConfirm extends Button  {
	
    private static final long serialVersionUID = 550713654103033621L;
	private String defaultConfirmMsg = null;
	private String defaultConfirmBtnMsg = "Confirm";
	private String defaultCancelBtnMsg = "Cancel";
	private String defaultConfirmPopupTitle = null;
    
    /************* constructor **********************/

    public ButtonConfirm(WidgetPage<?> page, String id) {
    	super(page, id);
    }
    
    public ButtonConfirm(WidgetPage<?> page, String id, String text) {
    	super(page, id, text);
    }
    public ButtonConfirm(WidgetPage<?> page, String id, String text, OgemaHttpRequest req) {
    	super(page, id, text, req);
    }
    
    public ButtonConfirm(WidgetPage<?> page, String id, boolean globalWidget) {
    	super(page, id, globalWidget);
    }
    
    public ButtonConfirm(WidgetPage<?> page, String id, String text, boolean globalWidget) {
    	super(page, id, text, globalWidget);
    }
    
    public ButtonConfirm(OgemaWidget parent, String id, OgemaHttpRequest req) {
    	super(parent, id, req);
    }
    
    /******* Inherited methods ******/
    
    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return ButtonConfirm.class;
    }

	@Override
	public ButtonData createNewSession() {
		return new ButtonConfirmData(this);
	}
	
	@Override
	public ButtonConfirmData getData(OgemaHttpRequest req) {
		return (ButtonConfirmData) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(ButtonData opt) {
		super.setDefaultValues(opt);
		ButtonConfirmData opt2 = (ButtonConfirmData) opt;
		opt2.setConfirmMsg(defaultConfirmMsg);
		opt2.setConfirmBtnMsg(defaultConfirmBtnMsg);
		opt2.setCancelBtnMsg(defaultCancelBtnMsg);
		opt2.setConfirmPopupTitle(defaultConfirmPopupTitle);
	}
    
    /******** public methods ***********/
	
	public void setDefaultConfirmBtnMsg(String confirmBtnMsg) {
		this.defaultConfirmBtnMsg = confirmBtnMsg;
	}
	
	public void setDefaultCancelBtnMsg(String cancelBtnMsg) {
		this.defaultCancelBtnMsg = cancelBtnMsg;
	}
	
	public void setDefaultConfirmMsg(String confirmMsg) {
		this.defaultConfirmMsg = confirmMsg;
	}
	
	public void setDefaultConfirmPopupTitle(String confirmPopupTitle) {
		this.defaultConfirmPopupTitle = confirmPopupTitle;
	}
	
    public String getConfirmBtnMsg(OgemaHttpRequest req) {
		return getData(req).getConfirmBtnMsg();
	}

	public void setConfirmBtnMsg(String confirmBtnMsg,OgemaHttpRequest req) {
		getData(req).setConfirmBtnMsg(confirmBtnMsg);
	}

	public String getCancelBtnMsg(OgemaHttpRequest req) {
		return getData(req).getCancelBtnMsg();
	}

	public void setCancelBtnMsg(String cancelBtnMsg,OgemaHttpRequest req) {
		getData(req).setCancelBtnMsg(cancelBtnMsg);
	}
	
	public String getConfirmMsg(OgemaHttpRequest req) {
		return getData(req).getConfirmMsg();
	}

	public void setConfirmMsg(String confirmMsg, OgemaHttpRequest req) {
		getData(req).setConfirmMsg(confirmMsg);
	}

	public String getConfirmPopupTitle(OgemaHttpRequest req) {
		return getData(req).getConfirmPopupTitle();
	}

	public void setConfirmPopupTitle(String confirmPopupTitle, OgemaHttpRequest req) {
		getData(req).setConfirmPopupTitle(confirmPopupTitle);
	}
	
}
