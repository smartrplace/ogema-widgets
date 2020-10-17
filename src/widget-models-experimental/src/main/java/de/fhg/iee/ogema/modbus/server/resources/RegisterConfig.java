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

import org.ogema.core.model.simple.IntegerResource;

/**
 *
 * @author jan.lapp@iee.fraunhofer.de
 */
public interface RegisterConfig extends PublishingDataElement {

    //IntegerResource address();

    /**
     * @return number of registers, where applicable, default 1.
     */
    IntegerResource registerCount();
    
    /**
     * @return create as writable (holding) register, default is {@code false} (input register)
     */
    //BooleanResource writable();
    
}
