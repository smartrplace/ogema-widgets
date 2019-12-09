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
package org.ogema.messaging.telegram.connector;

public class Constants {
	
	public final static String BOT_USER_NAME_PROPERTY = "org.ogema.messaging.telegram.username";
	public final static String BOT_KEY_PROPERTY = "org.ogema.messaging.telegram.key";
	public final static String BOT_PRIVATE_PROPERTY = "org.ogema.messaging.telegram.privatebot";
	
	public final static String BOT_USER_NAME = System.getProperty(BOT_USER_NAME_PROPERTY);
	public final static String BOT_KEY = System.getProperty(BOT_KEY_PROPERTY);
	/**
	 * If the bot is tagged non-public, the list of chats will be generated from the API,
	 * else users will have to be added manually
	 */
	public final static boolean BOT_PRIVATE = Boolean.getBoolean(BOT_PRIVATE_PROPERTY);
	
}
