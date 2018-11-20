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
package de.iwes.widgets.api.extended;

public class HtmlLibrary {
	
	private final LibType type; 
	private final String path;
	private final String identifier;
	private final boolean loadEarly;

	public HtmlLibrary(LibType type,String identifier,String path) {
		this(type, identifier, path, false);
	}
	
	/**
	 * 
	 * @param type
	 * @param identifier
	 * 		for JS libs: 'if (identifier) {}' must be satisfied iff the library has been loaded
	 * @param path
	 */
	public HtmlLibrary(LibType type,String identifier,String path, boolean loadEarly) {
		this.type = type;
		this.path = path;
		this.identifier = identifier;
		this.loadEarly = loadEarly;
	}
	
	public enum LibType {
		JS, CSS
	}
	
	public LibType getType() {
		return type;
	}

	public String getPath() {
		return path;
	}
	
	public String getIdentifier() {
		return identifier;
	}
		
	public boolean isLoadEarly() {
		return loadEarly;
	}
	
	public String getHtmlString() {
		switch (type) {
		case JS:
			return "<script src=\""+ path + "\"></script>";
		case CSS:
			return "<link rel=\"stylesheet\" href=\""+ path +"\">";
		default:
			return null;
		}
	}
	
	// the reason to compare only paths is that two libraries with the same path in fact refer to the same file 
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof HtmlLibrary)) 
			return false;
		return this.path.equals(((HtmlLibrary) obj).getPath());
	}
	
	@Override
	public int hashCode() {
		return path.hashCode() + 2;
	}
}
