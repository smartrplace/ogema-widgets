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

package de.iwes.widgets.html.textarea;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class TextAreaData extends WidgetData {

	// default values are set on creation
    private String text;
    private int cols;
    private int rows;


/*********** Constructor **********/
	
	public TextAreaData(TextArea textArea) {
		super(textArea);
	}
    
	/******* Inherited methods ******/

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
    	JSONObject result = new JSONObject();	
   		result.put("text", text);
   		result.put("row", rows);
   		result.put("cols", cols);
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

    
    
}
