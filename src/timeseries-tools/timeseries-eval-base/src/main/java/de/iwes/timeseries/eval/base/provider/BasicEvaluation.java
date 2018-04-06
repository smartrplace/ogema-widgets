package de.iwes.timeseries.eval.base.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationBaseImpl;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;

public class BasicEvaluation extends EvaluationBaseImpl {

    private final static AtomicLong idcounter = new AtomicLong(0); // TODO initialize from existing stored eval resources

    private final String id;
    // state variables
    private final float[] maxValues;
    private final float[] minValues;
    private final float[] integralValues;
    private final long[] integralSize;
    private final int[] counter;
    private final boolean[] isInGap;

    public BasicEvaluation(final List<EvaluationInput> input, final List<ResultType> requestedResults,
            Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time) {
        super(input, requestedResults, configurations, listener, time);
        this.id = "BasicEvaluation_" + idcounter.incrementAndGet();
        maxValues = new float[size];
        Arrays.fill(maxValues, Float.NaN);
        minValues = new float[size];
        Arrays.fill(minValues, Float.NaN);
        integralValues = new float[size];
        Arrays.fill(integralValues, Float.NaN);
        integralSize = new long[size];
        Arrays.fill(integralSize, -1);
        counter = new int[size];
        isInGap = new boolean[size];
        Arrays.fill(isInGap, true);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    protected Map<ResultType, EvaluationResult> getCurrentResults() {
        final boolean max = requestedResults.contains(BasicEvaluationProvider.MAX_TYPE);
        final boolean min = requestedResults.contains(BasicEvaluationProvider.MIN_TYPE);
        final boolean avg = requestedResults.contains(BasicEvaluationProvider.AVERAGE);
        final boolean itg = requestedResults.contains(BasicEvaluationProvider.INTEGRAL);
        final boolean ngt = requestedResults.contains(BasicEvaluationProvider.NON_GAPTIME);
        final boolean cnt = requestedResults.contains(BasicEvaluationProvider.COUNTER);
        final Map<ResultType, EvaluationResult> results = new LinkedHashMap<>();
        final List<SingleEvaluationResult> maxResults = max ? new ArrayList<SingleEvaluationResult>() : null;
        final List<SingleEvaluationResult> minResults = min ? new ArrayList<SingleEvaluationResult>() : null;
        final List<SingleEvaluationResult> integralResults = itg ? new ArrayList<SingleEvaluationResult>() : null;
        final List<SingleEvaluationResult> averageResults = avg ? new ArrayList<SingleEvaluationResult>() : null;
        final List<SingleEvaluationResult> nonGapResults = ngt ? new ArrayList<SingleEvaluationResult>() : null;
        final List<SingleEvaluationResult> counterResults = cnt ? new ArrayList<SingleEvaluationResult>() : null;
        synchronized (this) {
            for (int idx = 0; idx < size; idx++) {
                final TimeSeriesData t = input.get(idx);
                final List<TimeSeriesData> inputData = Collections.singletonList(t);

                if (max) {
                    float maxV = maxValues[idx];
                    if (!Float.isNaN(maxV)) {
                        final SingleEvaluationResult maxResult = new SingleValueResultImpl<>(BasicEvaluationProvider.MAX_TYPE, maxV, inputData);
                        maxResults.add(maxResult);
                    }
                }
                if (min) {
                    float minV = minValues[idx];
                    if (!Float.isNaN(minV)) {
                        final SingleEvaluationResult minResult = new SingleValueResultImpl<>(BasicEvaluationProvider.MIN_TYPE, minV, inputData);
                        minResults.add(minResult);
                    }
                }
                float itgV = integralValues[idx];
                if (itg && (!Float.isNaN(itgV))) {
                    final SingleEvaluationResult itgResult = new SingleValueResultImpl<>(BasicEvaluationProvider.INTEGRAL, itgV, inputData);
                    integralResults.add(itgResult);
                }

                Long sz = integralSize[idx];
                if (ngt && sz != -1) {
                    final SingleEvaluationResult ngtResult = new SingleValueResultImpl<>(BasicEvaluationProvider.NON_GAPTIME, sz, inputData);
                    nonGapResults.add(ngtResult);
                }
                if (avg && (!Float.isNaN(itgV))) {
                    if (sz != -1) {
                        final SingleEvaluationResult avgResult = new SingleValueResultImpl<>(BasicEvaluationProvider.AVERAGE, itgV / sz, inputData);
                        averageResults.add(avgResult);
                    }
                }
                if (cnt) {
                    int cntV = counter[idx];
                    final SingleEvaluationResult cntResult = new SingleValueResultImpl<>(BasicEvaluationProvider.COUNTER, cntV, inputData);
                    counterResults.add(cntResult);
                }
            }
        }
        if (max) {
            results.put(BasicEvaluationProvider.MAX_TYPE, new EvaluationResultImpl(maxResults, BasicEvaluationProvider.MAX_TYPE));
        }
        if (min) {
            results.put(BasicEvaluationProvider.MIN_TYPE, new EvaluationResultImpl(minResults, BasicEvaluationProvider.MIN_TYPE));
        }
        if (itg) {
            results.put(BasicEvaluationProvider.INTEGRAL, new EvaluationResultImpl(integralResults, BasicEvaluationProvider.INTEGRAL));
        }
        if (avg) {
            results.put(BasicEvaluationProvider.AVERAGE, new EvaluationResultImpl(averageResults, BasicEvaluationProvider.AVERAGE));
        }
        if (ngt) {
            results.put(BasicEvaluationProvider.NON_GAPTIME, new EvaluationResultImpl(nonGapResults, BasicEvaluationProvider.NON_GAPTIME));
        }
        if (cnt) {
            results.put(BasicEvaluationProvider.COUNTER, new EvaluationResultImpl(counterResults, BasicEvaluationProvider.COUNTER));
        }
        return Collections.unmodifiableMap(results);
    }

    @Override
    protected void stepInternal(SampledValueDataPoint dataPoint) throws Exception {
        for (Map.Entry<Integer, SampledValue> entry : dataPoint.getElements().entrySet()) {
            final int idx = entry.getKey();
            SampledValue sv = entry.getValue();
            final boolean quality = sv.getQuality() == Quality.GOOD;
            final float value = sv.getValue().getFloatValue();
            final long t = sv.getTimestamp();
            if (!quality) {
                isInGap[idx] = true;
                continue;
            }
            isInGap[idx] = false;
            final boolean isNaN = Float.isNaN(value);
            if (isNaN) {
                continue;
            }
            final float maxOld = maxValues[idx];
            if (Float.isNaN(maxOld) || value > maxOld) {
                maxValues[idx] = value;
            }
            final float minOld = minValues[idx];
            if (Float.isNaN(minOld) || value < minOld) {
                minValues[idx] = value;
            }
            counter[idx] = counter[idx] + 1;
            final SampledValue last = dataPoint.previous(idx);
            if (last == null) {
                continue;
            }
            final float itg = integrate(null, last, sv, null, modes[idx]);
            final float itgOld = integralValues[idx];
            final float newVal = !Float.isNaN(itgOld) ? itgOld + itg : itg;
            integralValues[idx] = newVal;
            final long diff = t - last.getTimestamp();
            final Long oldSize = integralSize[idx];
            final long newSize = oldSize != -1 ? oldSize + diff : diff;
            integralSize[idx] = newSize;
        }
    }

/*    static final float integrate(final SampledValue previous, final SampledValue start, final SampledValue end, final SampledValue next, final InterpolationMode mode) {
        if (start == null || end == null) {
            return Float.NaN;
        }
        final long startT = start.getTimestamp();
        final long endT = end.getTimestamp();
        if (startT == endT) {
            return 0;
        }
        if (endT < startT) {
            throw new IllegalArgumentException("Interval boundaries interchanged");
        }
        final float p;
        final float n;
        switch (mode) {
            case STEPS:
                return start.getValue().getFloatValue() * (endT - startT);
            case LINEAR:
                p = start.getValue().getFloatValue();
                n = end.getValue().getFloatValue();
                return (p + (n - p) / 2) * (endT - startT);
            case NEAREST:
                p = start.getValue().getFloatValue();
                n = end.getValue().getFloatValue();
                Objects.requireNonNull(previous);
                Objects.requireNonNull(next);
                final long boundary = (next.getTimestamp() + previous.getTimestamp()) / 2;
                if (boundary <= startT) {
                    return n * (endT - startT);
                }
                if (boundary >= endT) {
                    return p * (endT - startT);
                }
                return p * (boundary - startT) + n * (endT - boundary);
            default:
                return Float.NaN;
        }
    }
*/

}
