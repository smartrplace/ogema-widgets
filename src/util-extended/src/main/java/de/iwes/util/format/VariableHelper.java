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

@Deprecated
public class VariableHelper {

	/**
	 * @deprecated use ResourceUtils.getValidResourceName
	 * @param variableName
	 * @return
	 */
	@Deprecated()
	public static String getValidVariableName(String variableName) {
		if (variableName == null || variableName.isEmpty()) 
			return variableName; 
		char[] str = variableName.toCharArray();
		for (int i =0;i<str.length;i++) { 
			if (!Character.isJavaIdentifierPart(str[i]))
				str[i] = '_';
		}
		String out = new String(str);
		if (!Character.isJavaIdentifierStart(str[0]))
			out = "_" + out;
		return out;
	}
	
}
