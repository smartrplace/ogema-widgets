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
package de.iwes.widgets.html.buttonconfirm;

import java.util.Arrays;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.ButtonData;

public class ButtonConfirmData extends ButtonData {
	
	/**
	 * use {@link ButtonData} styles to set the style for the primary button
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
