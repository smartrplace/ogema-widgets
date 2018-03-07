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
