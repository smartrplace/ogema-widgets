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
package de.iee.sema.remote.message.receiver.model;

import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class MessageImpl implements Message {

	public MessageImpl(String title, String message, MessagePriority prio) {
		// TODO set the values
	}
	
	@Override
	public String title(OgemaLocale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String message(OgemaLocale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String link() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessagePriority priority() {
		// TODO Auto-generated method stub
		return null;
	}

}
