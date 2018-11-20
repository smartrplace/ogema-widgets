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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.textfield.ValueInputField;
import de.iwes.widgets.html.html5.FlexboxData;

public class ArrayManipulatorData<N extends Number> extends FlexboxData {

	private final ArrayManipulatorConfiguration config;
	private N[] array;
	private final int fixedSize; // negative for flexible size
	private final List<ValueInputField<?>> inputs;
	
	@SuppressWarnings("unchecked")
	protected ArrayManipulatorData(final ArrayManipulator<N> flexbox) {
		super(flexbox);
		this.config = flexbox.getConfig(); 
		this.fixedSize = flexbox.getConfig().getFixedSize();
		if (fixedSize >= 0) {
			this.array = (N[]) Array.newInstance(flexbox.getType(), fixedSize);
			if (!flexbox.getConfig().isAllowNull()) {
				final Class<N> type = flexbox.getType();
				if (type == int.class || type == long.class)
					Arrays.fill(array, 0);
				else
					Arrays.fill(array, 0F);
			}
			this.inputs = new ArrayList<>(fixedSize);
//			this.inputs = IntStream.range(0, fixedSize)
//				.mapToObj(i -> (ValueInputField<?>) new InputWidget<>(flexbox, flexbox.getType(), req, this, i))
//				.collect(Collectors.toList());
		}
		else {
			this.inputs = new ArrayList<>();
		}
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		updateWidgets(req);
		return super.retrieveGETData(req);
	}
	
	private void updateWidgets(OgemaHttpRequest req) {
		@SuppressWarnings("unchecked")
		final ArrayManipulator<N> widget = (ArrayManipulator<N>) this.widget;
		final N[] array = this.array;
		final int length = array == null ?0 : array.length;
		if (this.inputs.size() == length)
			return;
		final int diff = length - this.inputs.size();
		if (diff > 0) {
			IntStream.range(this.inputs.size(), length)
				.forEach(i -> inputs.add(new InputWidget<N>(widget, req, ArrayManipulatorData.this, i)));
		} else {
			final List<ValueInputField<?>> subList = this.inputs.subList(length, this.inputs.size());
			final List<ValueInputField<?>> toBeCleared = new ArrayList<>(subList);
			subList.clear();
			toBeCleared.forEach(w -> {
				try {
					ArrayManipulatorData.this.removeItem(w);
					w.destroyWidget();
				} catch (Exception e) {
					LoggerFactory.getLogger(ArrayManipulator.class).warn("Subwidget clearing failed",e);
				}
			});
		}
		
	}

	protected void setArray(N[] array, OgemaHttpRequest req) {
		if (Arrays.equals(this.array, array))
			return;
		if (fixedSize >= 0 && (array == null || array.length != fixedSize))
			throw new IllegalArgumentException("Only arrays of fixed size " + fixedSize + " allowed, got " + (array == null ? null : array.length));
		if (!config.isAllowNull()) {
			Arrays.stream(array)
				.filter(a -> a == null)
				.findAny().ifPresent(a -> {
					throw new IllegalArgumentException("Null values not allowed.");	
				});
		}
		this.array = array == null ? null : array.clone();
		updateWidgets(req);
	}
	
	protected N[] getArray() {
		final N[] array = this.array;
		return array == null ? null : array.clone();
	}
	
	protected int getLength() {
		final N[] array = this.array;
		return array == null ? 0 : array.length;
	}
	
	protected N get(int idx) {
		final N[] array = this.array;
		if (array == null)
			throw new NullPointerException("Array is null");
		return array[idx];
	}
	
	protected void set(int idx, N value) {
		final N[] array = this.array;
		if (array == null)
			throw new NullPointerException("Array is null");
		array[idx] = value;
	}

	static class InputWidget<N extends Number> extends ValueInputField<N> {
		
		private static final long serialVersionUID = 1L;
		private final ArrayManipulatorData<N> data;
		private final int idx;

		public InputWidget(ArrayManipulator<N> parent, OgemaHttpRequest req, ArrayManipulatorData<N> data, int idx) {
			super(parent, data.widget.getId() + "_input_" + idx, parent.getType(), req);
			this.data = data;
			this.idx = idx;
			final short nrDecimals = data.config.getNrDecimals();
			setDefaultNrDecimals(nrDecimals);
			setDefaultMargin("0.5em", false, false, true, false);
			this.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
			parent.triggerAction(this, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST, req);
			data.addItem(this);
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final N[] array = data.array;
			if (array == null || idx >= array.length) {
				setNumericalValue(null, req);
				return;
			}
			setNumericalValue(array[idx], req);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			final N[] array = this.data.array;
			if (array == null || idx >= array.length)
				return;
			final N value = getNumericalValue(req);
			if (value == null && !((ArrayManipulator<?>) this.data.widget).getConfig().isAllowNull())
				return;
			array[idx] = value;
		}
		
		
	}
	
	
}
