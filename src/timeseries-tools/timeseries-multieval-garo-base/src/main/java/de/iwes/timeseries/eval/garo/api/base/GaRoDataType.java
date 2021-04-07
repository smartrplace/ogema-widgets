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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.model.units.AngleResource;
import org.ogema.core.model.units.BrightnessResource;
import org.ogema.core.model.units.ElectricCurrentResource;
import org.ogema.core.model.units.EnergyResource;
import org.ogema.core.model.units.FlowResource;
import org.ogema.core.model.units.FrequencyResource;
import org.ogema.core.model.units.PercentageResource;
import org.ogema.core.model.units.PowerResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.model.units.VelocityResource;
import org.ogema.core.model.units.VoltageResource;
import org.ogema.core.model.units.VolumeResource;
import org.ogema.generictype.GenericAttribute;
import org.ogema.generictype.GenericAttributeImpl;

import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProviderPreEvalRequesting;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/** Supported data types
 * The respective Strings used for identifiction are defined in {@link GaRoEvalHelper#getDataType(String)}.
 * If a data type is used in an input definition also an entry in {@link GaRoEvalHelper#getRequiredInput(GaRoDataType)}
 * has to exist.<br>
 * Note that currently these files have to be extended when additional data input types shall
 * be defined and provided. An extended future approach could foresee that ResultTypes and GaRo input types declare an
 * OGEMA resource path indicating where a single value of a result would be placed in the OGEMA resource
 * model. This could be used to identify input for evaluations from DataProviders as well as from
 * Pre-Evaluation.
 */
public class GaRoDataType implements GaRoDataTypeI {
	private final Class<? extends Resource> representingResourceType;
	private final String label;
	private final Level level;
	
	/** Experimental: Usually no special unit information is provided, but it is possible*/
	public String unit = null;
	
	public GaRoDataType(String label, Class<? extends Resource> representingResourceType) {
		this(label, representingResourceType, Level.ROOM);
	}
	public GaRoDataType(String label, Class<? extends Resource> representingResourceType, Level level) {
		this.representingResourceType = representingResourceType;
		this.label = label;
		this.level = Level.ROOM;
	}

	@Override
	public String id() {
		//return ResourceUtils.getValidResourceName(getClass().getName());
		return label;
	}

	@Override
	public String label(OgemaLocale locale) {
		return label;
	}

	@Override
	public Class<? extends Resource> representingResourceType() {
		return representingResourceType;
	}

	@Override
	public List<GenericAttribute> attributes() {
		return Collections.emptyList();
	}
	
