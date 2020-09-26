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
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Configuration;

/**
 *
 * @author jan.lapp@iee.fraunhofer.de
 */
public interface ServerConfig extends Configuration {
    
    /**
     * The address on which to bind the server (optional, default: all available
     * interfaces)
     * @return address the server listens on (optional)
     */
    StringResource address();
    
    IntegerResource port();
    
    ResourceList<UnitConfig> units();
    
}
