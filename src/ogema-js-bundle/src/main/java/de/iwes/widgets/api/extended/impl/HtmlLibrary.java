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

package de.iwes.widgets.api.extended.impl;

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
