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
package de.iwes.util.develconfig;

import org.ogema.core.logging.LogLevel;
import org.slf4j.Logger;

/**
 * @deprecated use logger directly instead in productive apps... this util class requires
 * expensive string operations even when the result is not logged in the end. 
 */
@Deprecated
public class LoggerUtil {
	public static void log(Logger log, String message, LogLevel level) {
		switch(level) {
		case ERROR:
			log.error(message);
			break;
		case WARNING:
			log.warn(message);
			break;
		case INFO:
			log.info(message);
			break;
		case DEBUG:
			log.debug(message);
			break;
		case TRACE:
			log.trace(message);
			break;
		default:
			throw new IllegalStateException("unknown loglevel:"+level);
		}
	}

}
