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
package de.iwes.widgets.api.extended.impl;

public class SessionExpiredException extends IllegalStateException {

	private static final long serialVersionUID = 71642345130559645L;
	
	public SessionExpiredException() {
		super();
	}
	
	public SessionExpiredException(String message) {
		super(message);
	}
	
    public SessionExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public SessionExpiredException(Throwable cause) {
        super(cause);
    }

}
