package de.iwes.timeseries.eval.garo.api.base;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationProvider;
import de.iwes.timeseries.eval.api.extended.util.GenericLinkingOptionType;
import de.iwes.widgets.html.selectiontree.LinkingOptionType;

/**
 * Calculate basic field test evaluations
 */
public abstract class GaRoEvalProvider<R, T extends GaRoMultiResult<R>> extends AbstractMultiEvaluationProvider<R, T> {
	protected final GaRoDataType[] inputTypesFromRoom;
	protected final GaRoDataType[] inputTypesFromGw;
	private List<DataProviderType> inputDataTypes;
	
	@Override
	public List<Configuration<?>> getConfigurations() {
		return Collections.emptyList();
	}

	//TODO: Use options from server-timeseries-source?
	public static LinkingOptionType gw = new GenericLinkingOptionType(GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID, "Select gateways", null);
	public static LinkingOptionType room = new GenericLinkingOptionType(GaRoMultiEvalDataProvider.ROOM_LINKINGOPTION_ID, "Select rooms", new LinkingOptionType[]{gw});
	
	/*private static class TempSensDataProvider implements DataProviderType {
		@Override
		public LinkingOptionType[] selectionOptions() {
			LinkingOptionType tempSens = new GenericLinkingOptionType(GaRoDataType.TemperatureMeasurement.name(), "Select temperature sensors",
					new LinkingOptionType[]{room});
			return new LinkingOptionType[]{gw, room, tempSens};
		}
		
	}
	private static class WindowSensDataProvider implements DataProviderType {
		@Override
		public LinkingOptionType[] selectionOptions() {
			LinkingOptionType winSens = new GenericLinkingOptionType(GaRoDataType.WindowOpen.name(), "Select window sensors",
					new LinkingOptionType[]{room});
			return new LinkingOptionType[]{gw, room, winSens};
		}
		
	}
	private static class ValveSensDataProvider implements DataProviderType {
		@Override
		public LinkingOptionType[] selectionOptions() {
			LinkingOptionType valveSens = new GenericLinkingOptionType(GaRoDataType.ValvePosition.name(), "Select valve position sensors",
					new LinkingOptionType[]{room});
			return new LinkingOptionType[]{gw, room, valveSens};
		}
		
	}*/
	
	public static class InitData {
		GaRoDataType[] inputTypesFromRoom;
		GaRoDataType[] inputTypesFromGw;
		public InitData(GaRoDataType[] inputTypesFromRoom, GaRoDataType[] inputTypesFromGw) {
			this.inputTypesFromRoom = inputTypesFromRoom;
			this.inputTypesFromGw = inputTypesFromGw;
		}
	}
	@Override
	protected void preInit(Object initDataRaw) {
		InitData in = (InitData)initDataRaw;
		int len = 0;
		if(in.inputTypesFromRoom != null) len += in.inputTypesFromRoom.length;
		if(in.inputTypesFromGw != null) len += in.inputTypesFromGw.length;
		DataProviderType[] arr = new DataProviderType[len];
		int idx = 0;
		
		if(in.inputTypesFromRoom != null) for(final GaRoDataType type: in.inputTypesFromRoom) {
			DataProviderType dataProvider = new DataProviderType() {
				@Override
				public LinkingOptionType[] selectionOptions() {
					LinkingOptionType lot = new GenericLinkingOptionType(type.name(), "Select "+type.name(),
							new LinkingOptionType[]{room});
					return new LinkingOptionType[]{gw, room, lot};
				}
				
			};
			arr[idx] = dataProvider;
			idx++;
		}
		if(in.inputTypesFromGw != null) for(final GaRoDataType type: in.inputTypesFromGw) {
			DataProviderType dataProvider = new DataProviderType() {
				@Override
				public LinkingOptionType[] selectionOptions() {
					LinkingOptionType lot = new GenericLinkingOptionType(type.name(), "Select "+type.name(),
							new LinkingOptionType[]{gw});
					return new LinkingOptionType[]{gw, lot};
				}
				
			};
			arr[idx] = dataProvider;
			idx++;
		}
		inputDataTypes = Collections.<DataProviderType> unmodifiableList(Arrays.asList(arr));
	}
	public GaRoEvalProvider(GaRoDataType[] inputTypesFromRoom, GaRoDataType[] inputTypesFromGw) {
		super(new InitData(inputTypesFromRoom, inputTypesFromGw));
		this.inputTypesFromGw = inputTypesFromGw;
		this.inputTypesFromRoom = inputTypesFromRoom;
	}	
	
	/*private static DataProviderType TempSensProvider = new TempSensDataProvider();
	private static DataProviderType WindowSensProvider = new WindowSensDataProvider();
	private static DataProviderType ValveSensProvider = new ValveSensDataProvider();
	private static List<DataProviderType> inputDataTypes = Collections.<DataProviderType> unmodifiableList(Arrays.asList(
			new DataProviderType[]{TempSensProvider, WindowSensProvider, ValveSensProvider}));
	*/
	@Override
	public List<DataProviderType> inputDataTypes() {
		return inputDataTypes;
	}
	
	public GaRoDataType[] getInputTypesFromRoom() {
		return inputTypesFromRoom;
	}

	public GaRoDataType[] getInputTypesFromGw() {
		return inputTypesFromGw;
	}
}
