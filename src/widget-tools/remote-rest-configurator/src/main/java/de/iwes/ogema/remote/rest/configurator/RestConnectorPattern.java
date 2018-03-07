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
