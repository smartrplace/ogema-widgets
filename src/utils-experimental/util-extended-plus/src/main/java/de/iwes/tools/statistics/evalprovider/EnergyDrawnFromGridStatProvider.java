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

/**Provides evaluation of kWh drawn from grid by a certain load profile. We assume here that if the load
 * draws a certain amount of power P_Load at a time and the at the respective grid connection point we
 * see a power P_Grid drawn the power to be attributed to the load is the minimum of the two considering
 * negative values as zero.
 * @author dnestle
 */
public class EnergyDrawnFromGridStatProvider extends IntegratingStatProvider {
	public final FloatResource loadValue;
	public final FloatResource gridImportValue;
	
	//public final FloatResource energyFromGridValue;

/*	public final StatisticalAggregation energyFromGridEvaluation;
	public StatisticalAggregationProvider energyFromGridProvider;
*/	
	private final ResourceValueListener<FloatResource> inputListener = new ResourceValueListener<FloatResource>() {
		@Override
		public void resourceChanged(FloatResource resource) {
			float load = loadValue.getValue();
			if(load < 0) load = 0;
			float grid = gridImportValue.getValue();
			if(grid < 0) grid = 0;
			if(load < grid) {
				resultValue.setValue(load);
			} else {
				resultValue.setValue(grid);
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
	public EnergyDrawnFromGridStatProvider(FloatResource loadValue, FloatResource gridImportValue,
			ApplicationManager appMan, Resource sAggParent,
			AggregationResolution aggRes, final ComplexStatisticalAggregationProvider complexProvider) {
		
		List<FloatResource> fl = new ArrayList<FloatResource>();
		fl.add(loadValue);
		fl.add(gridImportValue);
		this.loadValue = loadValue;
		this.gridImportValue = gridImportValue;
		init(fl, inputListener, appMan, sAggParent, "energyFromGrid", aggRes, complexProvider);
	}
}
