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
package de.iwes.widgets.html.dragdropassign;

import java.io.IOException;
import java.util.Arrays;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class DragDropAssignData extends WidgetData {
	
	public static final WidgetStyle<DragDropAssign> STYLE_A = 
			new WidgetStyle<DragDropAssign>("dragdrop-container", Arrays.asList("dd-container-style-A"), 1);
	public static final WidgetStyle<DragDropAssign> CONTAINER_IMAGE_CENTERED_BOTTOM = 
			new WidgetStyle<DragDropAssign>("dragdrop-container", Arrays.asList("image-centered-bottom"), 1);

    private DragDropData assignData;
    private int colCount = 2;
    

    /**
     * ********* Constructor
     *
     **********
     * @param dragNDrop
     */
    public DragDropAssignData(DragDropAssign dragNDrop) {
        super(dragNDrop);
    }

    /**
     * ***** Inherited methods ******
     */
    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result = new JSONObject();
        result.put("colCount", colCount);
        final DragDropData data = (this.assignData != null ? this.assignData : new DragDropData()); 
        try {
            result.put("assignData", new JSONObject(assignData.toJSON()));
            return result;
        } catch (IOException ex) { //JSON-conversion can throw Exception
            throw new RuntimeException("JSON conversion failed",ex);
        }
        
    }

    @Override
    public JSONObject onPOST(String json, OgemaHttpRequest req) {
    	JSONObject request = new JSONObject(json);
        JSONObject j = request.getJSONObject("data");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        Item item;
        Container from;
        Container to;
        try {
	        //Parse json in objects and search them in our existing assignData
	        item = assignData.getItem(mapper.readValue(j.getJSONObject("item").toString(), Item.class));
	        from = assignData.getContainer(mapper.readValue(j.getJSONObject("from").toString(), Container.class));
	        to = assignData.getContainer(mapper.readValue(j.getJSONObject("to").toString(), Container.class));
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
        assignData.update(item, to); //Update assignData so it reflects the changes
        ((DragDropAssign) widget).onUpdate(item, from, to, req); //Inform application about update, include changes
        return request;
    }

    /**
     * ******** Public methods *********
     */
    public void setAssignData(DragDropData assignData) {
        this.assignData = assignData;
    }

    public DragDropData getAssignData() {
        return assignData;
    }
    
    public void setColCount(int colCount) {
        this.colCount = colCount;
    }
    
    public int getColCount() {
        return colCount;
    }
}
