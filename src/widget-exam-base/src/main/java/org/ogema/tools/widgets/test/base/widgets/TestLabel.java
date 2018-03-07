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

package org.ogema.tools.widgets.test.base.widgets;

import org.ogema.tools.widgets.test.base.GenericWidget;
import org.ogema.tools.widgets.test.base.WidgetLoader;

public class TestLabel extends GenericWidget {

	public TestLabel(WidgetLoader client, String id, String servletPath) {
		super(client, id, servletPath);
	}
	
	public String getText() {
		return getWidgetData().getString("text");
	}

}
