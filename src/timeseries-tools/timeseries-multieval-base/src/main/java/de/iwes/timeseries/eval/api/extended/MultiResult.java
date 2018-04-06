package de.iwes.timeseries.eval.api.extended;

import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;

/** Interface defines basic result information that shall be returned by every {@link MultiEvaluationProvider}.
 * The actual result values shall be provided by the class implementing MultiResultType defined
 * by each {@link MultiEvaluationProvider}.<br>
 * Note that all public fields are exported JSON/CSV by default.
  */
public interface MultiResult<R> {
	public long getStartTime();
	public long getEndTime();
	
	/**Provide summary for printing into a log file or to console*/
	public String getSummary();

	/**Input to the evaluation that created result*/
	public Collection<ConfigurationInstance> getConfigurations();
	
	/**Input to the evaluation that created result*/
	public abstract List<MultiEvaluationInputGeneric<R>> getInputData();
	
}
