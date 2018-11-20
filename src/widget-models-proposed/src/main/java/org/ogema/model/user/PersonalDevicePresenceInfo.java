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
package org.ogema.model.user;

import org.ogema.core.model.ModelModifiers.NonPersistent;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Configuration;

/** Representation of a mobile device (typically a tablet, smartphone or similar) known to the OGEMA gateway*/
public interface PersonalDevicePresenceInfo extends Configuration {

	/**ID of the device, usually provided by the mobile device itself*/
	public StringResource mobilSerialId();
	/**SSID of WLAN in which the device was recognized, usually just the SSID of the local WLAN to
	 * which the OGEMA Gateway is connected
	 */
	public StringResource ssId();
	
	/**This information should be read by the device. If trackPresence is set to false the device should not
	 * connect constantly anymore, just occasionally for data synchronization.
	 */
	public BooleanResource trackPresence();
	/**Message should be read by the device, processing of the message is specific to device/mobile app used*/
	public StringResource messageToDevice();
	@Deprecated
	public StringResource lastMessageIdReceivedByDevice();
	/**Owner/ holder of the mobile device. The presence of this person may be detected by the presence
	 * of the device*/
	public NaturalPerson user();
	
	@NonPersistent
	/**Message received from device. Note that initially the device should write to the resource dataInFlow of the
	 * driver, but reactions on messageToDevice or other specific information may be provided here
	 */
	public TimeResource lastMessageReceived();
	@NonPersistent
	@Deprecated
	/**@Deprecated: Only remains for manual indication of device presence*/
	public BooleanResource presenceDetected();
}
