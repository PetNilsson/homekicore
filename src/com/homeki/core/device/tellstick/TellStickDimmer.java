package com.homeki.core.device.tellstick;

import java.util.Date;

import javax.persistence.Entity;

import com.homeki.core.device.IntegerHistoryPoint;
import com.homeki.core.device.abilities.Settable;
import com.homeki.core.device.abilities.Triggable;
import com.homeki.core.main.L;

@Entity
public class TellStickDimmer extends TellStickDevice implements Settable, Triggable, TellStickLearnable {
	public static final int TELLSTICKDIMMER_ONOFF_CHANNEL = 0;
	public static final int TELLSTICKDIMMER_LEVEL_CHANNEL = 1;
	
	public TellStickDimmer() {
		
	}
	
	public TellStickDimmer(int defaultLevel) {
		addOnOffHistoryPoint(false);
		addLevelHistoryPoint(defaultLevel);
	}
	
	public TellStickDimmer(int defaultLevel, int house, int unit) {
		this(defaultLevel);
		
		int result = TellStickNative.addDimmer(house, unit);
		
		this.internalId = String.valueOf(result);
	}
	
	@Override
	public void trigger(int newValue) {
		L.i("TellStickDimmer with internal ID'" + getInternalId() + "' triggered with newValue " + newValue + ".");

		if (newValue > 0) {
			set(TELLSTICKDIMMER_LEVEL_CHANNEL, newValue);
		} else {
			set(TELLSTICKDIMMER_ONOFF_CHANNEL, 0);
		}
	}

	@Override
	public void set(int channel, int value) {
		int internalId = Integer.parseInt(getInternalId());
		IntegerHistoryPoint level = (IntegerHistoryPoint)getLatestHistoryPoint(TELLSTICKDIMMER_LEVEL_CHANNEL);
		
		if (channel == TELLSTICKDIMMER_ONOFF_CHANNEL) {
			boolean on = value > 0;
			if (on && level != null) {
				TellStickNative.dim(internalId, level.getValue());
				addOnOffHistoryPoint(true);
			} else if (on) {
				L.e("Level is null. Level being null when setting TellStickDimmer should not occur more than once, and only after recent upgrade. If this message haven't been seen in a while, remove the check for null in code. Added a history point for this device now.");
				TellStickNative.dim(internalId, 255);
			} else {
				TellStickNative.turnOff(internalId);
			}
		} else if (channel == TELLSTICKDIMMER_LEVEL_CHANNEL) {
			TellStickNative.dim(internalId, value);
		} else {
			throw new RuntimeException("Tried to set invalid channel " + channel + " on TellStickDimmer '" + getInternalId() + "'.");
		}
	}

	@Override
	public String getType() {
		return "dimmer";
	}
	
	public void addOnOffHistoryPoint(boolean on) {
		IntegerHistoryPoint dhp = new IntegerHistoryPoint();
		dhp.setDevice(this);
		dhp.setRegistered(new Date());
		dhp.setChannel(TELLSTICKDIMMER_ONOFF_CHANNEL);
		dhp.setValue(on ? 1 : 0);
		historyPoints.add(dhp);
	}
	
	public void addLevelHistoryPoint(int level) {
		IntegerHistoryPoint dhp = new IntegerHistoryPoint();
		dhp.setDevice(this);
		dhp.setRegistered(new Date());
		dhp.setChannel(TELLSTICKDIMMER_LEVEL_CHANNEL);
		dhp.setValue(level);
		historyPoints.add(dhp);
	}

	@Override
	public void learn() {
		TellStickNative.learn(Integer.valueOf(internalId));
	}
	
	@Override
	public void preDelete() {
		TellStickNative.removeDevice(Integer.valueOf(internalId));
	}
}
