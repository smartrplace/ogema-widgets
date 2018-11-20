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
package de.iwes.timeseries.eval.base.provider.gap;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

/**
 *
 * @author jlapp
 */
public abstract class AbstractEvaluator {
    
    protected final List<EvaluationInput> input;
    protected final List<ResultType> requestedResults;
    protected final Collection<ConfigurationInstance> configurations;
    
    public AbstractEvaluator(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations) {
        this.input = input;
        this.requestedResults = requestedResults;
        this.configurations = configurations;
    }
    
    public abstract Map<ResultType, EvaluationResult> currentResults(Collection<ResultType> requestedResults);
    
    public abstract void step(SampledValueDataPoint data);
    
}
