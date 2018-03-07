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

package org.ogema.messages.sms;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.messaging.basic.services.config.model.SmsConfiguration;

public class SmsSenderPattern extends ResourcePattern<SmsConfiguration> {

	public SmsSenderPattern(Resource match) {
		super(match);
	}

	public final StringResource userName = model.userName();
	public final StringResource email = model.smsEmail();
	public final StringResource password = model.smsEmailPassword();
	public final StringResource serverURL = model.smsEmailServer();
	public final IntegerResource port = model.smsEmailPort();
	public final BooleanResource active = model.active();
	
	@Override
	public boolean accept() {
		return active.getValue();
	}
	
}