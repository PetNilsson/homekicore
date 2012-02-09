package com.homeki.core.device.mock;

import org.hibernate.Session;

import com.homeki.core.device.Device;
import com.homeki.core.main.Module;
import com.homeki.core.storage.Hibernate;

public class MockModule implements Module {
	@Override
	public void construct() {
		Session session = Hibernate.openSession();
		
		addMockDevice(session, "switch1", new MockSwitch(false));
		addMockDevice(session, "switch2", new MockSwitch(false));
		addMockDevice(session, "dimmer1", new MockDimmer(0));
		addMockDevice(session, "temp1", new MockThermometer(0.0));
		addMockDevice(session, "temp2", new MockThermometer(0.0));
		
		Hibernate.closeSession(session);
	}
	
	private void addMockDevice(Session session, String internalId, Device newdev) {
		Device dev = Device.getByInternalId(session, internalId);
		
		if (dev == null) {
			newdev.setInternalId(internalId);
			session.save(newdev);
		}
	}
	
	@Override
	public void destruct() {
		
	}
}
