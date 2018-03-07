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
package de.iwes.widgets.html.tree;

import java.io.Serializable;

import de.iwes.widgets.html.Glyphicons;

public abstract class TreeElement<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String name;
    private String glyphicon = Glyphicons.EUR;
    private String css = "btn btn-danger";
    private final T object;
    
    public TreeElement(String name, T object) {
        this.name = name;
        this.object = object;
    }
    
    
    public String id() {
    	return name;
    }
    
    public String getName() {
        return name;
    }
    
    public T getObject() {
    	return object;
    }
    

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getGlyphicon() {
        return glyphicon;
    }

    public void setGlyphicon(String glyphicon) {
        this.glyphicon = glyphicon;
    }
    
}
