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
