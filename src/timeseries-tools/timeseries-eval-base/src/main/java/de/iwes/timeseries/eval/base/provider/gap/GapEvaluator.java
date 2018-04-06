package de.iwes.timeseries.eval.base.provider.gap;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.ConfigurationBuilder;
import de.iwes.timeseries.eval.base.provider.utils.ResultTypeBuilder;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ogema.core.channelmanager.measurements.BooleanValue;
import org.ogema.core.channelmanager.measurements.LongValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.api.MemoryTimeSeries;
import org.ogema.tools.timeseries.implementations.TreeTimeSeries;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

/**
 *
 * @author jlapp
 */
public class GapEvaluator extends AbstractEvaluator {

    public static final long DEFAULT_GAP_THRESHOLD = 2000;
    final long gapThreshold;

    private final List<SingleInputGapData> inputStates;

    static class SingleInputGapData {

        List<SampledValue> gaps = new ArrayList<>();
        // continuous sample runs.
        List<SampledValue> runs = new ArrayList<>();
        long currentRunStart = -1;
        SampledValue last;
        long gapCount = 0;
        long totalGapSize = 0;
    }

    public GapEvaluator(List<EvaluationInput> input, List<ResultType> requestedResults,
            Collection<ConfigurationInstance> configurations) {
        super(input, requestedResults, configurations);
        long gapCfg = DEFAULT_GAP_THRESHOLD;
        for (ConfigurationInstance cfg : configurations) {
            if (cfg.getConfigurationType().equals(GAP_THRESHOLD_CONFIGURATION)) {
                gapCfg = (long) ((ConfigurationInstance.GenericFloatConfiguration) cfg).getValue();
            }
        }
        this.gapThreshold = gapCfg;
        inputStates = new ArrayList<>(input.size());
        for (int i = 0; i < input.size(); i++) {
            inputStates.add(new SingleInputGapData());
        }
    }

    @Override
    public synchronized Map<ResultType, EvaluationResult> currentResults(Collection<ResultType> requestedResults) {
        Map<ResultType, EvaluationResult> rval = new LinkedHashMap<>();
        for (ResultType t: requestedResults) {
            if (GAPCOUNT.equals(t)) {
                List<SingleEvaluationResult> l = new ArrayList<>();
                int idx = 0;
                for (EvaluationInput i: input) {
                    l.add(new SingleValueResultImpl<>(GAPCOUNT, inputStates.get(idx++).gapCount, i.getInputData()));
                }
                rval.put(t, new EvaluationResultImpl(l, t));
            } else if (GAPTOTALLENGTH.equals(t)) {
                List<SingleEvaluationResult> l = new ArrayList<>();
                int idx = 0;
                for (EvaluationInput i: input) {
                    l.add(new SingleValueResultImpl<>(GAPTOTALLENGTH, inputStates.get(idx++).totalGapSize, i.getInputData()));
                }
                rval.put(t, new EvaluationResultImpl(l, t));
            } else if (GAPGRAPH.equals(t)) {
                List<SingleEvaluationResult> l = new ArrayList<>();
                int idx = 0;
                for (EvaluationInput i: input) {
                    l.add(getGraphResult(inputStates.get(idx), i));
                }
                rval.put(t, new EvaluationResultImpl(l, t));
            } else if (MAXCONTINUOUS.equals(t)) {
                List<SingleEvaluationResult> l = new ArrayList<>();
                int idx = 0;
                for (EvaluationInput i: input) {
                    long max = 0;
                    for (SampledValue sv: inputStates.get(idx).runs) {
                        long runLength = sv.getValue().getLongValue();
                        if (runLength > max) {
                            max = runLength;
                        }
                    }
                    l.add(new SingleValueResultImpl<>(MAXCONTINUOUS, max, i.getInputData()));
                    idx++;
                }
                rval.put(t, new EvaluationResultImpl(l, t));
            }
        }
        return rval;
    }
    
