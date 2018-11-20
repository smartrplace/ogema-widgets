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

import java.util.Collection;
import java.util.List;

public abstract class LinkingOption extends LinkingOptionType {

	/**
	 * Set default options:
	 * multiple selections allowed, no selection required
	 */
	protected LinkingOption() {
		this(false, true);
	}
	
	protected LinkingOption(boolean selectionRequired, boolean multipleSelectionsAllowed) {
		super(selectionRequired, multipleSelectionsAllowed);
	}
	
	/**
	 * Get the list of items for this selection, depending on the items that are selected
	 * in the dependencies.
	 * @param dependencies
	 * @return
	 */
	public abstract List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies);

	/**
	 * Other selections on which this one depends.
	 * @return
	 */
	@Override
	public abstract LinkingOption[] dependencies();
	
	/**
	 * Filters out null values
	 * @param options
	 * @return
	 */
	protected static LinkingOption[] createDependenciesArray(LinkingOption... options) {
		//return (LinkingOption[]) LinkingOptionType.createDependenciesArray(options);
		int cnt = 0;
		for (LinkingOption opt : options) {
			if (opt != null) {
				cnt++;
			}
		}
		final LinkingOption[] array = new LinkingOption[cnt];
		cnt = 0;
		for (LinkingOption opt: options) {
			if (opt != null)
				array[cnt++] = opt;
		}
		return array;
	}

}
