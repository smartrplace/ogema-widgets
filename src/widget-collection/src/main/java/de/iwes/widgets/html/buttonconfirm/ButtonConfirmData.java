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

import java.util.Arrays;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.ButtonData;

public class ButtonConfirmData extends ButtonData {
	
	/**
	 * use {@link ButtonOption} styles to set the style for the primary button
	 */
	public static final WidgetStyle<ButtonConfirm> CONFIRM_BLUE = new WidgetStyle<ButtonConfirm>("confirmButton",Arrays.asList("btn","btn-primary"),0);
	public static final WidgetStyle<ButtonConfirm> CONFIRM_RED = new WidgetStyle<ButtonConfirm>("confirmButton",Arrays.asList("btn","btn-danger"),0);
	public static final WidgetStyle<ButtonConfirm> CONFIRM_GREEN = new WidgetStyle<ButtonConfirm>("confirmButton",Arrays.asList("btn","btn-success"),0);
	public static final WidgetStyle<ButtonConfirm> CONFIRM_LIGHT_BLUE = new WidgetStyle<ButtonConfirm>("confirmButton",Arrays.asList("btn","btn-info"),0);
	public static final WidgetStyle<ButtonConfirm> CONFIRM_DEFAULT = new WidgetStyle<ButtonConfirm>("confirmButton",Arrays.asList("btn","btn-default"),0);
	public static final WidgetStyle<ButtonConfirm> CONFIRM_ORANGE = new WidgetStyle<ButtonConfirm>("confirmButton",Arrays.asList("btn","btn-warning"),0);
	public static final WidgetStyle<ButtonConfirm> CONFIRM_LARGE = new WidgetStyle<ButtonConfirm>("confirmButton",Arrays.asList("btn","btn-lg"),0);
	public static final WidgetStyle<ButtonConfirm> CONFIRM_SMALL = new WidgetStyle<ButtonConfirm>("confirmButton",Arrays.asList("btn","btn-sm"),0);
	
	public static final WidgetStyle<ButtonConfirm> CANCEL_BLUE = new WidgetStyle<ButtonConfirm>("cancelButton",Arrays.asList("btn","btn-primary"),0);
	public static final WidgetStyle<ButtonConfirm> CANCEL_RED = new WidgetStyle<ButtonConfirm>("cancelButton",Arrays.asList("btn","btn-danger"),0);
	public static final WidgetStyle<ButtonConfirm> CANCEL_GREEN = new WidgetStyle<ButtonConfirm>("cancelButton",Arrays.asList("btn","btn-success"),0);
	public static final WidgetStyle<ButtonConfirm> CANCEL_LIGHT_BLUE = new WidgetStyle<ButtonConfirm>("cancelButton",Arrays.asList("btn","btn-info"),0);
	public static final WidgetStyle<ButtonConfirm> CANCEL_DEFAULT = new WidgetStyle<ButtonConfirm>("cancelButton",Arrays.asList("btn","btn-default"),0);
	public static final WidgetStyle<ButtonConfirm> CANCEL_ORANGE = new WidgetStyle<ButtonConfirm>("cancelButton",Arrays.asList("btn","btn-warning"),0);
	public static final WidgetStyle<ButtonConfirm> CANCEL_LARGE = new WidgetStyle<ButtonConfirm>("cancelButton",Arrays.asList("btn","btn-lg"),0);
	public static final WidgetStyle<ButtonConfirm> CANCEL_SMALL = new WidgetStyle<ButtonConfirm>("cancelButton",Arrays.asList("btn","btn-sm"),0);
	
	private String confirmMsg = null;
	private String confirmBtnMsg = null;
	private String cancelBtnMsg = null;
	private String confirmPopupTitle = null;

	/************* constructor **********************/

    public ButtonConfirmData(ButtonConfirm bc) {
    	super(bc);
    }
        
    /******* Inherited methods ******/
    
    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
    	JSONObject obj = super.retrieveGETData(req);
    	if (confirmBtnMsg != null) 
    		obj.put("confirmBtnMsg", confirmBtnMsg);
    	if (cancelBtnMsg != null)
    		obj.put("cancelBtnMsg", cancelBtnMsg);
    	if (confirmMsg != null)
    		obj.put("confirmMsg", confirmMsg);
    	if (confirmPopupTitle != null)
    		obj.put("confirmPopupTitle", confirmPopupTitle);
    	return obj;
    }

    /******* Public methods ******/
	
    public String getConfirmBtnMsg() {
		return confirmBtnMsg;
	}

	public void setConfirmBtnMsg(String confirmBtnMsg) {
		this.confirmBtnMsg = confirmBtnMsg;
	}

	public String getCancelBtnMsg() {
		return cancelBtnMsg;
	}

	public void setCancelBtnMsg(String cancelBtnMsg) {
		this.cancelBtnMsg = cancelBtnMsg;
	}
	
	public String getConfirmMsg() {
		return confirmMsg;
	}

	public void setConfirmMsg(String confirmMsg) {
		this.confirmMsg = confirmMsg;
	}

	public String getConfirmPopupTitle() {
		return confirmPopupTitle;
	}

	public void setConfirmPopupTitle(String confirmPopupTitle) {
		this.confirmPopupTitle = confirmPopupTitle;
	}

}
