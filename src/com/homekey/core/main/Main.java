package com.homekey.core.main;

import com.homekey.core.command.CommandsThread;
import com.homekey.core.command.DimDeviceCommand;
import com.homekey.core.command.SwitchDeviceCommand;
import com.homekey.core.device.Device;
import com.homekey.core.device.Dimmable;
import com.homekey.core.device.Switchable;
import com.homekey.core.device.mock.MockDeviceDimmer;
import com.homekey.core.http.HttpListenerThread;
import com.homekey.core.device.mock.MockDeviceSwitcher;
import com.homekey.core.storage.Database;

public class Main {
	public static void main(String[] args) {
		
		Database b = new Database();
		
		CommandsThread ct = new CommandsThread();
		Monitor m = new Monitor(ct);
		
		new HttpListenerThread(m).start();
		ct.start();
		
		m.setServerName("Fresh Server");
		
		System.out.println("Starting server '" + m.getServerName() + "'");
		
		DoSomeTesting(m, ct, b);
		
		b.close();
	}
	
	private static void DoSomeTesting(Monitor m, CommandsThread ct, Database b) {
		// Create devices
		Device dev1 = new MockDeviceSwitcher("DA", "My MockDevice #1", true);
		Device dev2 = new MockDeviceDimmer("DDD", "My MockDevice #2", true);
		// Register devices with db
		b.ensureDevice(dev1);
		b.ensureDevice(dev2);
		// Add devices
		m.forceAddDevice(dev1);
		m.forceAddDevice(dev2);
		// Query devices by ID
		Switchable switchable = (Switchable) m.getDevice(dev1.getId());
		Dimmable dimmable = (Dimmable) m.getDevice(dev2.getId());
		// Create commands
		DimDeviceCommand ddc = new DimDeviceCommand(dimmable, 50);
		SwitchDeviceCommand sdcOn = new SwitchDeviceCommand(switchable, true);
		SwitchDeviceCommand sdcOff = new SwitchDeviceCommand(switchable, false);
		// Post commands
		ct.post(sdcOn);
		ct.post(ddc);
		ct.post(sdcOff);
		
		sdcOff.getResult();
		ddc.getResult();
		sdcOn.getResult();
	}
}
