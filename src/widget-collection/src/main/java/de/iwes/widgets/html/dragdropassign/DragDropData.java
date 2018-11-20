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
     * Set up data for drag&amp;assign widget
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
