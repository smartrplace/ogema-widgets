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
package de.iwes.widgets.html.textarea;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class TextAreaData extends WidgetData {

	// default values are set on creation
    private String text;
    private int cols;
    private int rows;
    private boolean selected;


/*********** Constructor **********/
	
	public TextAreaData(TextArea textArea) {
		super(textArea);
	}
    
	/******* Inherited methods ******/

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
    	JSONObject result = new JSONObject();	
   		result.put("text", text);
   		result.put("rows", rows);
   		result.put("cols", cols);
   		if (selected)
   			result.put("selected", selected);
        return result;
    }

	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		JSONObject obj = new JSONObject(data);
		this.text = obj.getString("data");
		return obj;
	}
	
	@Override
	protected String getWidthSelector() {
		return ">textarea";
	}
    
    /******* Public methods ******/

    public void setText(String text) {
        this.text = text;
    }
    
    public String getText() {
    	return text;
    }
    
    public int getCols() {
 		return cols;
 	}

 	public void setCols(int cols) {
 		if (cols <= 0) throw new IllegalArgumentException("Number of columns must be positive");
 		this.cols = cols;
 	}

 	public int getRows() {
 		return rows;
 	}

 	public void setRows(int rows) {
		if (rows <= 0) throw new IllegalArgumentException("Number of rows must be positive");
 		this.rows = rows;
 	}
 	
 	public boolean isSelected() {
 		return selected;
 	}
 	
 	public void setSelected(boolean selected) {
 		this.selected = selected;
 	}
 	
}
