package com.homekey.core.main;

import com.homekey.core.device.Device;
import com.homekey.core.device.Dimmable;
import com.homekey.core.device.Switchable;
import com.homekey.core.device.mock.MockDeviceDimmer;
import com.homekey.core.device.mock.MockDeviceSwitcher;
import com.homekey.core.storage.Database;

public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Database b = new Database();
		
		Monitor m = new Monitor();
		CommandsThread ct = new CommandsThread();
		ct.start();
		
		m.setServerName("Fresh Server");
		
		System.out.println("Starting server '" + m.getServerName() + "'");
		
		DoSomeTesting(m, ct,b);
		DoSomeMoreTesting(b);
		
		b.close();
	}

	private static void DoSomeTesting(Monitor m, CommandsThread ct, Database b) {
		// Create devices
		Device dev1 = new MockDeviceSwitcher(1, "My MockDevice #1", true);
		Device dev2 = new MockDeviceDimmer(2, "My MockDevice #2", true);
		// Add devices
		m.forceAddDevice(dev1);
		m.forceAddDevice(dev2);
		// Query devices by ID
		Switchable switchable = (Switchable)m.getDevice(dev1.getId());
		Dimmable dimmable = (Dimmable)m.getDevice(dev2.getId());
		// Create commands
		DimDeviceCommand ddc = new DimDeviceCommand(dimmable,50);
		SwitchDeviceCommand sdcOn = new SwitchDeviceCommand(switchable,true);
		SwitchDeviceCommand sdcOff = new SwitchDeviceCommand(switchable,false);
		// Post commands
		ct.post(sdcOn);
		ct.post(ddc);
		ct.post(sdcOff);
		
		sdcOff.getResult();
		ddc.getResult();
		sdcOn.getResult();
	}
	
	private static void DoSomeMoreTesting(Database b) {
		MockDeviceSwitcher sw = new MockDeviceSwitcher(2, "test", false);
		
		b.createTableFor(sw);
		
		
	}
}
