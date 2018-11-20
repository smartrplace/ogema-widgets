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
package de.iwes.widgets.html.arrays;

/**
 * Create an instance using {@link ArrayManipulatorConfigurationBuilder}.
 */
public class ArrayManipulatorConfiguration {

	private final boolean allowAdd;
	private final boolean allowDelete;
	private final boolean allowNull;
	private final short nrDecimals;
	// -1 if size is not fixed
	private final int fixedSize;
	
	ArrayManipulatorConfiguration(boolean allowAdd, boolean allowDelete, boolean allowNull, int fixedSize, short nrDecimals) {
		this.allowAdd = allowAdd;
		this.allowDelete = allowDelete;
		this.allowNull = allowNull;
		this.fixedSize = fixedSize;
		this.nrDecimals = nrDecimals;
		if ((allowAdd || allowDelete) && fixedSize >= 0)
			throw new IllegalArgumentException("Fixed size not compatible with allow add/delete");
	}

	public boolean isAllowAdd() {
		return allowAdd;
	}

	public boolean isAllowDelete() {
		return allowDelete;
	}

	public boolean isAllowNull() {
		return allowNull;
	}

	public int getFixedSize() {
		return fixedSize;
	}
	
	public short getNrDecimals() {
		return nrDecimals;
	}
	
}
