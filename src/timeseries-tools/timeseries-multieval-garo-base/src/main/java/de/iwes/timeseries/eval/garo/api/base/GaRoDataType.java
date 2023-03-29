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
import org.ogema.core.model.units.ConcentrationResource;
import org.ogema.core.model.units.ElectricCurrentResource;
import org.ogema.core.model.units.EnergyPerAreaResource;
import org.ogema.core.model.units.EnergyResource;
import org.ogema.core.model.units.FlowResource;
import org.ogema.core.model.units.FrequencyResource;
import org.ogema.core.model.units.PercentageResource;
import org.ogema.core.model.units.PhysicalUnitResource;
import org.ogema.core.model.units.PowerResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.model.units.VelocityResource;
import org.ogema.core.model.units.VoltageResource;
import org.ogema.core.model.units.VolumeResource;
import org.ogema.generictype.GenericAttribute;
import org.ogema.generictype.GenericAttributeImpl;

import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;
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
		return provideLabelWithUnit(label, representingResourceType, true, unit);
		/*if(Boolean.getBoolean("org.smartrplace.driverhandler.devices.garowithunits")) {
			String unitLoc;
			if(unit != null)
				unitLoc = unit;
			else if(representingResourceType != null && (PhysicalUnitResource.class.isAssignableFrom(representingResourceType))) {
				unitLoc = getUnitString((Class<? extends PhysicalUnitResource>)representingResourceType, true);
				if(unitLoc == null)
					return label;
			} else
				return label;
			return label + " ("+unitLoc+")";
		}
		return label;*/
	}

	public static String provideLabelWithUnit(String baseLabel, GaRoDataType gaRoType,
			boolean isChart) {
		return provideLabelWithUnit(baseLabel, gaRoType.representingResourceType, isChart, gaRoType.unit);
	}
	
	@SuppressWarnings("unchecked")
	public static String provideLabelWithUnit(String baseLabel, Class<? extends Resource> representingResourceType,
			boolean isChart, String unit) {
		if(Boolean.getBoolean("org.smartrplace.driverhandler.devices.garowithunits")) {
			String unitLoc;
			if(unit != null)
				unitLoc = unit;
			else if(representingResourceType != null && (PhysicalUnitResource.class.isAssignableFrom(representingResourceType))) {
				unitLoc = getUnitString((Class<? extends PhysicalUnitResource>)representingResourceType, isChart);
				if(unitLoc == null)
					return baseLabel;
			} else
				return baseLabel;
			return baseLabel + " ("+unitLoc+")";
		}
		return baseLabel;
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
	public static final GaRoDataType ValvePositionControl = new GaRoDataType("ValvePositionControl", 
			FloatResource.class);
	public static final GaRoDataType ValvePositionLimit = new GaRoDataType("ValvePositionLimit", 
			FloatResource.class);
	public static final GaRoDataType ValvePositionLimitRequested = new GaRoDataType("ValvePositionLimitRequested", 
			FloatResource.class);
	public static final GaRoDataType ValveErrorState = new GaRoDataType("ValveErrorState", 
			FloatResource.class);
	public static final GaRoDataType ValveAdaptionStart = new GaRoDataType("ValveAdaptionStart", 
			BooleanResource.class);
	public static final GaRoDataType ValveErrorPositionCt = new GaRoDataType("ValveErrorPosCt", 
			FloatResource.class);
	public static final GaRoDataType ValveErrorPositionFb = new GaRoDataType("ValveErrorPosFb", 
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
	public static final GaRoDataType StateOfCharge = new GaRoDataType("SOC",
			FloatResource.class);
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
			FloatResource.class);
	public static final GaRoDataType MultiSwitchStateControl = new GaRoDataType("MultiSwitchStateControl",
			FloatResource.class);
	public static final GaRoDataType OperationModeFb = new GaRoDataType("OperationModeFb",
			FloatResource.class);
	public static final GaRoDataType OperationModeControl = new GaRoDataType("OperationModeCtrl",
			FloatResource.class);
	public static final GaRoDataType FanFb = new GaRoDataType("FanFb",
			FloatResource.class);
	public static final GaRoDataType FanControl = new GaRoDataType("FanCtrl",
			FloatResource.class);

	public static final GaRoDataType MultiColorRed = new GaRoDataType("Red",
			IntegerResource.class);
	public static final GaRoDataType MultiColorGreen = new GaRoDataType("Green",
			IntegerResource.class);
	public static final GaRoDataType MultiColorBlue = new GaRoDataType("Blue",
			IntegerResource.class);
	public static final GaRoDataType MultiColorColdWhite = new GaRoDataType("ColdWhite",
			IntegerResource.class);
	public static final GaRoDataType MultiColorWarmWhite = new GaRoDataType("WarmWhite",
			IntegerResource.class);
	public static final GaRoDataType DimmerOnOffControl = new GaRoDataType("DimmerOnOffControl",
			BooleanResource.class);
	public static final GaRoDataType DimmerOnOffFb = new GaRoDataType("DimmerOnOffFb",
			BooleanResource.class);
	public static final GaRoDataType DimmerStateControl = new GaRoDataType("DimmerStateControl",
			BooleanResource.class);
	public static final GaRoDataType DimmerStateFb = new GaRoDataType("DimmerStateFb",
			BooleanResource.class);

	
	public static final GaRoDataType DevicePowerControl = new GaRoDataType("DevicePowerControl",
			PowerResource.class);
	public static final GaRoDataType DeviceReactivePowerControl = new GaRoDataType("DeviceReactivePowerControl",
			PowerResource.class);

	public static final GaRoDataType PluggedInStatus = new GaRoDataType("PluggedInStatus",
			BooleanResource.class);
	public static final GaRoDataType ChargingStatus = new GaRoDataType("ChargingStatus",
			BooleanResource.class);

	public static final GaRoDataType Heatpower = new GaRoDataType("Heatpower",
			PowerResource.class);
	public static final GaRoDataType HeatEnergyIntegral = new GaRoDataType("HeatEnergyIntegral",
			EnergyResource.class);
	@Deprecated //use VolumenFlow instead
	public static final GaRoDataType HeatFlow = new GaRoDataType("VolumeFlow",
			FlowResource.class);
	public static final GaRoDataType VolumeFlow = new GaRoDataType("VolumeFlow",
			FlowResource.class);
	@Deprecated //use VolumeIntegral instead
	public static final GaRoDataType HeatVolumeIntegral = new GaRoDataType("HeatVolumeIntegral",
			VolumeResource.class);
	public static final GaRoDataType VolumeIntegral = new GaRoDataType("VolumeIntegral",
			VolumeResource.class);
	public static final GaRoDataType HeatSupplyTemperatur = new GaRoDataType("HeatSupplyTemperatur",
			TemperatureResource.class);
	public static final GaRoDataType HeatReturnTemperatur = new GaRoDataType("HeatReturnTemperatur",
			TemperatureResource.class);
	public static final GaRoDataType HeatFlowTemperatur = new GaRoDataType("HeatFlowTemperatur",
			TemperatureResource.class);
	public static final GaRoDataType HeatFlowInLpmin = new GaRoDataType("HeatFlowInLpmin",
			FlowResource.class);
	public static final GaRoDataType HeatFlowPressure = new GaRoDataType("HeatFlowPressure",
			FlowResource.class);	
	public static final GaRoDataType StorageTemperature = new GaRoDataType("StorageTemperature",
			TemperatureResource.class);
	
	public static final GaRoDataType HeatCostAllocatorPoints = new GaRoDataType("HeatCostAllocatorPoints",
			FloatResource.class);
	public static final GaRoDataType LocationGenericFactor = new GaRoDataType("LocationFactor",
			FloatResource.class);
	public static final GaRoDataType HeatpumpCOP = new GaRoDataType("COP",
			FloatResource.class);

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
			ConcentrationResource.class);
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
	public static final GaRoDataType ErrorCode = new GaRoDataType("ErrorCode",
			IntegerResource.class);
	public static final GaRoDataType ErrorStatus = new GaRoDataType("ErrorStatus",
			BooleanResource.class);
	public static final GaRoDataType ErrorStatusFluid = new GaRoDataType("ErrorStatusFluid",
			BooleanResource.class);
	public static final GaRoDataType OperatingStatus = new GaRoDataType("OperatingStatus",
			BooleanResource.class);
	public static final GaRoDataType ConfigPending = new GaRoDataType("ConfigPending",
			BooleanResource.class);
	public static final GaRoDataType ManuMode = new GaRoDataType("ManuMode",
			IntegerResource.class);
	public static final GaRoDataType ManuModeFb = new GaRoDataType("ManuModeFb",
			IntegerResource.class);
	
	public static final GaRoDataType ShutterStopCt = new GaRoDataType("ShutterStopCt",
			BooleanResource.class);
	public static final GaRoDataType ShutterStopFb = new GaRoDataType("ShutterStopFb",
			BooleanResource.class);
	public static final GaRoDataType ShutterUpDownCt = new GaRoDataType("ShutterUpDownCt",
			BooleanResource.class);
	public static final GaRoDataType ShutterUpDownFb = new GaRoDataType("ShutterUpDownFb",
			BooleanResource.class);
	public static final GaRoDataType ShutterBottomPosition = new GaRoDataType("ShutterBottom",
			BooleanResource.class);
	public static final GaRoDataType ShutterTopPosition = new GaRoDataType("ShutterTop",
			BooleanResource.class);
	public static final GaRoDataType ShutterPosition = new GaRoDataType("ShutterPosition",
			FloatResource.class);
	public static final GaRoDataType Occupancy = new GaRoDataType("Occupancy",
			BooleanResource.class);	
	

	public static final GaRoDataType ControllerOpenFiles = new GaRoDataType("OpenFiles",
			FloatResource.class);
	public static final GaRoDataType ConnectedStatus = new GaRoDataType("ConnectedStatus",
			BooleanResource.class);
	public static final GaRoDataType DutyCycle = new GaRoDataType("DutyCycle",
			FloatResource.class);
	public static final GaRoDataType DutyCycleLevel = new GaRoDataType("DutyCycleLevel",
			FloatResource.class);
	public static final GaRoDataType CarrierSensLevel = new GaRoDataType("CarrierSensLevel",
			FloatResource.class);
	
	public static final GaRoDataType SetpointPerHour = new GaRoDataType("SetpointPerHour",
			FloatResource.class);
	public static final GaRoDataType SetpointPerHourConditional = new GaRoDataType("SetpointPerHourConditional",
			FloatResource.class);
	public static final GaRoDataType SetpointPerHourCondDrop = new GaRoDataType("SetpointPerHourCondDrop",
			FloatResource.class);
	public static final GaRoDataType SetpointPerHourPriority = new GaRoDataType("SetpointPerHourPriority",
			FloatResource.class);
	public static final GaRoDataType SetpointPerHourPrioDrop = new GaRoDataType("SetpointPerHourPrioDrop",
			FloatResource.class);
	public static final GaRoDataType SetpointResendMissingFbPerHour = new GaRoDataType("SetpointResendMissingFbPerHour",
			FloatResource.class);
	public static final GaRoDataType DutyCycleEff = new GaRoDataType("DutyCycleEff",
			FloatResource.class);
	public static final GaRoDataType RouterLoadEff = new GaRoDataType("RouterLoadEff",
			FloatResource.class);
	public static final GaRoDataType RouterInstallationModeCt = new GaRoDataType("InstallationModeCt",
			BooleanResource.class);
	public static final GaRoDataType RouterInstallationModeFb = new GaRoDataType("InstallationModeFb",
			BooleanResource.class);
	
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
	public static final GaRoDataType SystemRestartsLast2h = new GaRoDataType("SystemRestartsLast2h",
			IntegerResource.class);
	public static final GaRoDataType LogFileCheckNotification = new GaRoDataType("LogFileCheck",
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
	public static final GaRoDataType EnergyMonthly = new GaRoDataType("EnergyMonthly",
			EnergyResource.class);
	public static final GaRoDataType EnergyYearly = new GaRoDataType("EnergyYearly",
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

	public static final GaRoDataType EnergySum15min = new GaRoDataType("EnergySum15min",
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
	public static final GaRoDataType MessageInterval = new GaRoDataType("MessageInterval",
			IntegerResource.class);
	
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
	public static final GaRoDataType MemoryTsDebug = new GaRoDataType("PST",
			FloatResource.class);
	public static final GaRoDataType EcoMode = new GaRoDataType("EcoMode",
			BooleanResource.class);
	public static final GaRoDataType SeasonMode = new GaRoDataType("SeasonMode",
			IntegerResource.class);
	public static final GaRoDataType UsedDiskSpace = new GaRoDataType("UsedDiskSpace",
			FloatResource.class);
	public static final GaRoDataType JavaLoad = new GaRoDataType("JavaLoad",
			FloatResource.class);
	public static final GaRoDataType SystemLoad = new GaRoDataType("SystemLoad",
			FloatResource.class);
	public static final GaRoDataType FaultMessage = new GaRoDataType("Fault",
			BooleanResource.class);
	
	public static final GaRoDataType TemperatureGradient = new GaRoDataType("TemperatureGradient",
			FloatResource.class);
	public static final GaRoDataType TemperatureDifference = new GaRoDataType("TemperatureDifference",
			FloatResource.class);

	//The following options are per-gateway
	public static final GaRoDataType PowerMeter = new GaRoDataType("PowerMeter",
			PowerResource.class, Level.GATEWAY);
	public static final GaRoDataType PowerMeterEnergy = new GaRoDataType("PowerMeterEnergy",
			EnergyResource.class, Level.GATEWAY);
	/** For meters mainly focussing on consumption a separate field for export may exist*/
	public static final GaRoDataType PowerMeterEnergyExported = new GaRoDataType("PowerMeterEnergyExported",
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
	public static final GaRoDataType OutsideTemperatureExt = new GaRoDataType("OutsideTemperatureExt",
			TemperatureResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType OutsideHumidityExt = new GaRoDataType("OutsideHumiditiyExt",
			FloatResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType SolarIrradiationExt= new GaRoDataType("SolarIrradiationExt",
			EnergyPerAreaResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	
	public static final GaRoDataType WindSpeedExt = new GaRoDataType("WindSpeedExt",
			VelocityResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType WindDirectionExt = new GaRoDataType("WindDirectionExt",
			AngleResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType CloudCoverageExt = new GaRoDataType("CloudCoverageExt",
			FloatResource.class, Level.GATEWAY) {
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
			FloatResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType SolarIrradiationPerForcecast= new GaRoDataType("SolarIrradiationPerForcecast",
			EnergyPerAreaResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	
	public static final GaRoDataType SolarIrradiation = new GaRoDataType("SolarIrradiation",
			EnergyPerAreaResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType WindSpeedPerForcecast= new GaRoDataType("WindSpeed",
			VelocityResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType WindDirectionPerForcecast= new GaRoDataType("WindDirection",
			AngleResource.class, Level.GATEWAY) {
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}		
	};
	public static final GaRoDataType CloudCoverageForecast = new GaRoDataType("CloudCoverage",
			FloatResource.class, Level.GATEWAY) {
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
	
	public static final GaRoDataType GAP = new GaRoDataType("GapEvaluation",
			FloatResource.class);

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
			OutsideTemperatureExt,
			OutsideTemperatureOverall, Unknown, Any, LowLevel, PreEvaluated, OncePerGateway};
	
	public static GaRoDataType[] standardEvalTypes = new GaRoDataType[] {
			TemperatureMeasurementRoomSensor, TemperatureMeasurementThermostat,
			TemperatureSetpoint, TemperatureSetpointFeedback, TemperatureSetpointSet, ValvePosition,
			HumidityMeasurement, MotionDetection, WindowOpen, ChargeSensor, ChargeVoltage,
			PowerMeter, PowerMeterSubphase, PowerMeterOutlet,
			PowerMeterEnergy, PowerMeterEnergySubphase,
			Heatpower, HeatEnergyIntegral, VolumeFlow, VolumeIntegral, HeatSupplyTemperatur, HeatReturnTemperatur,
			SwitchStateFeedback, WaterPHValue, WaterConductivityValue, WaterRedoxValue, WaterOxygenConcentrationValue,
			WaterTemperatureValue,
			FreshWaterFlow, FreshWaterVolume,
			CO2Concentration, InternetConnection, RSSIDevice, RSSIPeer,
			ChargeVoltage};
	
	public static List<GaRoDataType> volumeTypes = Arrays.asList(new GaRoDataType[] {
			PowerMeterEnergy, PowerMeterEnergySubphase, EnergyIntegralOutlet,
			HeatEnergyIntegral, VolumeIntegral,
			FreshWaterVolume
	});
	public static List<GaRoDataType> volumeStepTypes = Arrays.asList(new GaRoDataType[] {
			FreshWater, FoodAmount
	});
	public static List<GaRoDataType> powerTypes = Arrays.asList(new GaRoDataType[] {
			PowerMeter, PowerMeterSubphase, PowerMeterOutlet,
			Heatpower, VolumeFlow,
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
	
	public static String getUnitString(Class<? extends PhysicalUnitResource> type, boolean isChart) {
		if(TemperatureResource.class.isAssignableFrom(type)) {
			if(isChart)
				return "°C";
			else
				return "K";
		}
		if(EnergyResource.class.isAssignableFrom(type))
			return "kWh";
		if(PowerResource.class.isAssignableFrom(type))
			return "W";
		if(VolumeResource.class.isAssignableFrom(type))
			return "m3";
		if(FlowResource.class.isAssignableFrom(type))
			return "m3/s";
		if(EnergyPerAreaResource.class.isAssignableFrom(type))
			return "W/m2";
		if(VelocityResource.class.isAssignableFrom(type))
			return "m/s";
		if(VoltageResource.class.isAssignableFrom(type))
			return "V";
		if(ElectricCurrentResource.class.isAssignableFrom(type))
			return "A";
		if(FrequencyResource.class.isAssignableFrom(type))
			return "Hz";
		if(ConcentrationResource.class.isAssignableFrom(type))
			return "ppm";
		return null;
	}
}
