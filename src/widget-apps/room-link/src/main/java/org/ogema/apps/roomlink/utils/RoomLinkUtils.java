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
package org.ogema.apps.roomlink.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 *
 * @author Tobias Gries <tobias.gries@iwes.fraunhofer.de>
 */
public class RoomLinkUtils {

    /**
     * Set the path PhysicalElement.location.room to the specified room.
     *
     * @param element Source resource which gets its location.room attribut set.
     * @param room Referencing room resource.
     * @return false if element is of type room, true otherwise.
     */
    public static boolean setLocation(PhysicalElement element, Room room) {
        if (room == null) {
            element.location().room().delete(); 
            return true;
        } else {
            if (element instanceof Room) {  // should not occur at all...
                return false;
            } else {
//                if (!element.location().room().exists()) {
//                    element.location().room().create();
//                }

                element.location().room().setAsReference(room);
                return true;
            }
        }
    }
    
	public static boolean isNameInUse(String name, ResourceAccess ra) {
		List<Room> rooms  = ra.getResources(Room.class);
		for (Room room: rooms) {
			if (!room.name().isActive()) continue;
			if (room.name().getValue().equals(name)) return true;
		}
		return false;
	}
	
	public static void writeFile(InputStream is, File fileOut) throws IOException {
		FileOutputStream outputStream = null;
		try {
			outputStream =  new FileOutputStream(fileOut);
			int read = 0;
			byte[] bytes = new byte[1024];
	
			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
				if (is != null)
					is.close();
			} catch (Exception e) {}
		}
	}
	
	public static String getContentType(File fl) {
		String name = fl.getName().toLowerCase();
		if (name.endsWith("png")) {
			return "image/png";
		}
		else if (name.endsWith("jpg") || name.endsWith("jpeg")) {
			return "image/jpeg";
		}
		else if (name.endsWith("gif")) {
			return "image/gif";
		}
		else if (name.endsWith("svg")) {
			return "image/svg+xml";
		}
		return "application/octet-stream";
	}

	/*
	 * We assume that the page has a parameter of the form "?configId=resourcePath", and return the
	 * corresponding resource, or null if there is no configId param or the resource does not exist
	 */
	public static <R extends Resource> R getActiveResource(WidgetPage<?> page, OgemaHttpRequest req, ResourceAccess ra) {
		String[] params = page.getPageParameters(req).get("configId");
		if (params == null || params.length == 0) 
			return null;
		String path = params[0];
		return ra.getResource(path);
	}
	
}
