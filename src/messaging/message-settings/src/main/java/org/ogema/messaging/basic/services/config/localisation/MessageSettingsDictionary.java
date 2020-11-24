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
package org.ogema.messaging.basic.services.config.localisation;

import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;

public interface MessageSettingsDictionary extends LocaleDictionary {
	
	final static String SELECT_CONNECTOR_LINK = "/de/iwes/ogema/apps/select-connector/index.html";
	final static String MESSAGE_READER_LINK = "/de/iwes/ogema/apps/message/reader/index.html";
	final static String SENDER_LINK = "/de/iwes/ogema/apps/messageSettings/sender.html"; //"sender.html";
	final static String RECEIVER_LINK = "index.html";

	String headerSenders();
	String headerReceivers();
	String descriptionReceivers();
	String descriptionSenders();
	
}