	public Level getLevel() {
		return level;
	};
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || (!(obj instanceof GaRoDataTypeI)))
			return false;
		GaRoDataTypeI objc = (GaRoDataTypeI) obj;
		return id().equals(objc.id());
	}
	@Override
	public int hashCode() {
		   int prime = 31;
		   return prime + Objects.hashCode(this.id());    
	}
	
	//The following options are per-room
	public static final GaRoDataType TemperatureMeasurementRoomSensor = new GaRoDataType("TemperatureMeasurementRoomSensor",
			TemperatureResource.class) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.SENSOR_SEPARATE});
		}		
	};
	
	public static final GaRoDataType TemperatureMeasurementThermostat = new GaRoDataType("TemperatureMeasurementThermostat", 
			TemperatureResource.class) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.SENSOR_WITH_ACTOR});
		}		
	};
	public static final GaRoDataType TemperatureSetpoint = new GaRoDataType("TemperatureSetpoint", 
			TemperatureResource.class);
	//subset of TemperatureSetpoint
	public static final GaRoDataType TemperatureSetpointFeedback = new GaRoDataType("TemperatureSetpointFeedback", 
			TemperatureResource.class);
	//subset of TemperatureSetpoint
	public static final GaRoDataType TemperatureSetpointSet = new GaRoDataType("TemperatureSetpointSet", 
			TemperatureResource.class);
	public static final GaRoDataType ValvePosition = new GaRoDataType("ValvePosition", 
			FloatResource.class);
	public static final GaRoDataType HumidityMeasurement = new GaRoDataType("HumidityMeasurement",
			PercentageResource.class);
	public static final GaRoDataType MotionDetection = new GaRoDataType("MotionDetection",
			BooleanResource.class);
	public static final GaRoDataType WindowOpen = new GaRoDataType("WindowOpen",
			BooleanResource.class);
	public static final GaRoDataType LightSensor = new GaRoDataType("LightSensor",
			BrightnessResource.class);
	public static final GaRoDataType LightSensorRaw = new GaRoDataType("LightSensorRaw",
			FloatResource.class);
	public static final GaRoDataType ChargeSensor = new GaRoDataType("ChargeSensor",
			BooleanResource.class);
	public static final GaRoDataType ChargeVoltage = new GaRoDataType("ChargeVoltage",
			VoltageResource.class);
	public static final GaRoDataType PowerMeterOutlet = new GaRoDataType("PowerMeterOutlet",
			PowerResource.class);
	public static final GaRoDataType ReactivePowerMeterOutlet = new GaRoDataType("ReactivePowerMeterOutlet",
			PowerResource.class);
	public static final GaRoDataType ReactivePowerMeterQ1 = new GaRoDataType("ReactivePowerQ1",
			PowerResource.class);
	public static final GaRoDataType ReactivePowerMeterQ2 = new GaRoDataType("ReactivePowerQ2",
			PowerResource.class);
	public static final GaRoDataType ReactivePowerMeterQ3 = new GaRoDataType("ReactivePowerQ3",
			PowerResource.class);
	public static final GaRoDataType ReactivePowerMeterQ4 = new GaRoDataType("ReactivePowerQ4",
			PowerResource.class);
	public static final GaRoDataType ReactiveEnergy = new GaRoDataType("ReactiveEnergy",
			EnergyResource.class);
	public static final GaRoDataType ApparentPower = new GaRoDataType("ApparentPower",
			PowerResource.class);
	public static final GaRoDataType ApparentEnergy = new GaRoDataType("ApparentEnergy",
			EnergyResource.class);
	public static final GaRoDataType CurrentSensorOutlet = new GaRoDataType("CurrentSensorOutlet",
			ElectricCurrentResource.class);
	public static final GaRoDataType VoltageSensorOutlet = new GaRoDataType("VoltageSensorOutlet",
			VoltageResource.class);
	public static final GaRoDataType VoltageSensorPhase = new GaRoDataType("VoltageSensorPhase",
			VoltageResource.class);
	public static final GaRoDataType FrequencySensorOutlet = new GaRoDataType("FrequencySensorOutlet",
			FrequencyResource.class);
	public static final GaRoDataType EnergyIntegralOutlet = new GaRoDataType("EnergyIntegralOutlet",
			EnergyResource.class);
	public static final GaRoDataType SwitchStateFeedback = new GaRoDataType("SwitchStateFeedback",
			BooleanResource.class);
	public static final GaRoDataType SwitchStateControl = new GaRoDataType("SwitchStateControl",
			BooleanResource.class);
	public static final GaRoDataType MultiSwitchStateFeedback = new GaRoDataType("MultiSwitchStateFeedback",
			BooleanResource.class);
	public static final GaRoDataType MultiSwitchStateControl = new GaRoDataType("MultiSwitchStateControl",
			BooleanResource.class);
	
	public static final GaRoDataType DevicePowerControl = new GaRoDataType("DevicePowerControl",
			PowerResource.class);
	public static final GaRoDataType DeviceReactivePowerControl = new GaRoDataType("DeviceReactivePowerControl",
			PowerResource.class);

	public static final GaRoDataType Heatpower = new GaRoDataType("Heatpower",
			PowerResource.class);
	public static final GaRoDataType HeatEnergyIntegral = new GaRoDataType("HeatEnergyIntegral",
			EnergyResource.class);
	public static final GaRoDataType HeatFlow = new GaRoDataType("HeatFlow",
			FlowResource.class);
	public static final GaRoDataType HeatVolumeIntegral = new GaRoDataType("HeatVolumeIntegral",
			VolumeResource.class);
	public static final GaRoDataType HeatSupplyTemperatur = new GaRoDataType("HeatSupplyTemperatur",
			TemperatureResource.class);
	public static final GaRoDataType HeatReturnTemperatur = new GaRoDataType("HeatReturnTemperatur",
			TemperatureResource.class);
	public static final GaRoDataType WaterPHValue = new GaRoDataType("WaterPHValue",
			FloatResource.class);
	public static final GaRoDataType WaterConductivityValue = new GaRoDataType("WaterConductivityValue",
			FloatResource.class);
	public static final GaRoDataType WaterRedoxValue = new GaRoDataType("WaterRedoxValue",
			FloatResource.class);
	public static final GaRoDataType WaterOxygenConcentrationValue = new GaRoDataType("WaterOxygenConcentrationValue",
			FloatResource.class);
	public static final GaRoDataType WaterTemperatureValue = new GaRoDataType("WaterTemperatureValue",
			TemperatureResource.class);
	public static final GaRoDataType CO2Concentration = new GaRoDataType("CO2Concentration",
			FloatResource.class);
	public static final GaRoDataType InternetConnection = new GaRoDataType("InternetConnection",
			BooleanResource.class);
	public static final GaRoDataType RSSIDevice = new GaRoDataType("RSSIDevice",
			FloatResource.class);
	public static final GaRoDataType RSSIPeer = new GaRoDataType("RSSIPeer",
			FloatResource.class);
	public static final GaRoDataType RSSISignal = new GaRoDataType("RSSISignal",
			FloatResource.class);
	public static final GaRoDataType CommunicationQuality = new GaRoDataType("CommunicationQuality",
			FloatResource.class);
	public static final GaRoDataType CommunicationDisturbed = new GaRoDataType("CommunicationDisturbed",
			BooleanResource.class);
	public static final GaRoDataType ConnectedStatus = new GaRoDataType("ConnectedStatus",
			BooleanResource.class);
	public static final GaRoDataType DutyCycle = new GaRoDataType("DutyCycle",
			FloatResource.class);
	public static final GaRoDataType DutyCycleLevel = new GaRoDataType("DutyCycleLevel",
			FloatResource.class);
	public static final GaRoDataType CarrierSensLevel = new GaRoDataType("CarrierSensLevel",
			FloatResource.class);
	/** Food amount added at a certain time*/
	public static final GaRoDataType FoodAmount = new GaRoDataType("FoodAmount",
			FloatResource.class);
	
	/** Fresh water added at a certain time*/
	public static final GaRoDataType FreshWater = new GaRoDataType("FreshWater",
			FloatResource.class);
	/** Fresh water meter counter*/
	public static final GaRoDataType FreshWaterVolume = new GaRoDataType("FreshWaterVolume",
			FloatResource.class);
	/** Fresh water float as m3/s (?)*/
	public static final GaRoDataType FreshWaterFlow = new GaRoDataType("FreshWaterFlow",
			FloatResource.class);

	public static final GaRoDataType SystemUpdateStatus = new GaRoDataType("SystemUpdateStatus",
			IntegerResource.class);
	public static final GaRoDataType SystemRestartLog = new GaRoDataType("SystemRestartLog",
			IntegerResource.class);
	public static final GaRoDataType RouterIPv4HM = new GaRoDataType("IPv4_HM",
			IntegerResource.class);
	public static final GaRoDataType RouterIPv4SSH = new GaRoDataType("IPv4_SSH",
			IntegerResource.class);
	public static final GaRoDataType RouterIPv6HM = new GaRoDataType("IPv6_HM",
			IntegerResource.class);
	public static final GaRoDataType RouterIPv6SSH = new GaRoDataType("IPv6_SSH",
			IntegerResource.class);
	public static final GaRoDataType UptimeInterval = new GaRoDataType("UptimeInterval",
			TimeResource.class);

	public static final GaRoDataType HeartbeatSendInterval = new GaRoDataType("HeartbeatSendInterval",
			TimeResource.class);
	public static final GaRoDataType HeartbeatMaxInterval = new GaRoDataType("HeartbeatMaxInterval",
			TimeResource.class);
	public static final GaRoDataType AlarmSupervisionNum = new GaRoDataType("AlarmSupervisionNum",
			IntegerResource.class);
	public static final GaRoDataType DatapointsAlarmNum = new GaRoDataType("DatapointsAlarmNum",
			IntegerResource.class);
	
	public static final GaRoDataType EnergyDaily = new GaRoDataType("EnergyDaily",
			EnergyResource.class);
	public static final GaRoDataType EnergyReactiveDaily = new GaRoDataType("EnergyReactiveDaily",
			EnergyResource.class);
	public static final GaRoDataType EnergyDailyAccumulated = new GaRoDataType("EnergyDailyAccumulated",
			EnergyResource.class);
	public static final GaRoDataType EnergyDailyAccumulatedFull = new GaRoDataType("EnergyDailyAccumulatedFull",
			EnergyResource.class);
	public static final GaRoDataType EnergyReactiveDailyAccumulated = new GaRoDataType("EnergyReactiveDailyAccumulated",
			EnergyResource.class);
	public static final GaRoDataType EnergyReactiveDailyAccumulatedFull = new GaRoDataType("EnergyReactiveDailyAccumulatedFull",
			EnergyResource.class);
	public static final GaRoDataType BilledEnergy = new GaRoDataType("BilledEnergy",
			EnergyResource.class);
	public static final GaRoDataType BilledEnergyReactive = new GaRoDataType("BilledEnergyReactive",
			EnergyResource.class);

	public static final GaRoDataType EnergySumHourly = new GaRoDataType("EnergySumHourly",
			EnergyResource.class);
	public static final GaRoDataType EnergySumDaily = new GaRoDataType("EnergySumDaily",
			EnergyResource.class);
	public static final GaRoDataType EnergySumMonthly = new GaRoDataType("EnergySumMonthly",
			EnergyResource.class);
	public static final GaRoDataType EnergySumYearly = new GaRoDataType("EnergySumYearly",
			EnergyResource.class);

	
	public static final GaRoDataType SmokeDetect = new GaRoDataType("SmokeDetect",
			BooleanResource.class);
	public static final GaRoDataType SmokeDetectError = new GaRoDataType("SmokeDetectError",
			BooleanResource.class);

	public static final GaRoDataType RainCounter = new GaRoDataType("RainCounter",
			FloatResource.class);
	public static final GaRoDataType RainStatus = new GaRoDataType("RainStatus",
			BooleanResource.class);
	public static final GaRoDataType WindSpeed = new GaRoDataType("WindSpeed",
			VelocityResource.class);
	public static final GaRoDataType WindDirection = new GaRoDataType("WindDirection",
			AngleResource.class);

	public static final GaRoDataType SirenStatus = new GaRoDataType("SirenStatus",
			BooleanResource.class);
	public static final GaRoDataType CO_Alert = new GaRoDataType("CO_Alert",
			BooleanResource.class);
	public static final GaRoDataType AirQuality1Best10Worst = new GaRoDataType("AirQuality_1Best_10Worst",
			FloatResource.class);
	
	public static final GaRoDataType TrafficDataTotal = new GaRoDataType("kBTrafficTotal",
			FloatResource.class);
	public static final GaRoDataType BitRate = new GaRoDataType("BitRate",
			IntegerResource.class);
	public static final GaRoDataType CommChannel = new GaRoDataType("Channel",
			IntegerResource.class);
	public static final GaRoDataType IPAddrPartLastProcessed = new GaRoDataType("IPPartLast",
			IntegerResource.class);
	public static final GaRoDataType IPAddrPartLastProcessedRaw = new GaRoDataType("IPPartLastRaw",
			IntegerResource.class);
	public static final GaRoDataType APIAccessCounter = new GaRoDataType("APIaccess",
			FloatResource.class);
	

	//The following options are per-gateway
	public static final GaRoDataType PowerMeter = new GaRoDataType("PowerMeter",
			PowerResource.class, Level.GATEWAY);
	public static final GaRoDataType PowerMeterEnergy = new GaRoDataType("PowerMeterEnergy",
			EnergyResource.class, Level.GATEWAY);
	public static final GaRoDataType PowerMeterCurrent = new GaRoDataType("PowerMeterCurrent",
			ElectricCurrentResource.class, Level.GATEWAY);
	public static final GaRoDataType PowerMeterVoltage = new GaRoDataType("PowerMeterVoltage",
			VoltageResource.class, Level.GATEWAY);
	public static final GaRoDataType PowerMeterFrequency = new GaRoDataType("PowerMeterFrequency",
			FrequencyResource.class, Level.GATEWAY);
	public static final GaRoDataType PowerMeterReactiveAngle = new GaRoDataType("PowerMeterReactiveAngle",
			AngleResource.class, Level.GATEWAY);
	public static final GaRoDataType PowerMeterSubphase = new GaRoDataType("PowerMeterSubphase",
			PowerResource.class, Level.GATEWAY);
	public static final GaRoDataType PowerMeterEnergySubphase = new GaRoDataType("PowerMeterEnergySubphase",
			EnergyResource.class, Level.GATEWAY);
	public static final GaRoDataType PowerMeterVoltageSubphase = new GaRoDataType("PowerMeterVoltageSubphase",
			VoltageResource.class, Level.GATEWAY);
	public static final GaRoDataType PowerMeterCurrentSubphase = new GaRoDataType("PowerMeterCurrentSubphase",
			ElectricCurrentResource.class, Level.GATEWAY);
	public static final GaRoDataType PowerMeterReactiveAngleSubphase = new GaRoDataType("PowerMeterReactiveAngleSubphase",
			AngleResource.class, Level.GATEWAY);
	public static final GaRoDataType GasMeter = new GaRoDataType("GasMeter",
			PowerResource.class, Level.GATEWAY);
	public static final GaRoDataType GasMeterBatteryVoltage = new GaRoDataType("GasMeterBatteryVoltage",
			VoltageResource.class, Level.GATEWAY);
	public static final GaRoDataType CompetitionLevel = new GaRoDataType("CompetitionLevel",
			FloatResource.class, Level.GATEWAY);
	public static final GaRoDataType CompetitionPosition = new GaRoDataType("CompetitionPosition",
			FloatResource.class, Level.GATEWAY);
	public static final GaRoDataType CompetitionPoints = new GaRoDataType("CompetitionPoints",
			FloatResource.class, Level.GATEWAY);
	public static final GaRoDataType OutsideTemperatureGw = new GaRoDataType("OutsideTemperatureGw",
			TemperatureResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType OutsideTemperaturePerForcecast= new GaRoDataType("OutsideTemperaturePerForcecast",
			TemperatureResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType OutsideHumidityPerForcecast= new GaRoDataType("OutsideHumiditiyPerForcecast",
			TemperatureResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType SolarIrradiationPerForcecast= new GaRoDataType("SolarIrradiationPerForcecast",
			TemperatureResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	
	public static final GaRoDataType SolarIrradiation = new GaRoDataType("SolarIrradiation",
			TemperatureResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType WindSpeedPerForcecast= new GaRoDataType("WindSpeed",
			TemperatureResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType WindDirectionPerForcecast= new GaRoDataType("WindDirection",
			TemperatureResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};

	//The following options are for all gateways. Note that most overall options can also be
	//applied as individual inputs per gateway
	public static final GaRoDataType OutsideTemperatureOverall = new GaRoDataType("OutsideTemperatureOverall",
			TemperatureResource.class, Level.OVERALL) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	
	//The following options are not linked to a specific GaRo level
	public static final GaRoDataType KPI_DURATION = new GaRoDataType("Duration",
			TimeResource.class);
	
	//General
	public static final GaRoDataType Internal = new GaRoDataType("Internal",
			SingleValueResource.class);
	public static final GaRoDataType Unknown = new GaRoDataType("Unknown",
			SingleValueResource.class);
	public static final GaRoDataType Any = new GaRoDataType("Any",
			SingleValueResource.class);
	public static final GaRoDataType LowLevel = new GaRoDataType("LowLevel",
			SingleValueResource.class);
	/** time series injected by Pre-Evalution: The actual time series is provided by
	 * {@link GaRoSingleEvalProviderPreEvalRequesting#timeSeriesToInject()}, which can be same
	 * for all evaluations or can be room-specific
	 */
	public static final GaRoDataType PreEvaluated = new GaRoDataType("PreEvaluated",
			SingleValueResource.class);
	/** This type should be the only input type and indicates that the evaluation shall be
	 * initiated once per gateway, but needs no further execution as the input data is taken
	 * e.g. from a text log file.
	 */
	public static final GaRoDataType OncePerGateway = new GaRoDataType("OncePerGateway",
			SingleValueResource.class);

	public static GaRoDataType[] standardTypes = new GaRoDataType[] {
			TemperatureMeasurementRoomSensor, TemperatureMeasurementThermostat,
			TemperatureSetpoint, TemperatureSetpointFeedback, TemperatureSetpointSet, ValvePosition,
			HumidityMeasurement, MotionDetection, WindowOpen, ChargeSensor, ChargeVoltage,
			PowerMeter, CompetitionLevel, CompetitionPosition, CompetitionPoints, OutsideTemperatureGw,
			OutsideTemperatureOverall, Unknown, Any, LowLevel, PreEvaluated, OncePerGateway};
	
	public static GaRoDataType[] standardEvalTypes = new GaRoDataType[] {
			TemperatureMeasurementRoomSensor, TemperatureMeasurementThermostat,
			TemperatureSetpoint, TemperatureSetpointFeedback, TemperatureSetpointSet, ValvePosition,
			HumidityMeasurement, MotionDetection, WindowOpen, ChargeSensor, ChargeVoltage,
			PowerMeter, PowerMeterSubphase, PowerMeterOutlet,
			PowerMeterEnergy, PowerMeterEnergySubphase,
			Heatpower, HeatEnergyIntegral, HeatFlow, HeatVolumeIntegral, HeatSupplyTemperatur, HeatReturnTemperatur,
			SwitchStateFeedback, WaterPHValue, WaterConductivityValue, WaterRedoxValue, WaterOxygenConcentrationValue,
			WaterTemperatureValue,
			FreshWaterFlow, FreshWaterVolume,
			CO2Concentration, InternetConnection, RSSIDevice, RSSIPeer,
			ChargeVoltage};
	
	public static List<GaRoDataType> volumeTypes = Arrays.asList(new GaRoDataType[] {
			PowerMeterEnergy, PowerMeterEnergySubphase, EnergyIntegralOutlet,
			HeatEnergyIntegral, HeatVolumeIntegral,
			FreshWaterVolume
	});
	public static List<GaRoDataType> volumeStepTypes = Arrays.asList(new GaRoDataType[] {
			FreshWater, FoodAmount
	});
	public static List<GaRoDataType> powerTypes = Arrays.asList(new GaRoDataType[] {
			PowerMeter, PowerMeterSubphase, PowerMeterOutlet,
			Heatpower, HeatFlow,
			FreshWaterFlow
	});

	
	/*public static final GaRoDataTypeParam powerSubPhaseType = new GaRoDataTypeParam(GaRoDataType.PowerMeterSubphase, false);
    public static final GaRoDataTypeParam powerOutletType = new GaRoDataTypeParam(GaRoDataType.PowerMeterOutlet, false);
    public static final GaRoDataTypeParam heatPowerType = new GaRoDataTypeParam(GaRoDataType.Heatpower, false);
    public static final GaRoDataTypeParam heatEnergyType = new GaRoDataTypeParam(GaRoDataType.HeatEnergyIntegral, false);
    public static final GaRoDataTypeParam heatFlowType = new GaRoDataTypeParam(GaRoDataType.HeatFlow, false);
    public static final GaRoDataTypeParam heatVolumeType = new GaRoDataTypeParam(GaRoDataType.HeatVolumeIntegral, false);
    public static final GaRoDataTypeParam heatSupplyTempType = new GaRoDataTypeParam(GaRoDataType.HeatSupplyTemperatur, false);
    public static final GaRoDataTypeParam heatReturnTempType = new GaRoDataTypeParam(GaRoDataType.HeatReturnTemperatur, false);
    public static final GaRoDataTypeParam stateFBType = new GaRoDataTypeParam(GaRoDataType.SwitchStateFeedback, false);
    public static final GaRoDataTypeParam energyType = new GaRoDataTypeParam(GaRoDataType.PowerMeterEnergy, false);
    public static final GaRoDataTypeParam energySubPhaseType = new GaRoDataTypeParam(GaRoDataType.PowerMeterEnergySubphase, false);
    public static final GaRoDataTypeParam phValueType = new GaRoDataTypeParam(GaRoDataType.WaterPHValue, false);
    public static final GaRoDataTypeParam conductivityValueType = new GaRoDataTypeParam(GaRoDataType.WaterConductivityValue, false);
    public static final GaRoDataTypeParam redoxValueType = new GaRoDataTypeParam(GaRoDataType.WaterRedoxValue, false);
    public static final GaRoDataTypeParam oxygenValueType = new GaRoDataTypeParam(GaRoDataType.WaterOxygenConcentrationValue, false);
    public static final GaRoDataTypeParam waterTempValueType = new GaRoDataTypeParam(GaRoDataType.WaterTemperatureValue, false);
    public static final GaRoDataTypeParam co2concentrationType = new GaRoDataTypeParam(GaRoDataType.CO2Concentration, false);
    public static final GaRoDataTypeParam internetType = new GaRoDataTypeParam(GaRoDataType.InternetConnection, false);
    public static final GaRoDataTypeParam rssiDeviceType = new GaRoDataTypeParam(GaRoDataType.RSSIDevice, false);
    public static final GaRoDataTypeParam rssiPeerType = new GaRoDataTypeParam(GaRoDataType.RSSIPeer, false);*/

	
	

	@Override
	public TypeCardinality typeCardinality() {
		return TypeCardinality.TIME_SERIES;
	}

	
	/*
	//The following options are per-room
	TemperatureMeasurementRoomSensor,
	TemperatureMeasurementThermostat,
	TemperatureSetpoint,
	//subset of TemperatureSetpoint
	TemperatureSetpointFeedback,
	//subset of TemperatureSetpoint
	TemperatureSetpointSet,
	ValvePosition,
	HumidityMeasurement,
	MotionDetection,
	WindowOpen,
	ChargeSensor,
	
	//The following options are per-gateway
	PowerMeter,
	CompetitionLevel,
	CompetitionPosition,
	CompetitionPoints,
	OutsideTemperatureGw,
	
	//The following options are for all gateways. Note that most overall options can also be
	//applied as individual inputs per gateway
	OutsideTemperatureOverall,
	
	//The following options are not linked to a specific GaRo level
	Unknown,
	Any,
	LowLevel,
	/** time series injected by Pre-Evalution: The actual time series is provided by
	 * {@link GaRoSingleEvalProviderPreEvalRequesting#timeSeriesToInject()}, which can be same
	 * for all evaluations or can be room-specific
	 */
	/*PreEvaluated*/
}
