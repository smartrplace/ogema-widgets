package de.iwes.timeseries.eval.garo.api.jaxb;

import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoSelectionItem;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiEvaluation;

@SuppressWarnings("unchecked")
@Deprecated //use GenericGaRoMultiEvaluation directly
public class GenericGaRoMultiEvaluationGen< P extends GaRoSingleEvalProvider> extends GenericGaRoMultiEvaluation<P> {
	
	public GenericGaRoMultiEvaluationGen(List<MultiEvaluationInputGeneric> input, Collection<ConfigurationInstance> configurations,
			GaRoEvalProvider<GaRoMultiResult> dataProviderAccess, TemporalUnit resultStepSize,
			GaRoDataType[] inputTypesFromRoom, GaRoDataType[] inputTypesFromGw,
			P singleProvider, boolean doBasicEval, List<ResultType> resultsRequested) {
		super(input, configurations, dataProviderAccess, resultStepSize, inputTypesFromRoom, inputTypesFromGw,
				singleProvider, doBasicEval, resultsRequested);
	}

	@Override
	protected List<GaRoSelectionItem> startRoomLevel(List<GaRoSelectionItem> levelItems, GaRoMultiResult result, String gw) {
		int[] roomTypeList = roomEval.getRoomTypes();
		if(roomTypeList == null) {
			/*GaRoSelectionItem toRemove = null;
			for(GaRoSelectionItem lvlIt: levelItems) {
				if(((GaRoSelectionItemJAXB)lvlIt).resource == null) {
					toRemove = lvlIt;
					break;
				}
				if(toRemove != null) levelItems.remove(toRemove);
			}*/
			return levelItems;
		}
		List<GaRoSelectionItem> retVal = new ArrayList<>();
		for(GaRoSelectionItem lvlIt: levelItems) {
			Integer type = ((GaRoSelectionItem)lvlIt).getRoomType();
			//Resource room = ((GaRoSelectionItemJAXB)lvlIt).getResource();
			if(lvlIt.id().equals(GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID)) { //(type == null) {
				if(ArrayUtils.contains(roomTypeList, -1))
					retVal.add(lvlIt);
				continue;
			}
			//Resource typeRes = room.get("type");
			//if(typeRes instanceof IntegerResource) {
			else {
				//int roomType = type;
				//int roomType = ((IntegerResource)typeRes).getValue();
				boolean found = false;
				for(int rt: roomTypeList) {
					if(type == null || type < 0) {
						if(rt < -1) { //-1 means overall-room
							found = true;
							break;
						}
					}
					else if(rt == type) {
						found = true;
						break;
					}
				}
				if(found) retVal.add(lvlIt);
			}		
		}
		return retVal;
		//result.dpNumGw = 0;
		//result.resultsGw = new HashMap<>();
	}

	/*@Override
	protected Integer getRoomType(Resource room) {
		return getRoomTypeStatic(room);
	}
	
	public static Integer getRoomTypeStatic(Resource room) {
		Resource typeRes = room.get("type");
		if(typeRes instanceof IntegerResource)
			return ((IntegerResource)typeRes).getValue();
		return null;		
	}

	@Override
	protected String getName(Resource room) {
		return room.getName();
	}

	@Override
	protected String getPath(Resource room) {
		return room.getPath();
	}*/
}
