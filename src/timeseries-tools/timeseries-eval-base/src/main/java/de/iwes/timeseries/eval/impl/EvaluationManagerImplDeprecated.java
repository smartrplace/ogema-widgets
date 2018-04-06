package de.iwes.timeseries.eval.impl;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesIterator;
import org.osgi.framework.Constants;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.EvaluationManager;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.configuration.TimestepConfiguration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.GenericDurationConfiguration;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationUtils;

/**
 * Uses old school blocking iterators for online evaluation
 */
@Service(EvaluationManager.class)
@Component
@Property(name=Constants.SERVICE_RANKING,value=""+Integer.MIN_VALUE)
public class EvaluationManagerImplDeprecated implements EvaluationManager {
	
	@Override
	public EvaluationInstance newEvaluation(EvaluationProvider provider, List<EvaluationInput> input,
			List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations) {
		final EvaluationInstance instance = provider.newEvaluation(input, requestedResults, configurations);
		Long timestep = null;
		if (configurations != null) {
			timestep = configurations.stream()
				.filter(cfg -> cfg.getConfigurationType() == TimestepConfiguration.INSTANCE)
				.map(cfg -> {
					GenericDurationConfiguration durationCfg = (GenericDurationConfiguration) cfg;
					final long d = durationCfg.getDuration();
					final String unit0 = durationCfg.getUnit();
					TemporalUnit unit = ChronoUnit.MILLIS;
					try {
						unit = ChronoUnit.valueOf(unit0.toUpperCase());
					} catch (Exception e) {}
					final long totalDuration = unit.getDuration().toMillis() * d;
					return totalDuration;
				}).findAny().orElse(null);
		} else {
			timestep = provider.requestedUpdateInterval();
		}
		
		final Evaluation eval = new Evaluation(EvaluationUtils.getMultiTimeSeriesIterator(input,
				requestedResults, configurations, instance, timestep), instance);
		instance.addListener(eval);
		Executors.newSingleThreadExecutor().submit(eval);
		return instance;
	}

	private static class Evaluation implements Callable<Status>, EvaluationListener {
		
		private final MultiTimeSeriesIterator iterator;
		private final EvaluationInstance instance;
		
		public Evaluation(MultiTimeSeriesIterator iterator, EvaluationInstance isntance) {
			this.iterator = iterator;
			this.instance = isntance;
		}
		
		@Override
		public Status call() throws Exception {
			while (!instance.isDone() && iterator.hasNext()) {
				try {
					instance.step(iterator.next());
				} catch (IllegalArgumentException e) {
					break; // finished already
				} catch (Exception e) {
					break;
				}
			}
			return instance.finish();
		}
		
		@Override
		public void evaluationDone(EvaluationInstance evaluation, Status status) {
			EvaluationUtils.interruptOnlineMultiIterator(iterator);
		}
		
	}
	
	public EvaluationInstance newEvaluationSelfOrganized(EvaluationProvider provider, List<EvaluationInput> input,
			List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations) {
		final EvaluationInstance instance = provider.newEvaluation(input, requestedResults, configurations);
		return instance;
	}

}
