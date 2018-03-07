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
package de.iwes.widgets.template;

import java.util.Objects;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class DefaultLabelledItem implements LabelledItem {

	private final String id;
	private final String label;
	private final String description;
	
	/**
	 * @param id
	 * @param label
	 * 		may be null, in which case the id is used as label
	 * @param description
	 * 		may be null
	 */
	public DefaultLabelledItem(String id, String label, String description) {
		this.id = Objects.requireNonNull(id);
		this.label = label != null ? label : id;
		this.description = description;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public String label(OgemaLocale locale) {
		return label;
	}

	@Override
	public String description(OgemaLocale locale) {
		return description;
	}
	
	
	
}
