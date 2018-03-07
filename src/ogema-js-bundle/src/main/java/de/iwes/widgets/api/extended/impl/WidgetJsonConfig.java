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
