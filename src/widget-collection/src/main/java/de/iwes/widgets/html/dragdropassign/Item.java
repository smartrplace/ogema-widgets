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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) //If frontend adds unknown attributes ignore them in deserialization-process
public class Item implements Serializable {

    private static final long serialVersionUID = 7749554206613116094L;

    private Container container;

    private String id;
    private String name;
    private String description;
    private String iconPath;

    /** Set up item that can be dropped into containers
     * @param container container in which the item sits initially. If not assigned to any container
     * 			use the unassigned container you have defined
     * @param id unique id within entire widget
     * @param name Name to be displayed on item
     * @param description currently not used
     * @param iconPath path to icon displayed below name (path relative to HTML file)
     */
    public Item(Container container, String id, String name, String description, String iconPath) {
        this.container = container;
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconPath = iconPath;
    }

    public Item(String id) {
        this(null, id, null, null, null);
    }

    //Dummy for jackson
    protected Item() {
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

}
