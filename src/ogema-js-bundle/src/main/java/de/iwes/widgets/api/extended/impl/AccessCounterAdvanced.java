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
package de.iwes.widgets.api.extended.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.ogema.core.model.simple.IntegerResource;

import de.iwes.widgets.api.widgets.WidgetPage;

public class AccessCounterAdvanced {

	private final WidgetPage<?> page;
	private final Map<String,String[]> parameters;
	private final IntegerResource counter;
	
	public AccessCounterAdvanced(final WidgetPage<?> page, final Map<String,String[]> parameters, final IntegerResource counter) {
		Objects.requireNonNull(parameters);
		Objects.requireNonNull(page);
		Objects.requireNonNull(counter);
		if (parameters.isEmpty())
			throw new IllegalArgumentException("Parameter map must not be empty");
		this.parameters = new HashMap<>(parameters);
		this.page = page;
		this.counter = counter;
	}
	
	public void touched(final Map<String,String[]> parameters) {
		if (isApplicable(parameters)) {
			counter.getAndAdd(1);
		}
	}
	
	public int getCount() {
		return counter.getValue();
	}
	
	private boolean isApplicable(final Map<String,String[]> parameters) {
		if (parameters == null)
			return false;
		for (Map.Entry<String, String[]> entry: this.parameters.entrySet()) {
			final String key = entry.getKey();
			if (!parameters.keySet().contains(key))
				return false;
			for (String val : entry.getValue()) {
				if (!Arrays.asList(parameters.get(key)).contains(val))
					return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof AccessCounterAdvanced))
			return false;
		final AccessCounterAdvanced other = (AccessCounterAdvanced) obj;
		if (!other.page.equals(this.page))
			return false;
		if (other.parameters.size() != this.parameters.size())
			return false;
		return other.isApplicable(this.parameters) && this.isApplicable(other.parameters);
	}
	
	@Override
	public int hashCode() {
		return page.hashCode() * 7 + parameters.hashCode(); 
	}
	
}
