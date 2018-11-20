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
