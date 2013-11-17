package com.homeki.core.device.tellstick;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import com.homeki.core.device.Channel;
import com.homeki.core.device.IntegerHistoryPoint;
import com.homeki.core.device.Settable;

@Entity
public class TellStickDimmer extends TellStickDevice implements Settable, TellStickLearnable {
	public static final int ONOFF_CHANNEL = 0;
	public static final int LEVEL_CHANNEL = 1;
	
	public TellStickDimmer() {
		
	}
	
	public TellStickDimmer(int defaultLevel) {
		addHistoryPoint(ONOFF_CHANNEL, 0);
		addHistoryPoint(LEVEL_CHANNEL, defaultLevel);
	}
	
	public TellStickDimmer(int defaultLevel, int house, int unit) {
		this(defaultLevel);
		
		int result = TellStickApi.INSTANCE.addDimmer(house, unit);
		
		this.internalId = String.valueOf(result);
	}

	@Override
	public void set(int channel, int value) {
		validateChannel(channel);
		
		int internalId = Integer.parseInt(getInternalId());
		IntegerHistoryPoint level = (IntegerHistoryPoint)getLatestHistoryPoint(LEVEL_CHANNEL);
		IntegerHistoryPoint onoff = (IntegerHistoryPoint)getLatestHistoryPoint(ONOFF_CHANNEL);
		
		if (channel == ONOFF_CHANNEL) {
			boolean on = value > 0;
			if (on) {
				TellStickApi.INSTANCE.dim(internalId, level.getValue());
				addHistoryPoint(ONOFF_CHANNEL, 1);
			} else {
				TellStickApi.INSTANCE.turnOff(internalId);
			}
		} else if (channel == LEVEL_CHANNEL) {
			if (onoff.getValue() > 0)
				TellStickApi.INSTANCE.dim(internalId, value);
			else
				addHistoryPoint(LEVEL_CHANNEL, value);
		}
	}

	@Override
	public String getType() {
		return "dimmer";
	}

	@Override
	public void learn() {
		TellStickApi.INSTANCE.learn(Integer.valueOf(internalId));
	}
	
	@Override
	public void preDelete() {
		TellStickApi.INSTANCE.removeDevice(Integer.valueOf(internalId));
	}
	
	@Override
	public String[] getAbilities() {
		return new String[] { "tellstick" };
	}

	@Override
	public List<Channel> getChannels() {
		List<Channel> list = new ArrayList<Channel>();
		list.add(new Channel(ONOFF_CHANNEL, "onoff", Channel.INT));
		list.add(new Channel(LEVEL_CHANNEL, "level", Channel.BYTE));
		return list;
	}
}