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
package de.iwes.util.performanceeval;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.LogLevel;

import de.iwes.util.develconfig.LoggerUtil;

/**This version measures everything from creation until finish() is called*/
public class ExecutionTimeLogger {
	private final String label;
	private final LogLevel logLevel;
	private final ApplicationManager appMan;
	
	private long tstart;
	private long intermediateStart;
	
	public boolean logAlwaysToConsole = false;

	public ExecutionTimeLogger(String label, ApplicationManager appMan) {
		this(label, LogLevel.INFO, appMan);
	}
	public ExecutionTimeLogger(String label, LogLevel logLevel, ApplicationManager appMan) {
		this.label = label;
		this.logLevel = logLevel;
		this.appMan = appMan;
		tstart = intermediateStart = appMan.getFrameworkTime();
	}

	public void intermediateStep(String intermediateLabel) {
		long now = appMan.getFrameworkTime();
		long duration = now-intermediateStart;
		log(duration, intermediateLabel);
		intermediateStart = now;
	}
	
	public void finish() {
		long duration = appMan.getFrameworkTime()-tstart;
		log(duration, "END");
	}
	
	private void log(long duration, String interLabel) {
		LoggerUtil.log(appMan.getLogger(), label+"."+interLabel+" took "+duration+" ms to execute.", logLevel, logAlwaysToConsole);		
	}
}
