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
     * @param id unique name (should be unique within entire drag&amp;drop widget)
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
