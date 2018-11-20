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
package org.ogema.tools.widgets.test.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;

/**
 * A widget plus information on its dependencies 
 */
public class DependencyWidget<T extends OgemaWidget> {

	private final T textField;
	// widgets depending on this
	private final List<DependencyWidget<?>> dependencies = new ArrayList<>();
	
	public DependencyWidget(T textField) {
		this.textField = textField;
	}
	
	public void addDependency(DependencyWidget<?> other, TriggeringAction a, TriggeredAction b) {
		Objects.requireNonNull(a);
		Objects.requireNonNull(b);
		Objects.requireNonNull(other);
		textField.triggerAction(other.getWidget(), a, b);
		dependencies.add(other);
		// FIXME
		System.out.println("     ii New dependency: " + this.getWidget().getId() + " -> " + other.getWidget().getId() + ": " + a + " -> " + b);
	}
	
	public T getWidget() {
		return textField;
	}
	
	public List<DependencyWidget<?>> getDependencies() {
		return new ArrayList<>(dependencies);
	}
	
	private static final int getIndex(List<OgemaWidget> widgets, DependencyWidget<?> target) {
		int idx = widgets.indexOf(target.getWidget());
		Assert.assertTrue("Registered widget is missing from list: " + target.getWidget(), idx >= 0);
		return idx;
	}
	
	public void verifyOrdering(List<OgemaWidget> widgets) {
		int ownIdx = getIndex(widgets, this);
		for (DependencyWidget<?> dependency: dependencies) {
			int otherIdx = getIndex(widgets, dependency);
			Assert.assertTrue("Widget dependency not reflected in sorted widgets list: " + dependency.getWidget() + " depends on " + 
					this.getWidget() + ", indices " + otherIdx + ": " + ownIdx, ownIdx < otherIdx);
		}
	}
	
	
}
