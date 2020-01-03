/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.tools.statistics;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.resourcemanager.ResourceValueListener;

import de.iwes.tools.statistics.StatisticsHelper.AggregationResolution;

/**Just provides the integral of the input value. Compared to {@link StatisticalAggregationProvider} this provides some
 * support for resource creation and linking to a {@link ComplexStatisticalAggregationProvider}.
 * @author dnestle
 */
// XXX where is the integration done? This looks obscure.
public class IntegralStatProvider extends IntegratingStatProvider {
	public final FloatResource loadValue;

	private final ResourceValueListener<FloatResource> inputListener = new ResourceValueListener<FloatResource>() {
		@Override
		public void resourceChanged(FloatResource resource) {
			resultValue.setValue(resource.getValue());
		}
	};
	/**
	 * 
	 * @param generatorPowerValue
	 * @param gridNetImportValue This is the net import value, so this may be zero or negative!
	 * @param gridPriceValue
	 * @param appMan
	 */
	public IntegralStatProvider(FloatResource inputValue,
			ApplicationManager appMan, Resource sAggParent,
			AggregationResolution aggRes, final ComplexStatisticalAggregationProvider complexProvider) {
		
		List<FloatResource> fl = new ArrayList<FloatResource>();
		fl.add(inputValue);
		this.loadValue = inputValue;
		init(fl, inputListener, appMan, sAggParent, "generalIntegral", aggRes, complexProvider);
	}
}
