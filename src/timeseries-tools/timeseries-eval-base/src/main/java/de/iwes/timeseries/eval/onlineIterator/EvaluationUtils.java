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
package de.iwes.timeseries.eval.onlineIterator;

import java.util.Collection;
import java.util.List;
import org.ogema.core.application.ApplicationManager;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationInstance.ResultListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.onlineIterator.OnlineNonBlockingIterator;

public class EvaluationUtils {
	
	public static OnlineNonBlockingIterator startEvaluationOnline(EvaluationProvider evalProvider,
			List<EvaluationInput> allItems, List<ResultType> requestedResults, 
			Collection<ConfigurationInstance> configurations, ResultListener intermediateListener,
			ApplicationManager appMan) {
		EvaluationInstance instance = evalProvider.newEvaluation(allItems, requestedResults, configurations);
		if(intermediateListener != null) instance.addIntermediateResultListener(intermediateListener);
		
		OnlineNonBlockingIterator onlineNBI = new OnlineNonBlockingIterator(instance, allItems, appMan);
		
		return onlineNBI;
	}

}
