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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.ogema.core.administration.FrameworkClock;
import org.ogema.core.model.simple.SingleValueResource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.OnlineEvaluation;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.AbstractEvaluationProvider;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Calculate average, std dev. etc.
 */
@Component(service=EvaluationProvider.class)
public class GapEvaluationProvider extends AbstractEvaluationProvider {

    @Reference
    protected FrameworkClock clock;
    public final static String ID = "gap_evaluation_provider";
    
    public GapEvaluationProvider() {
        super(null, ID, "Gap evaluation provider",
                "Finds gaps in the measurement data.");
    }

    @Override
    protected OnlineEvaluation createEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations) {
        long time = clock != null ? clock.getExecutionTime() : System.currentTimeMillis();
        return new GapEvaluation(input, requestedResults, configurations, this, time);
    }

    @Override
    public List<RequiredInputData> inputDataTypes() {
        return Collections.singletonList(INPUT);
    }

    @Override
    public List<Configuration<?>> getConfigurations() {
        List<Configuration<?>> rval = new ArrayList<>();
        rval.add(GapEvaluator.GAP_THRESHOLD_CONFIGURATION);
        return rval;
    }
    
    private static final List<ResultType> TYPES = Collections.unmodifiableList(
            Arrays.asList(GapEvaluator.GAPCOUNT, GapEvaluator.GAPTOTALLENGTH, GapEvaluator.GAPGRAPH, GapEvaluator.MAXCONTINUOUS));

    @Override
    public List<ResultType> resultTypes() {
        return TYPES;
    }

    private static final RequiredInputData INPUT = new RequiredInputData() {

        @Override
        public String id() {
            return ID + "_input";
        }

        @Override
        public String label(OgemaLocale locale) {
            return "Generic time series";
        }

        @Override
        public String description(OgemaLocale locale) {
            return "Requires one or multiple numerical time series as input";
        }

        @Override
        public Class<? extends SingleValueResource> requestedInputType() {
            return SingleValueResource.class;
        }

        @Override
        public int cardinalityMin() {
            return 1;
        }

        @Override
        public int cardinalityMax() {
            return Integer.MAX_VALUE;
        }

    };

    /*public final static ResultType GAPS = ResultTypeBuilder.newBuilder()
            .withId("gap")
            .withLabel("Gaps")
            .withDescription("Detects gaps in the measurement time series.")
            .withStructure(ResultType.ResultStructure.PER_INPUT)
            .withCardinality(ResultType.Cardinality.SINGLE_VALUE)
            .withType(ResultType.ValueType.TIME_SERIES).build();
*/
}
