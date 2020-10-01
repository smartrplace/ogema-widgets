/*
 * Copyright 2019 Fraunhofer IEE.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fhg.iee.ogema.modbus.server.resources;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.Configuration;

/**
 *
 * @author jan.lapp@iee.fraunhofer.de
 */
public interface UnitConfig extends Configuration {
    
    IntegerResource unitId();
    
    ResourceList<RegisterConfig> registers();
    
    ResourceList<DiscreteIO> discreteIOs();
    
}
