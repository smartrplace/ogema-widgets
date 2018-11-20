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
package de.iwes.ogema.remote.rest.configurator;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.ogema.remote.rest.connector.model.RestConnection;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageAnnotations.DefaultValue;

public class RestConnectorPattern extends ResourcePattern<RestConnection> {

	public RestConnectorPattern(Resource match) {
		super(match);
	}
	
	@DefaultValue(value="https://localhost:8444/rest/resources/path/to/resource")
	public final StringResource remotePath = model.remotePath(); 
	
	@DefaultValue(value="rest")
	public final StringResource remoteUser = model.remoteUser();

	@DefaultValue(value="rest")
	public final StringResource remotePw = model.remotePw();
	
	@DefaultValue(value="30000")
	public final IntegerResource pollingInterval = model.pollingInterval();
	
	@Existence(required=CreateMode.OPTIONAL)
	public final BooleanResource push = model.push();

}
