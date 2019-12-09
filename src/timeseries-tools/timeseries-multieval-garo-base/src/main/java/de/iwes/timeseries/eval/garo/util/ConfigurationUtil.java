package de.iwes.timeseries.eval.garo.util;

import org.ogema.core.model.Resource;

import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.GenericObjectConfiguration;
import de.iwes.timeseries.eval.base.provider.utils.ConfigurationBuilder;

public class ConfigurationUtil {
	/** Get configuration declaration for configuration resource to be returned in
	 * {@link EvaluationProvider#getConfigurations()}
	 * 
	 * @param id of provider
	 * @param anyResult any result type calculated by provider (does not have any effect)
	 * @param configType resource type that is used for configuration (provision
	 * 		of the resource for evaluation instances may be mandatory or optional)
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends Resource> Configuration<GenericObjectConfiguration<T>> getConfiguration(String id, ResultType anyResult,
			Class<? extends Resource> configType) {
		Configuration result2 =
			ConfigurationBuilder.newBuilder(ConfigurationInstance.GenericObjectConfiguration.class)
				.withId(id+"_cfg")
				.withLabel(configType.getSimpleName()+" Configuration Object")
				.withDescription(configType.getName()+" Configuration Object for provider "+id)
				.withResultTypes(anyResult)
				.withDefaultObject(configType)
				.isOptional(true)
				.build();
		Configuration<GenericObjectConfiguration<T>> result = result2;
		return result ;
	}

}
