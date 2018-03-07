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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NodeDO<T> extends TreeElement<T> {

	private static final long serialVersionUID = 1L;
	private List<NodeDO<T>> childs;
    private List<LeafDO<T>> data;
    
    public NodeDO(List<NodeDO<T>> childs, List<LeafDO<T>> data, String name, String css, T object) {
        this(childs, data, name, object);
        setCss(css);
    }

    public NodeDO(List<NodeDO<T>> childs, List<LeafDO<T>> data, String name, T object) {
        this(name, object);
        this.childs = childs;
        this.data = data;
    }

    public NodeDO(String name, T object) {
    	super(name, object);
        this.childs = new ArrayList<>();
        this.data = new ArrayList<>();
    }

    public List<NodeDO<T>> getChilds() {
        return childs;
    }

    public void add(NodeDO<T> child) {
        childs.add(child);
    }

    public void setChilds(List<NodeDO<T>> childs) {
        this.childs = childs;
    }

    public List<LeafDO<T>> getData() {
        return data;
    }

    public void addData(LeafDO<T> data) {
        this.data.add(data);

    }

    public void setChild(NodeDO<T> child) {
        childs.add(child);
    }

    public void setData(List<LeafDO<T>> data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.childs);
        hash = 37 * hash + Objects.hashCode(this.data);
        hash = 37 * hash + Objects.hashCode(this.getName());
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
        final NodeDO other = (NodeDO) obj;
        if (!Objects.equals(this.childs, other.childs)) {
            return false;
        }
        if (!Objects.equals(this.data, other.data)) {
            return false;
        }
        if (!Objects.equals(this.getName(), other.getName())) {
            return false;
        }
        return true;
    }

}
