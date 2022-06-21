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
 * WARNING: If the String operations required to provide the message are expensive and the log level
 * 		is deactivated in normal operation, do first check if log level is enabled (see examples below).
 *      You may also use logger directly instead in productive apps... otherise this util class requires
 *    	expensive string operations even when the result is not logged in the end. 
 */
//@Deprecated
public class LoggerUtil {
	public static void log(Logger log, String message, LogLevel level) {
		log(log, message, level, false);
	}
	public static void log(Logger log, String message, LogLevel level, boolean writeToConsoleAlways) {
		boolean isWrittenToConsole = false;
		switch(level) {
		case ERROR:
			if(log.isErrorEnabled()) {
				isWrittenToConsole = true;
				log.error(message);
			}
			break;
		case WARNING:
			if(log.isWarnEnabled()) {
				isWrittenToConsole = true;
				log.warn(message);
			}
			break;
		case INFO:
			if(log.isInfoEnabled()) {
				isWrittenToConsole = true;
				log.info(message);
			}
			break;
		case DEBUG:
			if(log.isDebugEnabled()) {
				isWrittenToConsole = true;
				log.debug(message);
			}
			break;
		case TRACE:
			if(log.isTraceEnabled()) {
				isWrittenToConsole = true;
				log.trace(message);
			}
			break;
		default:
			throw new IllegalStateException("unknown loglevel:"+level);
		}
		if(writeToConsoleAlways && (!isWrittenToConsole)) {
			System.out.println(log.getName()+" :: "+message);
		}
	}

}
