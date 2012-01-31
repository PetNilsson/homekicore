package com.homeki.core.device.tellstick;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import com.homeki.core.device.Device;
import com.homeki.core.device.DimmerHistoryPoint;
import com.homeki.core.device.SwitchHistoryPoint;
import com.homeki.core.main.ControlledThread;
import com.homeki.core.main.L;
import com.homeki.core.storage.Hibernate;

public class TellStickDetector extends ControlledThread {
	private List<Integer> allowedSensorIds;
	
	public TellStickDetector(int interval, List<Integer> allowedSensorIds) {
		super(interval);
		this.allowedSensorIds = allowedSensorIds;
	}

	@Override
	protected void iteration() throws InterruptedException {
		int[] ids = TellStickNative.getDeviceIds();
		Session session = Hibernate.openSession();
		
		for (int i = 0; i < ids.length; i++) {
			int id = ids[i];
			String internalId = String.valueOf(id);
			Device dev = Device.getByInternalId(session, internalId);
			
			if (dev == null) {
				String type = TellStickNative.getDeviceType(id);
				
				if (type.equals("dimmer")) {
					dev = new TellStickDimmer();
					DimmerHistoryPoint dhp = new DimmerHistoryPoint();
					dhp.setDevice(dev);
					dhp.setRegistered(new Date());
					dhp.setValue(0);
					dev.getHistoryPoints().add(dhp);
				} else if (type.equals("switch")) {
					dev = new TellStickSwitch();
					SwitchHistoryPoint dhp = new SwitchHistoryPoint();
					dhp.setDevice(dev);
					dhp.setRegistered(new Date());
					dhp.setValue(false);
					dev.getHistoryPoints().add(dhp);
				} else {
					L.w("Found no corresponding device for TellStick device type " + type + ".");
					continue;
				}
				
				dev.setInternalId(internalId);
				session.save(dev);
			}
		}
		
		for (int id : allowedSensorIds) {
			String internalId = "s" + String.valueOf(id);
			Device dev = Device.getByInternalId(session, internalId);
			
			if (dev == null) {
				dev = new TellStickThermometer();
				dev.setInternalId(internalId);
				session.save(dev);
			}
		}
		
		Hibernate.closeSession(session);
	}
}
