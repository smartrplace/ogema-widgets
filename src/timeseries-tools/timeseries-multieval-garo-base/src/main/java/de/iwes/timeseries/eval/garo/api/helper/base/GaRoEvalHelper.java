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
package de.iwes.timeseries.eval.garo.api.helper.base;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.widgets.configuration.service.OGEMAConfigurations;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.util.MultiEvaluationUtils;
import de.iwes.timeseries.eval.api.extended.util.TimeSeriesDataExtendedImpl;
import de.iwes.timeseries.eval.base.provider.utils.RequiredInputDefault;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvaluationInput;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.RoomData;
import de.iwes.timeseries.eval.garo.api.base.GaRoTimeSeriesId;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiEvaluation;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiProvider;
import de.iwes.timeseries.eval.garo.util.MultiEvaluationUtilsGaRo;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class GaRoEvalHelper {
	public static interface TypeChecker {
		boolean selectTimeSeries(String id);
	}
	public static class RecIdVal {
		public GaRoDataType type;
		public List<String> snippets;
		/** A type may specify snippets and/or a type checker. The type checker shall return true
		 * on selectTimeSeries if the String matches the type}
		 */
		public TypeChecker typeChecker;
		public Map<OgemaLocale, String> label;
		
		public RecIdVal(GaRoDataType type, String[] snippets, Map<OgemaLocale, String> label) {
			this.type = type;
			this.snippets = Arrays.asList(snippets);
			this.label = label;
		}		
		public RecIdVal(GaRoDataType type, List<String> snippets, Map<OgemaLocale, String> label) {
			this.type = type;
			this.snippets = snippets;
			this.label = label;
		}		
		public RecIdVal(GaRoDataType type, TypeChecker typeChecker, Map<OgemaLocale, String> label) {
			this.type = type;
			this.snippets = null;
			this.typeChecker = typeChecker;
			this.label = label;
		}		
	}
	/** <time series name> -> type/snippet info
	 */
	public static final Map<String, RecIdVal> recIdSnippets = new LinkedHashMap<>();
	static {
		addRecId(GaRoDataType.OutsideTemperaturePerForcecast, new String[] {"OpenWeatherMapData/temperatureSensor/reading/forecast"}, recIdSnippets,
				"Outside temperature(FC)", "Außentemperature(vorhergesagt)");
		addRecId(GaRoDataType.OutsideHumidityPerForcecast, new String[] {"OpenWeatherMapData/humiditySensor/reading/forecast"}, recIdSnippets,
				"Humidity(FC)", "Luftfeuchtigkeit(vorhergesagt)");
		addRecId(GaRoDataType.SolarIrradiationPerForcecast, new String[] {"OpenWeatherMapData/solarIrradiationSensor/reading/forecast"}, recIdSnippets,
				"Solar Irraditaion(FC)", "Einstrahlung(vorhergesagt)");
		addRecId(GaRoDataType.WindSpeedPerForcecast, new String[] {"OpenWeatherMapData/windSensor/speed/reading/forecast"}, recIdSnippets,
				"Wind Speed(FC)", "Windgeschwindigkeit(vorhergesagt)");
		addRecId(GaRoDataType.WindDirectionPerForcecast, new String[] {"OpenWeatherMapData/windSensor/direction/reading/forecast"}, recIdSnippets,
				"Wind Direction(FC)", "Windrichtung(vorhergesagt)");
		
		addRecId(GaRoDataType.OutsideTemperatureExt, new String[] {"BigBlueRoom/sensors/temperature", "WeatherData/sensors/temperature",
				"OpenWeatherMapData/temperatureSensor"}, recIdSnippets,
				"Outside temperature(Ext)", "Außentemperature(ext)");
		addRecId(GaRoDataType.OutsideHumidityExt, new String[] {"BigBlueRoom/sensors/humidity", "WeatherData/sensors/humidity",
				"OpenWeatherMapData/humiditySensor"}, recIdSnippets,
				"Humidity(Ext)", "Luftfeuchtigkeit(ext)");
		addRecId(GaRoDataType.SolarIrradiationExt, new String[] {"BigBlueRoom/sensors/solarIrradiation",
				"WeatherData/sensors/solarIrradiation", "OpenWeatherMapData/solarIrradiationSensor"}, recIdSnippets,
				"Solar Irraditaion(Ext)", "Einstrahlung(ext)");
		addRecId(GaRoDataType.WindSpeedExt, new String[] {"BigBlueRoom/sensors/wind/speed",
				"WeatherData/sensors/wind/speed", "OpenWeatherMapData/windSensor/speed"}, recIdSnippets,
				"Wind Speed(Ext)", "Windgeschwindigkeit(ext)");
		addRecId(GaRoDataType.WindDirectionExt, new String[] {"BigBlueRoom/sensors/wind/direction",
				"WeatherData/sensors/wind/direction", "OpenWeatherMapData/windSensor/direction"}, recIdSnippets,
				"Wind Direction(Ext)", "Windrichtung(ext)");

		addRecId(GaRoDataType.StateOfCharge, new String[] {"/chargeSensor/reading"}, recIdSnippets,
				"SOC", "SOC");
		addRecId(GaRoDataType.ChargeVoltage, new String[] {"chargeSensor", "internalVoltage"}, recIdSnippets,
				"Battery Voltage", "Batteriespannung");
		addRecId(GaRoDataType.ChargeSensor, new String[] {"batteryLow", "battery_low"}, recIdSnippets,
				"Battery Status", "Batterie Status");
		addRecId(GaRoDataType.HumidityMeasurement, new String[] {"HUMIDITY", "/humidity"}, recIdSnippets,
				"Humidity", "Luftfeuchtigkeit");
		addRecId(GaRoDataType.WindowOpen, new String[] {"SHUTTER_CONTACT"}, recIdSnippets,
				"Window Open Status", "Fensteröffnung");
		addRecId(GaRoDataType.TemperatureSetpointFeedback, new String[] {"temperatureSensor/deviceFeedback/setpoint",
				"/temperatureSensor/deviceSettings/setpoint"}, recIdSnippets,
				"Temperature Setpoint Thermostat", "Temperatursollwert real");
		addRecId(GaRoDataType.TemperatureSetpointSet, new String[] {"temperatureSensor/settings/setpoint"}, recIdSnippets,
				"Temperature Setpoint Requested", "Temperatursollwert angefordert");
		addRecId(GaRoDataType.TemperatureMeasurementThermostat, new String[] {"temperatureSensor/reading"}, recIdSnippets,
				"Temperature measured at thermostat", "Temperaturmesswert Thermostat");
		addRecId(GaRoDataType.TemperatureMeasurementRoomSensor, new String[] {"TEMPERATURE/reading", "EXTERNAL_TEMPERATURE_0_0", "/sensors/temperature"}, recIdSnippets,
				"Room Sensor Temperature", "Raumsensor Temperatur");
		addRecId(GaRoDataType.OutsideTemperatureGw, new String[] {"/sensors/TEMPERATURE"}, recIdSnippets,
				"Outside Sensor Temperature", "Außensensor Temperatur");
		addRecId(GaRoDataType.ValvePosition, new String[] {"valve/setting/stateFeedback"}, recIdSnippets,
				"Valve Position", "Ventilstellung");
		addRecId(GaRoDataType.ValvePositionLimit, new String[] {"/maximumValvePosition"}, recIdSnippets,
				"MaxValveLimit", "Maximale Ventilstellung");
		addRecId(GaRoDataType.InternetConnection, new String[] {"NetworkState/mainNetworkOk"}, recIdSnippets,
				"Main Internet connection status", "DSL Status");
		addRecId(GaRoDataType.RSSIDevice, new String[] {"/rssiDevice", "/communicationStatus/RSSI", "/signal/reading"}, recIdSnippets,
				"RSSI Device");
		addRecId(GaRoDataType.RSSIPeer, new String[] {"/rssiPeer"}, recIdSnippets,
				"RSSI Peer");
		addRecId(GaRoDataType.RSSISignal, new String[] {"/communicationStatus/Signal"}, recIdSnippets,
				"RSSI Signal");
		addRecId(GaRoDataType.CommunicationQuality, new String[] {"/communicationStatus/quality"}, recIdSnippets,
				"RSSI Quality");
		addRecId(GaRoDataType.FreshWaterFlow, new String[] {"/VOLUME_FLOW_FRESHWATER"}, recIdSnippets,
				"Fresh Waster Flow");
		addLabel(GaRoDataType.PowerMeterEnergySubphase, "Phase Energy", "Energie pro Phase");
		addLabel(GaRoDataType.PowerMeterSubphase, "Phase Power", "Phasenleistung");
		addLabel(GaRoDataType.PowerMeterOutlet, "Plug Power", "Stecker Leistung");
		addLabel(GaRoDataType.EnergyIntegralOutlet, "Plug Energy", "Stecker Energie");
	}
	public static void addRecId(GaRoDataType type, String[] snippets, Map<String, RecIdVal> recIdSnippets) {
		Map<OgemaLocale, String> label = new HashMap<OgemaLocale, String>();
		label.put(OgemaLocale.ENGLISH, type.label(null));
		addRecId(type.label(null), type, snippets, recIdSnippets, label);		
	}
	public static void addRecId(GaRoDataType type, String[] snippets, Map<String, RecIdVal> recIdSnippets,
			String labelEnglish) {
		Map<OgemaLocale, String> label = new HashMap<OgemaLocale, String>();
		label.put(OgemaLocale.ENGLISH, labelEnglish);
		addRecId(type.label(null), type, snippets, recIdSnippets, label);
	}
	public static void addRecId(GaRoDataType type, String[] snippets, Map<String, RecIdVal> recIdSnippets,
			String labelEnglish, String labelGerman) {
		Map<OgemaLocale, String> label = new HashMap<OgemaLocale, String>();
		label.put(OgemaLocale.ENGLISH, labelEnglish);
		label.put(OgemaLocale.GERMAN, labelGerman);
		addRecId(type.label(null), type, snippets, recIdSnippets, label);
	}
	public static void addLabel(GaRoDataType type, String labelEnglish, String labelGerman) {
		Map<OgemaLocale, String> label = new HashMap<OgemaLocale, String>();
		label.put(OgemaLocale.ENGLISH, labelEnglish);
		label.put(OgemaLocale.GERMAN, labelGerman);
		addRecId(type.label(null), type, new String[] {}, recIdSnippets, label);
	}
	public static void addRecId(String id, GaRoDataType type, String[] snippets, Map<String, RecIdVal> recIdSnippets,
			Map<OgemaLocale, String> label) {
		recIdSnippets.put(id, new RecIdVal(type, snippets, label));		
	}
	/** <time series name> -> recIdSnippets
	 * standard user plot configurations*/
	//public static final Map<String, List<Map<GaRoDataType, List<String>>>> userPlotOptions = new LinkedHashMap<>();
	//static {
	//	List<Map<GaRoDataType, List<String>>> helpList = new ArrayList<>();
	//	helpList.add(recIdSnippets);
	//	userPlotOptions.put("defaultInternetConnectionState", helpList);
	//}
	
	
	@SuppressWarnings("unchecked")
	public static GaRoDataType getDataType(String recId) {
		Object recSnippetsTouse = OGEMAConfigurations.getObject(GaRoDataType.class.getName(), "%recSnippets");
		if(recSnippetsTouse != null && recSnippetsTouse instanceof Map) {
			for(Entry<String, RecIdVal> e: ((Map<String, RecIdVal>)recSnippetsTouse).entrySet()) {
				if(e.getValue().typeChecker != null) {
					if(e.getValue().typeChecker.selectTimeSeries(recId))
						return e.getValue().type;					
				}
				if(e.getValue().snippets != null) for(String snpippet: e.getValue().snippets) {
					if(recId.contains(snpippet))
						return e.getValue().type;
				}
			}
		}

		//if(recId.contains("chargeSensor")) return GaRoDataType.ChargeSensor;
		//if(recId.contains("internalVoltage")) return GaRoDataType.ChargeVoltage;
		//if(recId.contains("HUMIDITY")) return GaRoDataType.HumidityMeasurement;
		//if(recId.contains("SHUTTER_CONTACT")) return GaRoDataType.WindowOpen;
		if(recId.contains("MOTION_DETECTOR")) return GaRoDataType.MotionDetection;
		if(recId.contains("/motionSensor/reading")) return GaRoDataType.MotionDetection;
		if(recId.contains("/sensors/motion")) return GaRoDataType.MotionDetection;
		//type TemperatureSetpoint is not recognized anymore
		//if(recId.contains("temperatureSensor/deviceFeedback/setpoint")) return GaRoDataType.TemperatureSetpointFeedback;
		//if(recId.contains("temperatureSensor/settings/setpoint")) return GaRoDataType.TemperatureSetpointSet;
		//if(recId.contains("temperatureSensor/reading")) return GaRoDataType.TemperatureMeasurementThermostat;
		//if(recId.contains("TEMPERATURE/reading")) return GaRoDataType.TemperatureMeasurementRoomSensor;
		//if(recId.contains("EXTERNAL_TEMPERATURE_0_0")) return GaRoDataType.TemperatureMeasurementRoomSensor;
		//if(recId.contains("valve/setting/stateFeedback")) return GaRoDataType.ValvePosition;
		if(recId.contains("connection/powerSensor/reading")) return GaRoDataType.PowerMeter;
		if(recId.contains("connection/energySensor/reading")) return GaRoDataType.PowerMeterEnergy;
		if(recId.contains("connection/currentSensor/reading")) return GaRoDataType.PowerMeterCurrent;
		if(recId.contains("connection/voltageSensor/reading")) return GaRoDataType.PowerMeterVoltage;
		if(recId.contains("connection/frequencySensor/reading")) return GaRoDataType.PowerMeterFrequency;
		if(recId.contains("connection/reactiveAngleSensor/reading")) return GaRoDataType.PowerMeterReactiveAngle;
		if((recId.contains("connection/subPhaseConnections") || recId.contains("connection/L"))
				&&recId.contains("powerSensor/reading")) return GaRoDataType.PowerMeterSubphase;
		if((recId.contains("connection/subPhaseConnections") || recId.contains("connection/L"))
				&&recId.contains("energySensor/reading")) return GaRoDataType.PowerMeterEnergySubphase;
		if((recId.contains("connection/subPhaseConnections") || recId.contains("connection/L"))
				&&recId.contains("currentSensor/reading")) return GaRoDataType.PowerMeterCurrentSubphase;
		if((recId.contains("connection/subPhaseConnections") || recId.contains("connection/L"))
				&&recId.contains("voltageSensor/reading")) return GaRoDataType.PowerMeterVoltageSubphase;
		if((recId.contains("connection/subPhaseConnections") || recId.contains("connection/L"))
				&&recId.contains("reactiveAngleSensor/reading")) return GaRoDataType.PowerMeterReactiveAngleSubphase;
		if(recId.contains("RexometerSerial/configs/gas_energy/value")) return GaRoDataType.GasMeter;
		if(recId.contains("RexometerSerial/configs/gas_batteryVoltage/value")) return GaRoDataType.GasMeterBatteryVoltage;
		if(recId.contains("semaLevel")) return GaRoDataType.CompetitionLevel;
		if(recId.contains("competitionPosition")) return GaRoDataType.CompetitionPosition;
		if(recId.contains("Points")) return GaRoDataType.CompetitionPoints;

		if(recId.contains("/apparentEnergy/reading")) return GaRoDataType.ApparentEnergy;
		if(recId.contains("/apparentPower/reading")) return GaRoDataType.ApparentPower;
		
		if(recId.contains("/powerSensor/reading")) return GaRoDataType.PowerMeterOutlet;
		if(recId.contains("/reactivePowerSensor/reading")) return GaRoDataType.ReactivePowerMeterOutlet;
		if(recId.contains("/reactivePowerSensorQ1/reading")) return GaRoDataType.ReactivePowerMeterQ1;
		if(recId.contains("/reactivePowerSensorQ2/reading")) return GaRoDataType.ReactivePowerMeterQ2;
		if(recId.contains("/reactivePowerSensorQ3/reading")) return GaRoDataType.ReactivePowerMeterQ3;
		if(recId.contains("/reactivePowerSensorQ4/reading")) return GaRoDataType.ReactivePowerMeterQ4;
		if(recId.contains("/reactiveEnergy/reading")) return GaRoDataType.ReactiveEnergy;
		if(recId.contains("/currentSensor/reading")) return GaRoDataType.CurrentSensorOutlet;
		if(recId.contains("/voltageSensor/reading")) return GaRoDataType.VoltageSensorOutlet;
		if(recId.contains("/voltageSensor")) return GaRoDataType.VoltageSensorPhase;
		if(recId.contains("/frequencySensor/reading")) return GaRoDataType.FrequencySensorOutlet;
		if(recId.contains("/reactiveAngleSensor/reading")) return GaRoDataType.PowerMeterReactiveAngle;
		if(recId.contains("/energySensor/reading")) return GaRoDataType.EnergyIntegralOutlet;
		
		if(recId.endsWith("/r")) return GaRoDataType.MultiColorRed;
		if(recId.endsWith("/g")) return GaRoDataType.MultiColorGreen;
		if(recId.endsWith("/b")) return GaRoDataType.MultiColorBlue;
		if(recId.endsWith("/c")) return GaRoDataType.MultiColorColdWhite;
		if(recId.endsWith("/w")) return GaRoDataType.MultiColorWarmWhite;
		if(recId.contains("/dimmer/onOffSwitch/stateControl")) return GaRoDataType.DimmerOnOffControl;
		if(recId.contains("/dimmer/onOffSwitch/stateFeedback")) return GaRoDataType.DimmerOnOffFb;
		if(recId.contains("/dimmer/setting/stateControl")) return GaRoDataType.DimmerStateControl;
		if(recId.contains("/dimmer/setting/stateFeedback")) return GaRoDataType.DimmerStateFb;
		
		if(recId.contains("/operationMode/stateFeedback")) return GaRoDataType.OperationModeFb; //"onOffSwitch/stateFeedback"
		if(recId.contains("/fan/setting/stateFeedback")) return GaRoDataType.FanFb; //"onOffSwitch/stateFeedback"
		if(recId.contains("/setting/stateFeedback")) return GaRoDataType.MultiSwitchStateFeedback; //"onOffSwitch/stateFeedback"
		if(recId.contains("/operationMode/stateControl")) return GaRoDataType.OperationModeControl; //"onOffSwitch/stateFeedback"
		if(recId.contains("/fan/setting/stateControl")) return GaRoDataType.FanControl; //"onOffSwitch/stateFeedback"
		if(recId.contains("/setting/stateControl")) return GaRoDataType.MultiSwitchStateControl; //"onOffSwitch/stateFeedback"		
		if(recId.contains("/stateFeedback")) return GaRoDataType.SwitchStateFeedback; //"onOffSwitch/stateFeedback"
		if(recId.contains("/stateControl")) return GaRoDataType.SwitchStateControl; //"onOffSwitch/stateFeedback"

		
		if(recId.contains("/powerSensor/settings/setpoint")) return GaRoDataType.DevicePowerControl;
		if(recId.contains("/reactivePowerSensor/settings/setpoint")) return GaRoDataType.DeviceReactivePowerControl;
		
		if(recId.contains("/POWER_")) return GaRoDataType.Heatpower;
		if(recId.contains("/ENERGY_")) return GaRoDataType.HeatEnergyIntegral;
		if(recId.contains("/VOLUME_FLOW_")) return GaRoDataType.HeatFlow;
		if(recId.contains("/VOLUME")) return GaRoDataType.HeatVolumeIntegral;
		if(recId.contains("/FLOW_TEMPERATURE_")) return GaRoDataType.HeatSupplyTemperatur;
		if(recId.contains("/inputTemperature/")) return GaRoDataType.HeatSupplyTemperatur;
		if(recId.contains("/RETURN_TEMPERATURE_")) return GaRoDataType.HeatReturnTemperatur;
		if(recId.contains("/outputTemperature/")) return GaRoDataType.HeatReturnTemperatur;

		if(recId.startsWith("vpFlowScope/") && recId.contains("/flow/reading")) return GaRoDataType.HeatFlow;
		if(recId.startsWith("vpFlowScope/") && recId.contains("/flowInLpmin/reading")) return GaRoDataType.HeatFlowInLpmin;
		if(recId.startsWith("vpFlowScope/") && recId.contains("/pressure/reading")) return GaRoDataType.HeatFlowPressure;
		if(recId.startsWith("vpFlowScope/") && recId.contains("/temperature/reading")) return GaRoDataType.HeatSupplyTemperatur;
		
		if(recId.contains("/pH_Wert_1/sensor/reading")) return GaRoDataType.WaterPHValue;
		//if(recId.contains("/Leitwert_S__1/sensor/reading")) return GaRoDataType.WaterConductivityValue;
		if(recId.contains("/Redox_1/sensor/reading")) return GaRoDataType.WaterRedoxValue;
		if(recId.contains("/Sauerstoff_1/sensor/reading")) return GaRoDataType.WaterOxygenConcentrationValue;
		if(recId.contains("/Temperatur_1/sensor/reading")) return GaRoDataType.WaterTemperatureValue;
		if(recId.contains("/USER_DEFINED_0_0")) return GaRoDataType.CO2Concentration;
		if(recId.contains("/co2/reading")) return GaRoDataType.CO2Concentration;
		if(recId.contains("/CARBON_DIOXIDE_RECEIVER_")) return GaRoDataType.CO2Concentration;
		//if(recId.contains("NetworkState/mainNetworkOk")) return GaRoDataType.InternetConnection;
		if(recId.contains("/communicationStatus/communicationDisturbed")) return GaRoDataType.CommunicationDisturbed;
		if(recId.contains("/lightSensor/reading")) return GaRoDataType.LightSensor;
		if(recId.contains("BRIGHTNESS/reading")) return GaRoDataType.LightSensor;
		if(recId.contains("/sensors/BRIGHTNESS/rawValue")) return GaRoDataType.LightSensorRaw;

		if(recId.contains("/connected/")) return GaRoDataType.ConnectedStatus;
		if(recId.contains("/dutyCycle/")) return GaRoDataType.DutyCycle;
		if(recId.contains("/dutyCycleLevel/")) return GaRoDataType.DutyCycleLevel;
		if(recId.contains("/carrierSensLevel/")) return GaRoDataType.CarrierSensLevel;
		if(recId.contains("/communicationStatus/communicationDisturbed")) return GaRoDataType.CommunicationDisturbed;

		if(recId.contains("/openFiles/reading")) return GaRoDataType.ControllerOpenFiles;
		if(recId.contains("/totalWritePerHour")) return GaRoDataType.SetpointPerHour;
		if(recId.contains("/conditionalWritePerHour")) return GaRoDataType.SetpointPerHourConditional;
		if(recId.contains("/conditionalDropPerHour")) return GaRoDataType.SetpointPerHourCondDrop;
		if(recId.contains("/priorityWritePerHour")) return GaRoDataType.SetpointPerHourPriority;
		if(recId.contains("/priorityDropPerHour")) return GaRoDataType.SetpointPerHourPrioDrop;
		if(recId.contains("/resendMissingFbPerHour")) return GaRoDataType.SetpointResendMissingFbPerHour;
		if(recId.contains("/dutyCycleMax")) return GaRoDataType.DutyCycleEff;
		if(recId.contains("/relativeLoadEff")) return GaRoDataType.RouterLoadEff;

		if(recId.contains("Gateway_Device/gitUpdateStatus")) return GaRoDataType.SystemUpdateStatus;
		if(recId.contains("Gateway_Device/systemRestart")) return GaRoDataType.SystemRestartLog;
		if(recId.contains("numberIP4AddressesHM")) return GaRoDataType.RouterIPv4HM;
		if(recId.contains("numberIP4AddressesSSH")) return GaRoDataType.RouterIPv4SSH;
		if(recId.contains("numberIP6PlusAddressesHM")) return GaRoDataType.RouterIPv6HM;
		if(recId.contains("numberIP6PlusAddressesSSH")) return GaRoDataType.RouterIPv6SSH;
		if(recId.contains("/uptime")) return GaRoDataType.UptimeInterval;
		
		for(GaRoDataType type: GaRoDataType.standardTypes) {
    		if(type.label(null).equals(recId)) return type;
    	}
		if(recId.contains("Gateway_Device/heartBeatDelay")) return GaRoDataType.HeartbeatSendInterval;
		if(recId.contains("warningMessageInterval")) return GaRoDataType.HeartbeatMaxInterval;
		if(recId.contains("Gateway_Device/activeAlarmSupervision")) return GaRoDataType.AlarmSupervisionNum;
		if(recId.contains("EvalCollection/knownIssueDataGw/activeAlarmSupervision")) return GaRoDataType.AlarmSupervisionNum;
		if(recId.contains("Gateway_Device/datapointsInAlarmState")) return GaRoDataType.DatapointsAlarmNum;
		if(recId.contains("EvalCollection/knownIssueDataGw/datapointsInAlarmState")) return GaRoDataType.DatapointsAlarmNum;
		
		if(recId.contains("/energyDaily/reading")) return GaRoDataType.EnergyDaily;
		if(recId.contains("/energyMonthly/reading")) return GaRoDataType.EnergyMonthly;
		if(recId.contains("/energyDailyAccumulatedFull/reading")) return GaRoDataType.EnergyDailyAccumulatedFull;
		if(recId.contains("/energyReactiveDaily/reading")) return GaRoDataType.EnergyReactiveDaily;
		if(recId.contains("/energyAccumulatedDaily/reading")) return GaRoDataType.EnergyDailyAccumulated;
		if(recId.contains("/energyReactiveAccumulatedDaily/reading")) return GaRoDataType.EnergyReactiveDailyAccumulated;
		if(recId.contains("/energyReactiveAccumulatedDailyFull/reading")) return GaRoDataType.EnergyReactiveDailyAccumulatedFull;
		if(recId.contains("/billedEnergy/reading")) return GaRoDataType.BilledEnergy;
		if(recId.contains("/billedEnergyReactive/reading")) return GaRoDataType.BilledEnergyReactive;

		if(recId.contains("/energySumHourly/reading")) return GaRoDataType.EnergySumHourly;
		if(recId.contains("/energySumDaily/reading")) return GaRoDataType.EnergySumDaily;
		if(recId.contains("/energySumMonthly/reading")) return GaRoDataType.EnergySumMonthly;
		if(recId.contains("/energySumYearly/reading")) return GaRoDataType.EnergySumYearly;

		if(recId.contains("/SMOKE_DETECTOR") && (recId.contains("/reading"))) return GaRoDataType.SmokeDetect;
		if(recId.contains("/sensors/smoke")) return GaRoDataType.SmokeDetect;
		if(recId.contains("/SMOKE_DETECTOR") && (recId.contains("/error"))) return GaRoDataType.SmokeDetectError;

		if(recId.contains("/sensors/RAIN_COUNTER")) return GaRoDataType.RainCounter; //"onOffSwitch/stateFeedback"
		if(recId.contains("/sensors/RAINING")) return GaRoDataType.RainStatus; //"onOffSwitch/stateFeedback"
		if(recId.contains("/sensors/WIND_SPEED") || recId.contains("/sensors/wind/speed")) return GaRoDataType.WindSpeed; //"onOffSwitch/stateFeedback"
		if(recId.contains("/sensors/WIND_DIRECTION") || recId.contains("/sensors/wind/direction")) return GaRoDataType.WindDirection; //"onOffSwitch/stateFeedback"
		if(recId.contains("/sensors/solarIrradiation")) return GaRoDataType.SolarIrradiation; //"onOffSwitch/stateFeedback"
		
		if(recId.contains("/sensors/co_alert")) return GaRoDataType.CO_Alert; //"onOffSwitch/stateFeedback"
		if(recId.contains("/sensors/siren")) return GaRoDataType.SirenStatus; //"onOffSwitch/stateFeedback"
		if(recId.contains("/sensors/air")) return GaRoDataType.AirQuality1Best10Worst; //"onOffSwitch/stateFeedback"
		if(recId.endsWith("MessageInterval")) return GaRoDataType.MessageInterval; //"onOffSwitch/stateFeedback"

		if(recId.contains("/monthlyTotalKiB")) return GaRoDataType.TrafficDataTotal;
		if(recId.contains("/trafficSum")) return GaRoDataType.TrafficDataTotal;
		if(recId.contains("/bitrate")) return GaRoDataType.BitRate;
		if(recId.endsWith("/channel")) return GaRoDataType.CommChannel;
		if(recId.contains("/foundPublicAddressLastPartRaw")) return GaRoDataType.IPAddrPartLastProcessedRaw;
		if(recId.contains("/foundPublicAddressLastPart")) return GaRoDataType.IPAddrPartLastProcessed;
		if(recId.contains("/apiMethodAccess")) return GaRoDataType.APIAccessCounter;

		if(recId.contains("Gateway_Device/pst")) return GaRoDataType.MemoryTsDebug;
		if(recId.endsWith("/ecoMode")||recId.endsWith("/ecoModeActive")) return GaRoDataType.EcoMode;
		if(recId.endsWith("/seasonMode")) return GaRoDataType.SeasonMode;
		if(recId.contains("Gateway_Device/usedSpace")) return GaRoDataType.UsedDiskSpace;

		if(recId.contains("$$")) return GaRoDataType.Internal;
		return GaRoDataType.Unknown;
	}
	
	/*public static List<GaRoDataType> getGatewayTypes() {
		return Arrays.asList(new GaRoDataType[] {GaRoDataType.PowerMeter, GaRoDataType.CompetitionLevel,
				GaRoDataType.CompetitionPoints, GaRoDataType.CompetitionPosition,
				GaRoDataType.OutsideTemperatureGw});
	}
	public static List<GaRoDataType> getOverallTypes() {
		return Arrays.asList(new GaRoDataType[] {GaRoDataType.OutsideTemperatureOverall});
	}*/

	@SuppressWarnings("unchecked")
	public static RequiredInputData getRequiredInput(GaRoDataTypeI dt) {
		String description = dt.description(null);
		return new RequiredInputDefault(dt.label(null),
				(description!=null)?description:dt.label(null),
				(Class<? extends SingleValueResource>) dt.representingResourceType());
		//RequiredInputData result = getRequiredInputInternal(dataType,
		//		(Class<? extends SingleValueResource>) dataType.representingResourceType());
		//return result;
	}
	/*private static RequiredInputData getRequiredInputInternal(GaRoDataType dataType,
			Class<? extends SingleValueResource> type) {
		switch(dataType.label(null)) {
		case "TemperatureMeasurementRoomSensor":
			return new RequiredInputDefault("Temperature measurements in the room", "Provide all temperature measurement timesieres that shall be"
				+ "evaluated. This are temperature measurements of room sensors.",
				type); //TemperatureResource.class);
		case "TemperatureMeasurementThermostat": // FIXME duplicate ids not admissible!
			return new RequiredInputDefault("Temperature measurements in the room", "Provide all temperature measurement timesieres that shall be"
				+ "evaluated. This are temperature measurements of thermostats.",
				type); //TemperatureResource.class);
		case "TemperatureSetpointFeedback":
			return new RequiredInputDefault("Temperature setpoints in the room", "Provide all temperature setpoints that shall be"
				+ "evaluated.",
				type); //TemperatureResource.class);
		case "WindowOpen":
			return new RequiredInputDefault(
		    		"Window opening sensors of the room", "Provide time series for all window opening sensors of the room",
		    		type); //BooleanResource.class);
		case "ValvePosition":
			return new RequiredInputDefault(
		    		"Valve positions of the room", "Provide time series for all valves of the room",
		    		type); //FloatResource.class);
		case "MotionDetection":
			return new RequiredInputDefault(
		    		"Motion detection sensor in the room", "Provide time series for a motion detector of the room",
		    		type); //FloatResource.class);
		case "PowerMeter":
			return new RequiredInputDefault(
		    		"PowerMeter of gateway", "Provide time series for a power meter of the gateway",
		    		type); //FloatResource.class);
		case "PreEvaluated":
			return new RequiredInputDefault(
		    		"Pre-evaluated time series", "Provide some pre-evaluated time series, see Pre-evaluation defintion for details",
		    		type); //FloatResource.class);
		default:
			throw new IllegalStateException(dataType.label(null)+" not implemented yet as Required Input");
		}
	}*/
	
	public static List<RequiredInputData> getInputDataTypes(GaRoDataTypeI[] gaRoDataTypeIs) {
		List<RequiredInputData> result = new ArrayList<>();
		for(GaRoDataTypeI dt: gaRoDataTypeIs) {
			result.add(getRequiredInput(dt));
		}
		return result;
	}

    public static void printAllResults(final String room, final Map<ResultType, EvaluationResult> results, long[] startEnd) {
   // public static void printResults(final String room, final Map<ResultType, EvaluationResult> results, long start, long end) {
		int ll = Integer.getInteger("org.ogema.multieval.loglevel", 10);
		if(ll >= 10) System.out.println("*********************************");
		if(ll >= 10) System.out.println("Room base evaluation results: " + room);
    	//if (startEnd!= null)
		if(ll >= 10) System.out.println("Duration: " + new Date(startEnd[0]) + " - " + new Date(startEnd[1])); // FIXME
    	for (Entry<ResultType, EvaluationResult> re: results.entrySet()) {
			final SingleEvaluationResult r = re.getValue().getResults().iterator().next();
			if (r instanceof SingleValueResult<?>) {
				final Object value = ((SingleValueResult<?>) r).getValue();
				if(ll >= 10) System.out.println(" " + re.getKey().description(OgemaLocale.ENGLISH) + ": " + value);
			}
    	}
    	if(ll >= 10) System.out.println("*********************************");
    }

	public static interface CSVArchiveExporter {
		void writeGatewayDataArchive(FileOutputStream file,
				Collection<GaRoTimeSeriesId> timeSeriesToExport,
	            long startTime, long endTime) throws IOException;
	}

	private static class AggData {
		float sum = 0;
		int countRoom = 0;
	}
	private static class AggDataString {
		String elements = null;
	}

    /** Extract averages over all rooms and gateways for all results in the roomEvals struct
     * of a GaRoMultiResult. Note that each GaRoMultiResult represents a single evaluation interval,
     * so averaging over intervals is not relevant here anyways.
     * TODO: Support not only averaging in the future
     * @param singleTimeStepResult
     * @return
     */
	public static Map<String, Float> getKPIs(GaRoMultiResult singleTimeStepResult, String subGw) {
		Map<String, Float> result = new HashMap<>();
		Map<String, AggData> aggData = new HashMap<>();
		for(RoomData rd: singleTimeStepResult.roomEvals) {
			if((subGw != null)&&(!rd.gwId.equals(subGw))) continue;
			if(rd.evalResults != null) for(Entry<String, String> resData: rd.evalResults.entrySet()) {
				try {
					float val = Float.parseFloat(resData.getValue());
					if(Float.isNaN(val)) continue;
					AggData ad = aggData.get(resData.getKey());
					if(ad == null) {
						ad = new AggData();
						aggData.put(resData.getKey(), ad);
					}
					ad.sum += val;
					ad.countRoom++;
				} catch(NumberFormatException e) {
					//do nothing
				}
			}
		}
		for(Entry<String, AggData> ad: aggData.entrySet()) {
			final float value;
			if(ad.getValue().countRoom == 0) value = Float.NaN;
			else value = ad.getValue().sum /  ad.getValue().countRoom;
			result.put(ad.getKey(), value);
		}
		return result ;
	}
	public static Map<String, String> getStringKPIs(GaRoMultiResult singleTimeStepResult, String subGw) {
		Map<String, String> result = new HashMap<>();
		Map<String, AggDataString> aggData = new HashMap<>();
		for(RoomData rd: singleTimeStepResult.roomEvals) {
			if((subGw != null)&&(!rd.gwId.equals(subGw))) continue;
			if(rd.evalResults != null) for(Entry<String, String> resData: rd.evalResults.entrySet()) {
				try {
					if(!resData.getKey().startsWith("$")) continue;
					//float val = Float.parseFloat(resData.getValue());
					//if(Float.isNaN(val)) continue;
					AggDataString ad = aggData.get(resData.getKey());
					if(ad == null) {
						ad = new AggDataString();
						aggData.put(resData.getKey(), ad);
					}
					if(ad.elements == null)
						ad.elements = resData.getValue();
					else
						ad.elements += ", "+resData.getValue();
				} catch(NumberFormatException e) {
					//do nothing
				}
			}
		}
		for(Entry<String, AggDataString> ad: aggData.entrySet()) {
			//final float value;
			//if(ad.getValue().countRoom == 0) value = Float.NaN;
			//else value = ad.getValue().sum /  ad.getValue().countRoom;
			if(ad.getValue().elements != null) result.put(ad.getKey(), ad.getValue().elements);
		}
		return result ;
	}
	/*public static Map<String, Float> getOverallKPIs(GaRoSuperEvalResult<?> superResult, String... ids) {
		Map<String, AggData> aggData = new HashMap<>();
		for(GaRoMultiResult singleTimeStepResult: superResult.intervalResults) {
			for(RoomData rd: singleTimeStepResult.roomEvals) {
				if(rd.evalResults != null) {
					if(ids.length == 0) for(Entry<String, String> resData: rd.evalResults.entrySet()) {
						processEntry(resData.getKey(), resData.getValue(), aggData);
					}
					else for(String id: ids) {
						String val = rd.evalResults.get(id);
						if(val != null) processEntry(id, val, aggData);
					}
				}
			}
		}
		return finalizeKPIs(aggData);
	}
	private static void processEntry(String key, String value, Map<String, AggData> aggData) {
		try {
			float val = Float.parseFloat(value);
			if(Float.isNaN(val)) return;
			AggData ad = aggData.get(key);
			if(ad == null) {
				ad = new AggData();
				aggData.put(key, ad);
			}
			ad.sum += val;
			ad.countRoom++;
		} catch(NumberFormatException e) {
			//do nothing
		}		
	}
	private static Map<String, Float> finalizeKPIs(Map<String, AggData> aggData) {
		Map<String, Float> result = new HashMap<>();
		for(Entry<String, AggData> ad: aggData.entrySet()) {
			final float value;
			if(ad.getValue().countRoom == 0) value = Float.NaN;
			else value = ad.getValue().sum /  ad.getValue().countRoom;
			result.put(ad.getKey(), value);
		}
		return result;
	}*/
	
	/** See {@link MultiEvaluationUtils#getFittingTSforEval(DataProvider, de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationProvider, List)}
	 * @param dp DataProvider to use
	 * @param provider EvaluationProvider to use
	 * @param gwIds gatewayIds to be included. Note that result is unstructued, so more
	 * 		than one gateway may lead to a quite long unstructured list
	 * @return the result is not structured here in contrast would is needed for real evaluation.
	 * 		All input time series are just collected in a list. We do not check for the same time
	 * 		series being in the list here. We get new TimeSeriesData object usually, so using
	 * 		a Set would probably not help
	 */
	public static <SI extends SelectionItem, P extends GaRoSingleEvalProvider> List<TimeSeriesData> getFittingTSforEval(DataProvider<?> dp,
			P singleProvider,
			List<String> gwIds) {
		return getFittingTSforEval(dp, singleProvider, gwIds, null);
	}
	public static <SI extends SelectionItem, P extends GaRoSingleEvalProvider> List<TimeSeriesData> getFittingTSforEval(DataProvider<?> dp,
			P singleProvider,
			List<String> gwIds, List<String> roomIds) {
		@SuppressWarnings("unchecked")
		GaRoEvalProvider<?> garoEval = new GenericGaRoMultiProvider<P>(singleProvider, false) {
			@Override
			protected GenericGaRoMultiEvaluation<P> newGenericGaRoMultiEvaluation(
					List<MultiEvaluationInputGeneric> input, Collection<ConfigurationInstance> configurations,
					GaRoEvalProvider<GaRoMultiResult> dataProviderAccess, TemporalUnit resultStepSize,
					GaRoDataTypeI[] inputTypesFromRoom, GaRoDataTypeI[] inputTypesFromGw, P singleProvider,
					boolean doBasicEval, List<ResultType> resultsRequested) {
				throw new IllegalStateException("This is a generic instance that cannot be used to create actual evaluation instances!");
			}
		};
		MultiEvaluationInputGeneric[] arr = new MultiEvaluationInputGeneric[garoEval.inputDataTypes().size()];
        int inputIdx = 0;
        int maxDependencyIdx =  garoEval.maxTreeIndex;
        List<DataProviderType> providerTypes = garoEval.inputDataTypes();
	    GaRoMultiEvalDataProvider<?> gdp = null;
	    if(dp instanceof GaRoMultiEvalDataProvider)
	    	gdp = (GaRoMultiEvalDataProvider<?>)dp;
        for(GaRoDataTypeI dt: garoEval.getInputTypesFromRoom()) {
        	MultiEvaluationInputGeneric ts = null;
        	if(gdp != null)
        		ts = gdp.provideMultiEvaluationInput(providerTypes.get(maxDependencyIdx), dp, dt, gwIds, GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID);
			if(ts == null)
				ts = new GaRoMultiEvaluationInput(
	        		providerTypes.get(maxDependencyIdx), dp, dt, gwIds, GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID); //isMulti?GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID:GaRoMultiEvalDataProvider.ROOM_LINKINGOPTION_ID);
	     	arr[inputIdx] = ts;
	     	inputIdx++;
	    }
	    List<MultiEvaluationInputGeneric> input = Arrays.<MultiEvaluationInputGeneric>asList(arr);
	    List<TimeSeriesData> result = MultiEvaluationUtilsGaRo.getFittingTSforEval(dp, garoEval, input);
	    // The GaRo DataProvider returns room time series also for the overall room. We have to filter this out
	    List<TimeSeriesData> toRemove = new ArrayList<>();
	    //candidates are time TimeSeriesData elements of BUILDING_OVERALL_ROOM_ID for which no fitting
	    //room has been found
	    Map<String, TimeSeriesData> toRemoveCandidate = new HashMap<>();
	    Set<ReadOnlyTimeSeries> knownTS = new HashSet<>();
	    Set<String> knowTSPath = new HashSet<>();
	    for(TimeSeriesData ts: result) {
			if(!(ts instanceof TimeSeriesDataOffline)) throw new IllegalStateException("getStartAndEndTime only works on TimeSeriesData input!");
			TimeSeriesDataOffline tsd = (TimeSeriesDataOffline) ts;
	    	ReadOnlyTimeSeries rots = tsd.getTimeSeries();
	    	final TimeSeriesDataExtendedImpl tse;
	    	final String roomId;
	    	if(tsd instanceof TimeSeriesDataExtendedImpl) {
				tse = (TimeSeriesDataExtendedImpl)tsd;
				if(tse.getIds() == null || tse.getIds().size() < 2) {
					continue;
					//throw new IllegalStateException("No room information - check if we should just keep the element or error?");
				}
				roomId = tse.getIds().get(1);
    		} else continue;
	    	String path = null;
	    	if(rots instanceof RecordedData) {
	    		path = ((RecordedData)rots).getPath();
	    	}
	    	if(knownTS.contains(rots) || (path != null && knowTSPath.contains(path))) {
				if(roomId.equals(GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID)) {
					toRemove.add(ts);
					continue;
				} else {
					TimeSeriesData tsCand = toRemoveCandidate.get(roomId);
					if(tsCand != null) toRemove.add(tsCand);
				}
	    	} else {
	    		knownTS.add(rots);
	    		if(path != null) knowTSPath.add(path);
	    	}
	    	if(roomId.equals(GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID)) {
	    		toRemoveCandidate.put(roomId, ts);
	    	}
	    	if(roomIds != null) {
	    		if(!roomIds.contains(roomId))
	    			toRemove.add(ts);
	    	}
	    }
	    result.removeAll(toRemove);
	    return result;
	}

}
