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
package de.iwes.timeseries.eval.garo.api.base;

import org.ogema.generictype.GenericDataTypeDeclaration;

import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.AggregationMode;

/** Supported data types
 * The respective Strings used for identifiction are defined in {@link GaRoEvalHelper#getDataType(String)}.
 * If a data type is used in an input definition also an entry in {@link GaRoEvalHelper#getRequiredInput(GaRoDataTypeI)}
 * has to exist.<br>
 * Note that currently these files have to be extended when additional data input types shall
 * be defined and provided. An extended future approach could foresee that ResultTypes and GaRo input types declare an
 * OGEMA resource path indicating where a single value of a result would be placed in the OGEMA resource
 * model. This could be used to identify input for evaluations from DataProviders as well as from
 * Pre-Evaluation.
 */
public interface GaRoDataTypeI extends GenericDataTypeDeclaration {
	public static enum AggregationModePlus {
		/** power / volume flow*/
		FLOW,
		/** integrated flow like meter reading */
		INTEGRATED,
		/** integrated flow per time step, time step may be variable */
		CONSUMPTION,
		/** like "integrated", but reading counter is reset to zero each beginning of day*/
		CONSUMPTION_PER_DAY,
		/** like "integrated", but reading counter is reset to zero each beginning of month*/
		CONSUMPTION_PER_MONTH,
		/** like "integrated", but reading counter is reset to zero each beginning of year*/
		CONSUMPTION_PER_YEAR,
		/** like "integrated", but reset to zero may occur occasionally, usually when meter is
		 * powered off - this may be the case for simple submeters without persistent storage 
		 */
		INTEGRATED_WITH_RESET,
		/** Average power or other value. The average shall apply to the entire time series until the
		 * next value. The last two values of the time series shall be equal and indicate the duration of
		 * the last interval. This can be applied to almost all evaluated measurement values and other
		 * evaluation results.
		 */
		AVERAGE_VALUE_PER_STEP
	}

	public enum Level {ROOM, GATEWAY, OVERALL};
	public Level getLevel();
	
	/**Null for standard types, otherwise the id of the {@link GaRoSingleEvalProvider} is given here
	 */
	public default String primaryEvalProvider() { return null; }
	
	public default AggregationModePlus aggregationMode() {
		return AggregationModePlus.AVERAGE_VALUE_PER_STEP;
	}
}
