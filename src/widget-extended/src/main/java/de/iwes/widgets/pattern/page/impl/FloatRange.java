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
