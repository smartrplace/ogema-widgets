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
