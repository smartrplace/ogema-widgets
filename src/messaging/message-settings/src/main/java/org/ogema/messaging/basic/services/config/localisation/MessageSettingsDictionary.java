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

package org.ogema.messaging.basic.services.config.localisation;

import de.iwes.widgets.api.widgets.localisation.LocaleDictionary;

public interface MessageSettingsDictionary extends LocaleDictionary {
	
	final static String SELECT_CONNECTOR_LINK = "/de/iwes/ogema/apps/select-connector/index.html";
	final static String MESSAGE_READER_LINK = "/de/iwes/ogema/apps/message/reader/index.html";
	final static String SENDER_LINK = "sender.html";
	final static String RECEIVER_LINK = "index.html";

	String headerSenders();
	String headerReceivers();
	String descriptionReceivers();
	String descriptionSenders();
	
}
