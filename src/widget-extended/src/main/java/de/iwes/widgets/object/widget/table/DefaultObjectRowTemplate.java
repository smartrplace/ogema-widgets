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

package de.iwes.widgets.object.widget.table;

import java.util.Map;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.html.complextable.RowTemplate;

public abstract class DefaultObjectRowTemplate<R> extends RowTemplate<R> {

	@Override
	public String getLineId(R object) {
		if (object instanceof Resource) {
			Resource r = (Resource) object;
			return ResourceUtils.getValidResourceName(r.getLocation());
		} else if (object instanceof ResourcePattern<?>) {
			return ResourceUtils.getValidResourceName(((ResourcePattern<?>) object).model.getLocation());
		} else {
			return ResourceUtils.getValidResourceName(object.toString().replace('$', '_'));
		}
	}

	@Override
	public Map<String, Object> getHeader() {
		// TODO Auto-generated method stub
		return null;
	}
}
