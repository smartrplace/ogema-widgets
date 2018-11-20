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
 * Create an {@link ArrayManipulatorConfiguration}
 */
public class ArrayManipulatorConfigurationBuilder {

	private boolean allowAdd = false;
	private boolean allowDelete = false;
	private boolean allowNull = false;
	private short nrDecimals = 2;
	// -1 if size is not fixed
	private int fixedSize = -1;
	
	private ArrayManipulatorConfigurationBuilder() {}
	
	public static ArrayManipulatorConfigurationBuilder newInstance() {
		return new ArrayManipulatorConfigurationBuilder();
	}
	
	public ArrayManipulatorConfiguration build() {
		return new ArrayManipulatorConfiguration(allowAdd, allowDelete, allowNull, fixedSize, nrDecimals);
	}

	/**
	 * Allow the user to add array entries?
	 * Default: false
	 * @param allowAdd
	 * @return
	 */
	public ArrayManipulatorConfigurationBuilder setAllowAdd(boolean allowAdd) {
		this.allowAdd = allowAdd;
		if (allowAdd)
			fixedSize = -1;
		return this;
	}

	/**
	 * Allow the user to delete array entries?
	 * Default: false
	 * 
	 * @param allowDelete
	 * @return
	 */
	public ArrayManipulatorConfigurationBuilder setAllowDelete(boolean allowDelete) {
		this.allowDelete = allowDelete;
		if (allowDelete)
			fixedSize = -1;
		return this;
	}

	/**
	 * Allow null entries in the arry?
	 * Default: false
	 * @param allowNull
	 * @return
	 */
	public ArrayManipulatorConfigurationBuilder setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
		return this;
	}
	
	/**
	 * Set a fixed array size. Negative value means flexible size. 
	 * Default: -1.
	 * @param fixedSize
	 * @return
	 */
	public ArrayManipulatorConfigurationBuilder setFixedSize(int fixedSize) {
		this.fixedSize = fixedSize;
		if (fixedSize >= 0) {
			allowAdd = false;
			allowDelete = false;
		}
		return this;
	}
	
	/**
	 * Set number of decimals. Negative value means: show all digits. Only relevant for float or double values
	 * Default: 2.
	 * @param nrDecimals
	 * @return
	 */
	public ArrayManipulatorConfigurationBuilder setNrDecimals(short nrDecimals) {
		this.nrDecimals= nrDecimals;
		return this;
	}
	
}
