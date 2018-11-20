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
package de.iwes.widgets.reswidget.scheduleplot.api;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class DefaultMaxValuesSupplier implements Supplier<Cache<String,MaxValBuffer>> {
	
	/**
	 * As long as a session exists, there will be strong references;
	 * if no session exists any more, we can drop the buffer
	 */
	private volatile WeakReference<Cache<String, MaxValBuffer>> ref = new WeakReference<Cache<String,MaxValBuffer>>(null);

	@Override
	public Cache<String, MaxValBuffer> get() {
		Cache<String, MaxValBuffer> existing = ref.get();
		if (existing != null)
			return existing;
		synchronized (this) {
			existing = ref.get();
			if (existing != null)
				return existing;
			existing = CacheBuilder.newBuilder().softValues().build();
			ref = new WeakReference<Cache<String,MaxValBuffer>>(existing);
			return existing;
		}
	}

	
	
}