    private SingleEvaluationResult getGraphResult(final SingleInputGapData data, final EvaluationInput input) {
        final List<SampledValue> gaps = new ArrayList<>(data.gaps);
        SingleEvaluationResult result = new SingleEvaluationResult.TimeSeriesResult() {
            
            MemoryTimeSeries gapGraph = null;
            
            @Override
            public synchronized ReadOnlyTimeSeries getValue() {
                if (gapGraph == null) {
                    gapGraph = new TreeTimeSeries(BooleanValue.class);
                    gapGraph.setInterpolationMode(InterpolationMode.STEPS);
                    for (SampledValue sv: gaps) {
                        gapGraph.addValue(new SampledValue(new BooleanValue(true), sv.getTimestamp(), Quality.GOOD));
                        gapGraph.addValue(new SampledValue(new BooleanValue(false), sv.getTimestamp()+sv.getValue().getLongValue(), Quality.GOOD));
                    }
                }
                return gapGraph;
            }

            @Override
            public ResultType getResultType() {
                return GAPGRAPH;
            }

            @Override
            public List<TimeSeriesData> getInputData() {
                return input.getInputData();
            }

        };
        return result;
    }

    @Override
    public void step(SampledValueDataPoint data) {
        for (Map.Entry<Integer, SampledValue> inputValue : data.getElements().entrySet()) {
            SingleInputGapData inputState = inputStates.get(inputValue.getKey());
            SampledValue v = inputValue.getValue();

            if (inputState.currentRunStart == -1) {
                inputState.currentRunStart = v.getTimestamp();
            }
            if (inputState.last != null) {
                long interval = v.getTimestamp() - inputState.last.getTimestamp();
                if (interval > gapThreshold) {
                    inputState.gaps.add(new SampledValue(new LongValue(interval), inputState.last.getTimestamp(), Quality.GOOD));
                    inputState.gapCount++;
                    inputState.totalGapSize += interval;
                    
                    long runLength = inputState.last.getTimestamp() - inputState.currentRunStart;
                    if (runLength > 0) {
                        SampledValue run = new SampledValue(new LongValue(runLength), inputState.currentRunStart, Quality.GOOD);
                        inputState.runs.add(run);
                    }
                    inputState.currentRunStart = -1;
                }
            }
            inputState.last = v;
        }
    }

    //TODO: change to GenericDurationConfiguration when the front end supports it.
    public static final Configuration<ConfigurationInstance.GenericFloatConfiguration>
            GAP_THRESHOLD_CONFIGURATION = //new GapConfiguration();
            ConfigurationBuilder.newBuilder(ConfigurationInstance.GenericFloatConfiguration.class)
                    .withId("gap_threshold_cfg")
                    .withLabel("Gap Threshold")
                    .withDescription("Maximum acceptable interval between 2 samples, bigger intervals will be counted as gap.")
                    .withDefaultFloat(DEFAULT_GAP_THRESHOLD)
                    .withResultTypes(GapEvaluator.GAPGRAPH, GapEvaluator.GAPTOTALLENGTH, GapEvaluator.GAPCOUNT)
                    .isOptional(false)
                    .build();

    public final static ResultType GAPGRAPH = ResultTypeBuilder.newBuilder()
            .withId("gap_graph")
            .withLabel("Gap Graph")
            .withDescription("Shows gaps in the measurement time series as graph.")
            .withStructure(ResultType.ResultStructure.PER_INPUT)
            .withType(ResultType.ValueType.TIME_SERIES)
            .withSingleValueOrArray(true).build();

    public final static ResultType GAPCOUNT = ResultTypeBuilder.newBuilder()
            .withId("gap_count")
            .withLabel("Gap Count")
            .withDescription("Number of gaps in input data.")
            .withStructure(ResultType.ResultStructure.PER_INPUT)
            .withType(ResultType.ValueType.NUMERIC)
            .withSingleValueOrArray(true).build();

    public final static ResultType GAPTOTALLENGTH = ResultTypeBuilder.newBuilder()
            .withId("gap_total")
            .withLabel("Gap Total Length")
            .withDescription("Total time without input.")
            .withStructure(ResultType.ResultStructure.PER_INPUT)
            .withType(ResultType.ValueType.NUMERIC)
            .withSingleValueOrArray(true).build();
    
    public final static ResultType MAXCONTINUOUS = ResultTypeBuilder.newBuilder()
            .withId("continuous_max")
            .withLabel("Longest Continuous Run")
            .withDescription("Longest time with continuous sensor data.")
            .withStructure(ResultType.ResultStructure.PER_INPUT)
            .withType(ResultType.ValueType.NUMERIC)
            .withSingleValueOrArray(true).build();

}
