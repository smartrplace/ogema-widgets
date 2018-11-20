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
package de.iwes.widgets.api.messaging;

public enum MessagePriority {
	
	LOW(0),
	
	MEDIUM(1),
	
	HIGH(2);
	
	private MessagePriority(int priority) {
		this.priority = priority;
	}

	private final int priority;

	/**
	 * Gets the current priority set.
	 */
	public int getPriority() {
		return priority;
	}
}
