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

package de.iwes.widgets.html.dragdropassign;

import java.io.IOException;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"unassigned"})
public class DragDropData implements Serializable {

    private static final long serialVersionUID = 1436556704893223181L;
    private Collection<Container> containers;
    private Collection<Item> items;
    
    /**
     * Set up data for drag&assign widget
     *
     * @param containers: Containers into which items can be dropped
     * @param items: Items that can be dropped into containers. All items must
     * be assigned to exactly one container, so usually you have to define an
     * "unassigned container" if there are unassigned items.
     */
    public DragDropData(Collection<Container> containers, Collection<Item> items) {
        this.containers = containers;
        this.items = items;
    }
    
    public DragDropData() {
        this.containers = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public Collection<Container> getContainers() {
        return containers;
    }

    public void setContainers(Collection<Container> containers) {
        this.containers = containers;
    }

    public Collection<Item> getItems() {
        return items;
    }

    public void setItems(Collection<Item> items) {
        this.items = items;
    }

    @JsonIgnore
    /**
     * Assign item to new container, the item will not be assigned to its
     * previous container anymore
     */
    public void update(Item item, Container to) {
        item.setContainer(to);
    }

    @JsonIgnore
    protected Item getItem(Item item) {
        for (Item i : items) {
            if (i.getId().equals(item.getId())) {
                return i;
            }
        }
        return null;
    }

    @JsonIgnore
    protected Item getItem(String id) {
        for (Item i : items) {
            if (i.getId().equals(id)) {
                return i;
            }
        }
        return null;
    }

    @JsonIgnore
    protected Container getContainer(Container container) {
        Container result = null;

        for (Container c : containers) {
            result = getContainer(c, container);
            if (result != null) {
                return result;
            }
        }

        return result;
    }

    @JsonIgnore //Internal helper-method
    private Container getContainer(Container c, Container container) {
        Container result = null;

        if (c.getId().equals(container.getId())) {
            result = c;
        } else {
            if (c.getContainers() != null && c.getContainers().size() > 0) {
                for (Container c2 : c.getContainers()) {
                    result = getContainer(c2, container);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return result;
    }

    @JsonIgnore
    public String toJSON() throws IOException {
    	try {
			return AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {

				@Override
				public String run() throws Exception {
			    	ObjectMapper mapper = new ObjectMapper();
			        return mapper.writeValueAsString(DragDropData.this);
				}
			});
		} catch (PrivilegedActionException e) {
			Exception cause = e.getException();
			if (cause instanceof IOException)
				throw (IOException) cause;
			else
				throw new RuntimeException(cause);
		}


    }
}
