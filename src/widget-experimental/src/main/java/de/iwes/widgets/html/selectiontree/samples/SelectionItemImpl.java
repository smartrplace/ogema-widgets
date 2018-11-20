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
package de.iwes.widgets.html.selectiontree.samples;

import java.util.Objects;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class SelectionItemImpl implements SelectionItem {

	private final String id;
	private final String label;
	
	public SelectionItemImpl(String id, String label) {
		Objects.requireNonNull(id);
		Objects.requireNonNull(label);
		this.id = id;
		this.label = label;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public String label(OgemaLocale locale) {
		return label;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof SelectionItem))
			return false;
		return id.equals(((SelectionItem) obj).id());
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
		
	@Override
	public String toString() {
		return "SelectionItemImpl: (" + id + ": " + label + ")";
	}
	
}
