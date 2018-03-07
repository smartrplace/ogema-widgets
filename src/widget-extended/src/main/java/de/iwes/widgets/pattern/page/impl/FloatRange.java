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

package de.iwes.widgets.pattern.page.impl;

public class FloatRange implements FilterRange {
	
	private final float lowerValue;
	private final float upperValue;
	private final boolean lowerIncluded;
	private final boolean upperIncluded;
	
	public FloatRange(float lowerBound,float upperBound,boolean lowerIncluded, boolean upperIncluded) {
		this.lowerValue = lowerBound;
		this.upperValue = upperBound;
		this.lowerIncluded = lowerIncluded;
		this.upperIncluded = upperIncluded;
	}
	
	public float getLowerValue() {
		return lowerValue;
	}

	public float getUpperValue() {
		return upperValue;
	}

	public boolean isLowerIncluded() {
		return lowerIncluded;
	}

	public boolean isUpperIncluded() {
		return upperIncluded;
	}

	

}
