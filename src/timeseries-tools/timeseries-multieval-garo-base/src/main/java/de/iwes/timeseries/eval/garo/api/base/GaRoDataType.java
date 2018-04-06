package de.iwes.timeseries.eval.garo.api.base;

public enum GaRoDataType {
	TemperatureMeasurementRoomSensor,
	TemperatureMeasurementThermostat,
	TemperatureSetpoint,
	//subset of TemperatureSetpoint
	TemperatureSetpointFeedback,
	//subset of TemperatureSetpoint
	TemperatureSetpointSet,
	ValvePosition,
	HumidityMeasurement,
	PowerMeter,
	MotionDetection,
	WindowOpen,
	CompetitionLevel,
	CompetitionPosition,
	CompetitionPoints,
	ChargeSensor,
	Unknown,
	Any,
	LowLevel, 
}
