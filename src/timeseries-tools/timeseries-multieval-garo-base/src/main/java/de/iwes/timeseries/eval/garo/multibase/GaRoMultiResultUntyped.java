package de.iwes.timeseries.eval.garo.multibase;

import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;

@SuppressWarnings("rawtypes")
public class GaRoMultiResultUntyped extends GaRoMultiResult {

	@SuppressWarnings("unchecked")
	public GaRoMultiResultUntyped(List inputData, long start, long end, Collection configurations) {
		super(inputData, start, end, configurations);
	}
}
