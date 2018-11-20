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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.iwes.widgets.api.extended.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

class WidgetJsonConfig {
    
    private final List<String[]> widgetResourcePaths = new ArrayList<>(2);
    
    void addScriptResourcePath(String id, String type, String widgetResourcePath){
        widgetResourcePaths.add( new String[]{id, type, widgetResourcePath} );
    }
    
    String toJson() {
//        return new ObjectMapper().writeValueAsString(widgetResourcePaths);
    	final JSONArray arr = new JSONArray();
    	for (String[] strings: widgetResourcePaths) {
    		arr.put(new JSONArray(strings));
    	}
    	return arr.toString();
    }
}
