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
package de.iwes.tools.system.supervision.model;

import org.ogema.core.model.simple.TimeResource;

public class SupervisionUtils {
	
	public final static long MIN_SUPERVISION_ITV = 60 * 1000;
	public final static long DEFAULT_RAM_SUPERVISION_ITV = 60*60*1000;
	public final static long DEFAULT_DISK_SUPERVISION_ITV = 24*60*60*1000;
	public final static long DEFAULT_RESOURCE_SUPERVISION_ITV = 60*60*1000;
	
	public static long getInterval(final TimeResource itvRes, long defaultItv) {
		long proposed = 0;
		if (itvRes.isActive())
			proposed = itvRes.getValue();
		if (proposed < MIN_SUPERVISION_ITV) {
			proposed = defaultItv;
			itvRes.<TimeResource> create().setValue(defaultItv);
			itvRes.activate(false);
		}
		return proposed;
	}

}
