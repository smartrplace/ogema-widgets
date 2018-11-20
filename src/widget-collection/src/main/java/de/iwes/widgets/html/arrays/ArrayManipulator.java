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

import java.util.Arrays;
import java.util.stream.IntStream;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.FlexboxData;
import de.iwes.widgets.html.html5.flexbox.FlexDirection;

//TODO support add and delete
public class ArrayManipulator<N extends Number> extends Flexbox {

	private static final long serialVersionUID = 1L;
	private final Class<N> type;
	private final ArrayManipulatorConfiguration config;
	
	/**
	 * Create an ArrayManipulator with default settings
	 * @param page
	 * @param id
	 * @param type
	 */
	public ArrayManipulator(WidgetPage<?> page, String id, Class<N> type) {
		this(page, id, type, null);
	}

	/**
	 * @param page
	 * @param id
	 * @param type
	 * @param config
	 */
	public ArrayManipulator(WidgetPage<?> page, String id, Class<N> type,
			ArrayManipulatorConfiguration config) {
		super(page, id, false);
		this.type = type;
		this.config = config == null ? ArrayManipulatorConfigurationBuilder.newInstance().build() : config;
		setDefaultFlexDirection(FlexDirection.COLUMN);
	}

	public ArrayManipulator(OgemaWidget parent, String id, OgemaHttpRequest req, Class<N> type) {
		this(parent, id, req, type, null);
	}
	
	public ArrayManipulator(OgemaWidget parent, String id, OgemaHttpRequest req, Class<N> type, 
			ArrayManipulatorConfiguration config) {
		super(parent, id, req);
		this.type = type;
		this.config = config == null ? ArrayManipulatorConfigurationBuilder.newInstance().build() : config;
		setDefaultFlexDirection(FlexDirection.COLUMN);
	}
	
	@Override
	public FlexboxData createNewSession() {
		return new ArrayManipulatorData<>(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayManipulatorData<N> getData(OgemaHttpRequest req) {
		return (ArrayManipulatorData<N>) super.getData(req);
	}
	
	public void setArray(N[] array, OgemaHttpRequest req) {
		getData(req).setArray(array, req);
	}
	
	public N[] getArray(OgemaHttpRequest req) {
		return getData(req).getArray();
	}
	
	public int getLength(OgemaHttpRequest req) {
		return getData(req).getLength();
	}
	
	protected N get(int idx, OgemaHttpRequest req) {
		return getData(req).get(idx);
	}
	
	protected void set(int idx, N value, OgemaHttpRequest req) {
		getData(req).set(idx, value);
	}
	
	public Class<N> getType() {
		return type;
	}
	
	/**
	 * Cast to a float[] array
	 * @param req
	 * @return
	 * @throws NullPointerException if null is allowed as value,
	 * 		and the array contanis a null value
	 */
	public float[] getAsFloatArray(OgemaHttpRequest req) {
		final N[] array = getArray(req);
		if (array == null)
			return null;
		final float[] arr = new float[array.length];
		IntStream.range(0, array.length).forEach(i -> arr[i] = array[i].floatValue());
		return arr;
	}
	
	/**
	 * Cast to a double[] array
	 * @param req
	 * @return
	 * @throws NullPointerException if null is allowed as value,
	 * 		and the array contanis a null value
	 */
	public double[] getAsDoubleArray(OgemaHttpRequest req) {
		final N[] array = getArray(req);
		if (array == null)
			return null;
		return Arrays.stream(array)
			.mapToDouble(f -> f.doubleValue())
			.toArray();
	}
	
	/**
	 * Cast to a int[] array
	 * @param req
	 * @return
	 * @throws NullPointerException if null is allowed as value,
	 * 		and the array contanis a null value
	 */
	public int[] getAsIntArray(OgemaHttpRequest req) {
		final N[] array = getArray(req);
		if (array == null)
			return null;
		final int[] arr = new int[array.length];
		IntStream.range(0, array.length).forEach(i -> arr[i] = array[i].intValue());
		return arr;
	}
	
	/**
	 * Cast to a long[] array
	 * @param req
	 * @return
	 * @throws NullPointerException if null is allowed as value,
	 * 		and the array contanis a null value
	 */
	public long[] getAsLongArray(OgemaHttpRequest req) {
		final N[] array = getArray(req);
		if (array == null)
			return null;
		return Arrays.stream(array)
			.mapToLong(f -> f.longValue())
			.toArray();
	}
	
	public ArrayManipulatorConfiguration getConfig() {
		return config;
	}
	
}
