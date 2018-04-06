package de.iwes.timeseries.eval.garo.resource;

import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.locations.Room;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoSelectionItem;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiEvaluation;

@SuppressWarnings("unchecked")
public class GenericGaRoMultiEvaluationResource<P extends GaRoSingleEvalProvider> extends GenericGaRoMultiEvaluation<Resource, P> {
	
	public GenericGaRoMultiEvaluationResource(List<MultiEvaluationInputGeneric<Resource>> input, Collection<ConfigurationInstance> configurations,
			GaRoEvalProvider<Resource, GaRoMultiResult<Resource>> dataProviderAccess, TemporalUnit resultStepSize,
			GaRoDataType[] inputTypesFromRoom, GaRoDataType[] inputTypesFromGw,
			P singleProvider, boolean doBasicEval, List<ResultType> resultsRequested) {
		super(input, configurations, dataProviderAccess, resultStepSize, inputTypesFromRoom, inputTypesFromGw,
				singleProvider, doBasicEval, resultsRequested);
	}

	@Override
	protected List<GaRoSelectionItem<Resource>> startRoomLevel(List<GaRoSelectionItem<Resource>> levelItems, GaRoMultiResult<Resource> result, String gw) {
		int[] roomTypeList = roomEval.getRoomTypes();
		if(roomTypeList == null) return levelItems;
		List<GaRoSelectionItem<Resource>> retVal = new ArrayList<>();
		for(GaRoSelectionItem<Resource> lvlIt: levelItems) {
			Room room = (Room) lvlIt.getResource();
			IntegerResource typeRes = room.type();
			int roomType = typeRes.getValue();
			boolean found = false;
			for(int rt: roomTypeList) {
				if(rt == roomType) {
					found = true;
					break;
				}
			}
			if(found) retVal.add(lvlIt);
		}
		return retVal;
		//result.dpNumGw = 0;
		//result.resultsGw = new HashMap<>();
	}

	@Override
	protected Integer getRoomType(Resource room) {
		IntegerResource type = ((Room)room).type();
		if(type.isActive()) return type.getValue();
		return null;
	}

	@Override
	protected String getName(Resource room) {
		return ResourceUtils.getHumanReadableName(room);
	}

	@Override
	protected String getPath(Resource room) {
		return room.getLocation();
	}
}
