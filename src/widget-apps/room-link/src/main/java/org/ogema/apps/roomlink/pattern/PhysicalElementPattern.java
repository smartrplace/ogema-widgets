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
package org.ogema.apps.roomlink.pattern;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.locations.Building;
import org.ogema.model.locations.BuildingPropertyUnit;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;

/**
 *
 */
public class PhysicalElementPattern extends ResourcePattern<PhysicalElement> {

    public PhysicalElementPattern(Resource match) {
        super(match);
    }

    @Override
    public boolean accept() {
//        return model.isTopLevel();
    	if (model instanceof Room || model instanceof Building || model instanceof BuildingPropertyUnit) return false;
    	else if (model.isTopLevel()) return true;
    	Resource res = model.getParent();
    	while (res != null) {
    		if (res instanceof PhysicalElement) return false; // if any parent is itself a device, then the subdevice's location must not be set independently, but will be "inherited" from the parent
    		res = res.getParent();
    	}
    	return true;
    }

}
