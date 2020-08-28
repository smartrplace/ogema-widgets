/**
 * ﻿Copyright 2020 Smartrplace UG
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
package de.smartrplace.ghl.ogema.resources;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.GenericFloatSensor;

/**
 *
 * @author jlapp
 */
public interface PondSensorReading extends PhysicalElement {

    GenericFloatSensor sensor();

    BooleanResource loggingEnabled();
    
    /**
     * @return Unit string as reported by the aquarium computer.
     */
    StringResource unit();
    
    /**
     * Arithmetic expression (optional) to transform the reported value.
     * The original value is referenced as 'x'.
     * 
     * @return arithmetic expression to transform reported value before storing it.
     */
    StringResource inputTransform();
}
