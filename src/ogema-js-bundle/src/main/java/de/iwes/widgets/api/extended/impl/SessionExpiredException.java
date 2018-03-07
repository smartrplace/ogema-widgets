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
