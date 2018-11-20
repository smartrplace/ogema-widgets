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
package de.iwes.timeseries.eval.base.provider.utils;

import de.iwes.timeseries.eval.api.Status;

public class StatusImpl implements Status {
	
	public static final Status DONE = new StatusImpl(EvaluationStatus.FINISHED, null);
	public static final Status RUNNING = new StatusImpl(EvaluationStatus.RUNNING, null);
	public static final Status CANCELLED = new StatusImpl(EvaluationStatus.CANCELLED, null);	
	public static final Status RESTART_REQUESTED = new StatusImpl(EvaluationStatus.RESTART_REQUESTED, null);	
	public static final Status SKIP_EVALLEVEL = new StatusImpl(EvaluationStatus.SKIP_EVALLEVEL, null);	
	private final EvaluationStatus status;
	private final Exception cause;
	
	public StatusImpl(EvaluationStatus status, Exception cause) {
		this.status = status;
		this.cause = cause;
	}

	@Override
	public EvaluationStatus getStatus() {
		return status;
	}

	@Override
	public Exception getCause() {
		return cause;
	}

}
