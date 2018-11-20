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

public class LeafDO<T> extends TreeElement<T> {

	private static final long serialVersionUID = 1L;
	private final String value;

	public LeafDO(String name, String value, String glyphicon, String css, T object) {
        this(name, value, glyphicon, object);
        setCss(css);
    }
    
    public LeafDO(String name, String value, String glyphicon, T object) {
        this(name, value, object);
        setGlyphicon(glyphicon);
    }
    public LeafDO(String name, String value, T object) {
        super(name, object);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
        hash = 37 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings({ "rawtypes" })
		final LeafDO other = (LeafDO) obj;
        if ((this.getName() == null) ? (other.getName() != null) : !this.getName().equals(other.getName())) {
            return false;
        }
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
