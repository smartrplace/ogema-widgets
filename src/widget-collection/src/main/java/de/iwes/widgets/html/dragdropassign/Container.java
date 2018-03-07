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
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) //If frontend adds unknown attributes ignore them in deserialization-process
public class Container implements Serializable {

    private static final long serialVersionUID = -3688106441597057702L;

    private String id;
    private String name;
    private String bgImagePath;
    private Collection<Container> containers;
    // optional
    private String iconLink;
    private String iconType;

	/**
     * Set up container that may contain child-containers.
     *
     * @param children list of direct child-containers. If empty the container
     * is a "leaf" container
     * @param id unique name (should be unique within entire drag&drop widget)
     * @param name container heading to be displayed
     * @param bgImagePath path, relative to application, for image for container-background
     */
    public Container(Collection<Container> children, String id, String name, String bgImagePath) {
        this.id = id;
        this.name = StringEscapeUtils.escapeHtml4(name);
        this.bgImagePath = bgImagePath;
        this.containers = children;
    }

    public Container(String id, String name, String bgImagePath) {
        this(new ArrayList<Container>(), id, name, bgImagePath);
    }

    public Container(String id, String name) {
        this(id, name, "");
    }

    public Collection<Container> getContainers() {
        return containers;
    }

    public void setContainers(Collection<Container> containers) {
        this.containers = containers;
    }

    public Container(String id) {
        this(id, null);
    }

    //dummy for jackson
    protected Container() {
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

    public String getBgImagePath() {
        return bgImagePath;
    }

    public void setBgImagePath(String bgImagePath) {
        this.bgImagePath = bgImagePath;
    }
    

    public String getIconLink() {
		return iconLink;
	}

	public void setIconLink(String iconLink) {
		this.iconLink = iconLink;
	}

	public String getIconType() {
		return iconType;
	}

	/**
	 * Typical value: "glyphicon glyphicon-envelope"
	 */
	public void setIconType(String iconType) {
		this.iconType = iconType;
	}
}
