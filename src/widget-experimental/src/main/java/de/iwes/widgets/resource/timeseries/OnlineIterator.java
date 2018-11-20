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
package de.iwes.widgets.resource.timeseries;

import java.util.Iterator;

import org.ogema.core.channelmanager.measurements.SampledValue;

import com.google.common.annotations.Beta;

@Beta
public interface OnlineIterator extends Iterator<SampledValue> {

	/**
	 * At most one more point will be returned after calling this method,
	 * then {@link #hasNext()} will return false. <br>
	 * Contrary to most other iterator methods it is safe to call this 
	 * from a thread different from the iterator main thread (the one accessing {@link #hasNext()}
	 * and {@link #next()}).
	 */
	void stop();
	
	/**
	 * Remove is not supported by OnlineIterator
	 */
	@Override
	void remove() throws UnsupportedOperationException;
	
}
