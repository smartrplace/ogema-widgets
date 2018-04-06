package de.iwes.timeseries.eval.api.extended.util;

import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInstance.MultiEvaluationListener;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationProvider;
import de.iwes.timeseries.eval.api.extended.MultiResult;

/**
 * Base class for {@link MultiEvaluationProvider} implementations.
 */
public abstract class AbstractMultiEvaluationProvider<R, T extends MultiResult<R>>
		implements MultiEvaluationProvider<R, T>, MultiEvaluationListener<R, T> {
	
	private final ConcurrentMap<String, MultiEvaluationInstance<R, T>> offlineOngoing = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, MultiEvaluationInstance<R, T>> offlineFinished = new ConcurrentHashMap<>();
    
    public final int argNum;
    //This is the index of the first input with maximum tree size. This input is used to determine
    //the elements selected for evaluation from each dependency tree level.
    public final int maxTreeIndex;
    public final int maxTreeSize;
    
    /**Called by constructor before setting up data to allow for inherited classes to create inputDataTypes first
     * @param initData */
    protected abstract void preInit(Object initData);

	@Override
	public abstract List<DataProviderType> inputDataTypes();
	
    /**
     * @param id widget id
     * @param label widget label
     * @param description evaluation description.
     * @param clock (optional)
     */
    public AbstractMultiEvaluationProvider(Object initData) {
    	preInit(initData);
		this.argNum = inputDataTypes().size();
		int maxTreeSize = 0;
		int maxTreeIndex = -1;
        for(int i=0; i<argNum; i++) {
			if(inputDataTypes().get(i).selectionOptions().length > maxTreeSize) {
				maxTreeSize = inputDataTypes().get(i).selectionOptions().length;
				maxTreeIndex = i;
			}
		}
        this.maxTreeIndex = maxTreeIndex;
        this.maxTreeSize = maxTreeSize;
    }
    
    public void initData() {
    	
    }

	@SuppressWarnings("unchecked")
	@Override
	public final void evaluationDone(MultiEvaluationInstance<R, ?> evaluation, Status status) {
		offlineFinished.put(evaluation.id(), (MultiEvaluationInstance<R, T>) evaluation);
		offlineOngoing.remove(evaluation.id());
	}
		
	@Override
	public final List<MultiEvaluationInstance<R, T>> getOfflineEvaluations(boolean includeOngoing, boolean includeFinished) {
		final List<MultiEvaluationInstance<R, T>> list = new ArrayList<>();
		if (includeOngoing)
			list.addAll(offlineOngoing.values());
		if (includeFinished)
			list.addAll(offlineFinished.values());
		return list;
	}
	
	@Override
	public List<Configuration<?>> getConfigurations() {
        return Collections.emptyList();
	}
	
	@Override
	//public final EvaluationInstance newEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, 
	//		Collection<ConfigurationInstance> configurations) {
	public final MultiEvaluationInstance<R, T> newEvaluation(List<MultiEvaluationInputGeneric<R>> input, 
				Collection<ConfigurationInstance> configurations, TemporalUnit resultStepSize,
				List<ResultType> resultsRequested) {
		Objects.requireNonNull(input);
		if (input.isEmpty())
			throw new IllegalArgumentException("Input list and requested result types must not be empty");
		final MultiEvaluationInstance<R, T> instance = createEvaluation(input, configurations, resultStepSize,
				resultsRequested);
		offlineOngoing.put(instance.id(), instance);
		
		// TODO: start instance here or organize start
		/*if(startSeparateThread) Executors.newSingleThreadExecutor().submit(instance);
		else try {
			instance.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		return instance;
	}
    
    /**
     * Called by {@link #newEvaluation } to create the actual evaluation processor.
     * @param input passed through from {@link #newEvaluation } call
     * @param requestedResults passed through from {@link #newEvaluation } call
     * @param configurations passed through from {@link #newEvaluation } call
     * @param resultStepSize 
     * @return evaluation implementation.
     */
    abstract protected MultiEvaluationInstance<R, T> createEvaluation(List<MultiEvaluationInputGeneric<R>> input, 
			Collection<ConfigurationInstance> configurations, TemporalUnit resultStepSize,
			List<ResultType> resultsRequested);
	
	@Override
	public final boolean hasOngoingEvaluations() {
		return !offlineOngoing.isEmpty();
	}
	
	@Override
	public final List<String> getEvaluationIds() {
		final List<String> ids= new ArrayList<>();
		ids.addAll(offlineOngoing.keySet());
		ids.addAll(offlineFinished.keySet());
		return ids;
	}
	
	@Override
	public final MultiEvaluationInstance<R, T> getEvaluation(String id) {
		MultiEvaluationInstance<R, T> instance = offlineOngoing.get(id);
		if (instance != null)
			return instance;
		instance = offlineFinished.get(id);
		if (instance != null)
			return instance;
		return null;
	}
}
