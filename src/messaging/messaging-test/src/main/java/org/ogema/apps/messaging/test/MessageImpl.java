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

package org.ogema.apps.messaging.test;

import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class MessageImpl implements Message {
	
	private final String title;
	private final String msg;
	private final String link = null;
	private final MessagePriority prio;
	
	public MessageImpl(String title,String msg,MessagePriority prio) {
		this.title = title;
		this.msg = msg;
		this.prio = prio;
	}

	@Override
	public String title(OgemaLocale locale) {
		return title;
	}

	@Override
	public String message(OgemaLocale locale) {
		return msg;
	}

	@Override
	public String link() {
		return link;
	}

	@Override
	public MessagePriority priority() {
		return prio;
	}

	
	
}
