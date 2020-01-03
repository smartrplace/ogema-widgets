/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.tools.statistics.evalprovider;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.resourcemanager.ResourceValueListener;

import de.iwes.tools.statistics.ComplexStatisticalAggregationProvider;
import de.iwes.tools.statistics.IntegratingStatProvider;
import de.iwes.tools.statistics.StatisticsHelper;
import de.iwes.tools.statistics.StatisticsHelper.AggregationResolution;

/**Provides evaluation of cost for kWh drawn from grid by a certain load profile. Assumptions are the same
 * as for {@link EnergyDrawnFromGridStatProvider}.
 * @author dnestle
 */
public class CostDrawnFromGridStatProvider extends IntegratingStatProvider {
	public final FloatResource loadValue;
	public final FloatResource gridImportValue;
	public final FloatResource gridPriceValue;
	
	/*public final FloatResource costValue;

	public final StatisticalAggregation costEvaluation;
	public StatisticalAggregationProvider costProvider;
	*/
	private final ResourceValueListener<FloatResource> inputListener = new ResourceValueListener<FloatResource>() {
		@Override
		public void resourceChanged(FloatResource resource) {
			float load = loadValue.getValue();
			if(load < 0) load = 0;
			float grid = gridImportValue.getValue();
			if(grid < 0) grid = 0;
			if(load < grid) {
				resultValue.setValue(load * gridPriceValue.getValue());
			} else {
				resultValue.setValue(grid * gridPriceValue.getValue());
			}
		}
	};
	/**
	 * 
	 * @param generatorPowerValue
	 * @param gridNetImportValue This is the net import value, so this may be zero or negative!
	 * @param gridPriceValue
	 * @param appMan
	 */
	public CostDrawnFromGridStatProvider(FloatResource loadValue, FloatResource gridImportValue,
			FloatResource gridPriceValue, ApplicationManager appMan, Resource sAggParent,
			AggregationResolution aggRes, final ComplexStatisticalAggregationProvider complexProvider) {
		
		this.loadValue = loadValue;
		this.gridImportValue = gridImportValue;
		this.gridPriceValue = gridPriceValue;
		List<FloatResource> fl = new ArrayList<FloatResource>();
		fl.add(loadValue);
		fl.add(gridImportValue);
		fl.add(gridPriceValue);
		init(fl, inputListener, appMan, sAggParent, "costFromGrid", aggRes, complexProvider);
	}
}
