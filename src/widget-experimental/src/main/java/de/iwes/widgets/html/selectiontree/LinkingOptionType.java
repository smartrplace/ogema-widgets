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
package de.iwes.widgets.html.selectiontree;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public abstract class LinkingOptionType implements Comparable<LinkingOptionType> {

	private final boolean selectionRequired;
	private final boolean multipleSelectionsAllowed;

	/**
	 * Set default options:
	 * multiple selections allowed, no selection required
	 */
	protected LinkingOptionType() {
		this(false, true);
	}
	
	protected LinkingOptionType(boolean selectionRequired, boolean multipleSelectionsAllowed) {
		this.multipleSelectionsAllowed = multipleSelectionsAllowed;
		this.selectionRequired = selectionRequired;
	}
	
	/**Should be unique for different linking options and may be used as part of a widget id*/
	public abstract String id();
	
	/**
	 * @param locale
	 * @return
	 * 		E.g. "Select a room", or "Select a device"
	 */
	public abstract String label(OgemaLocale locale);
	
	/**
	 * Other selections on which this one depends.
	 * @return
	 */
	public abstract LinkingOptionType[] dependencies();
	
	public boolean selectionRequired() {
		return selectionRequired;
	}
	
	public boolean multipleSelectionsAllowed() {
		return multipleSelectionsAllowed;
	}
	
	@Override
	public final int compareTo(final LinkingOptionType o) {
		if (hasRecursiveDependency(this, o))
			return 1;
		if (hasRecursiveDependency(o, this))
			return -1;
		return 0;
	}
	
	private final static boolean hasRecursiveDependency(final LinkingOptionType base, final LinkingOptionType dep) {
		final LinkingOptionType[] bd = base.dependencies();
		if (bd == null)
			return false;
		for (LinkingOptionType d : bd) {
			if (d.equals(dep))
				return true;
			if (hasRecursiveDependency(d, dep))
				return true;
		}
		return false;
	}
	
	@Override
	public final boolean equals(final Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof LinkingOptionType))
			return false;
		return id().equals(((LinkingOptionType) obj).id());
	}
	
	@Override
	public final int hashCode() {
		return id().hashCode();
	}
	
	@Override
	public String toString() {
		return "Linking option: (" + id() + ": " + label(OgemaLocale.ENGLISH) + ")";
	}
	
	/**
	 * Filters out null values
	 * @param options
	 * @return
	 */
	protected static LinkingOptionType[] createDependenciesArray(LinkingOptionType... options) {
		int cnt = 0;
		for (LinkingOptionType opt : options) {
			if (opt != null) {
				cnt++;
			}
		}
		final LinkingOptionType[] array = new LinkingOptionType[cnt];
		cnt = 0;
		for (LinkingOptionType opt: options) {
			if (opt != null)
				array[cnt++] = opt;
		}
		return array;
	}
	
}
