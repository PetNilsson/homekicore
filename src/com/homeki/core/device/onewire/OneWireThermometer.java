package com.homeki.core.device.onewire;

import java.util.Date;
import java.util.List;

import com.homeki.core.device.abilities.IntervalLoggable;
import com.homeki.core.storage.Hibernate;
import com.homeki.core.storage.HistoryPoint;
import com.homeki.core.storage.entities.TemperatureHistoryPoint;

public class OneWireThermometer extends OneWireDevice implements IntervalLoggable<Double> {
	public OneWireThermometer(String internalId, String deviceDirPath) {
		super(deviceDirPath);
	}

	@Override
	public Double getValue() {
		return getDoubleVar("Thermometer");
	}

	@Override
	public void updateValue() {
		double value = getDoubleVar("Thermometer");
		//setValue(value);
	}

	@Override
	public List<HistoryPoint> getHistory(Date from, Date to) {
		return null;
	}

	@Override
	public String getOuterType() {
		return "thermometer";
	}
}
