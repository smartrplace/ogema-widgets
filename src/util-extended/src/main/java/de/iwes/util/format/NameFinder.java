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
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.util.format;

import org.ogema.core.model.Resource;
import org.ogema.tools.resource.util.ResourceUtils;

/** Class providing static functions to get a string name value for a resource. This does not
 * use the resource name, but the name field of a resource or the getName method of a
 * ManagedResourcePattern.
 * 
 * @author dnestle
 *
 */
public class NameFinder {
	/** Return id based on resource location that is safe to use inside widget Ids and
	 * other elements intended to be used with the widget framework.
	@Deprecated use {@link ResourceUtils#getValidResourceName(String)} instead
	 */
	@Deprecated
	public static String getUniqueID(Resource res) {
		ResourceUtils.getValidResourceName(""); // XXX
		return res.getLocation("_");
	}

	// FIXME replace by NameService?
//	public static String getHumanReadableName(ManagedResourcePattern<?, ?> res) {
//		if(res.getName() == null) {
//			return "";
//		}
//		return res.getName();
//	}
	
	/**
	 * @deprecated use ResourceUtils.getValidResourceName
	 * @param variableName
	 * @return
	 */
	public static String validJavaOGEMAName(String s) {
	    StringBuilder sb = new StringBuilder();
	    if(!Character.isJavaIdentifierStart(s.charAt(0))) {
	        sb.append("_");
	    }
	    for (char c : s.toCharArray()) {
	        if(!Character.isJavaIdentifierPart(c)) {
	            sb.append("_");
	        } else {
	            sb.append(c);
	        }
	    }
	    return sb.toString();
	}
	
	/**Get String representation of boolean value
	 * @param val
	 * @return "on" or "off"
	 */
	public static String onOffText(boolean val) {
		if(val) return "on";
		return "off";
	}
	
	/**Converts a camel case String into a String that looks a bit nicer to non-programmers
	 * by inserting spaces and some other measures
	 * @param camelString
	 * @return human readable string
	 */
	public static String convertCamelToHumanReadble(String camelString) {
	    StringBuilder sb = new StringBuilder();
	    boolean init = false;
	    boolean previousWasUpper = false;
	    for (char c : camelString.toCharArray()) {
 	        if(!init) {
	        	init = true;
	        	if(Character.isLowerCase(c)) {
		        	sb.append(String.valueOf(c).toUpperCase());
	        		previousWasUpper = true;
	        	} else {
	    	        sb.append(c);
	        		previousWasUpper = false;
	        	}
	        	continue;
 	        }
	        if((!previousWasUpper)&&Character.isUpperCase(c)) {
	            sb.append(" ");
	        } else if((c == '_') || (c == '$')) {
	            sb.append(" ");
        		previousWasUpper = true;
        		continue;
	        }	        		
	        if(Character.isUpperCase(c)) {
        		previousWasUpper = true;
        	} else {
        		previousWasUpper = false;
        	}
	        sb.append(c);
	    }
	    return sb.toString();
	}
}
