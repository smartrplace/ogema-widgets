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