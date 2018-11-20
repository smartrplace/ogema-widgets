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
