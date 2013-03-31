package com.homeki.core.main;

import java.util.ArrayList;
import java.util.List;

import com.homeki.core.device.mock.MockModule;
import com.homeki.core.device.onewire.OneWireModule;
import com.homeki.core.device.tellstick.TellStickModule;
import com.homeki.core.events.ChannelValueChangedEvent;
import com.homeki.core.events.EventHandlerModule;
import com.homeki.core.generators.GeneratorModule;
import com.homeki.core.http.RestApiModule;
import com.homeki.core.logging.L;
import com.homeki.core.report.ReportModule;
import com.homeki.core.storage.DatabaseManager;
import com.homeki.core.storage.Hibernate;

public class Homeki {
	private List<Module> modules;
	
	public Homeki() {
		modules = new ArrayList<Module>();
		addShutdownHook();
	}
	
	private void addShutdownHook() {
		Runtime rt = Runtime.getRuntime();
		rt.addShutdownHook(new Thread() {
			public void run() {
				Thread.currentThread().setName("ShutdownHook");
				shutdown();
				L.i("Homeki version " + Util.getVersion() + " exited.");
			};
		});
	}
	
	public void launch() {
		Thread.currentThread().setName("Homeki");
		
		L.i("Homeki version " + Util.getVersion() + " started.");
		
		// perform, if necessary, database upgrades
		try {
			new DatabaseManager().upgrade();
		} catch (ClassNotFoundException e) {
			L.e("Failed to load Postgres JDBC driver, killing Homeki.", e);
			System.exit(-1);
		} catch (Exception e) {
			L.e("Database upgrade failed, killing Homeki.", e);
			System.exit(-1);
		}
		L.i("Database version up to date.");
		
		// init Hibernate functionality
		try {
			Hibernate.init();
		} catch (Exception e) {
			L.e("Something went wrong when verifying access to database through Hibernate, killing Homeki.", e);
			L.e(e.getStackTrace().toString());
			System.exit(-1);
		}
		L.i("Database access through Hibernate verified.");
		
		// setup and construct modules
		modules.add(new ReportModule());
		modules.add(new EventHandlerModule());
		modules.add(new MockModule());
		modules.add(new TellStickModule());
		modules.add(new OneWireModule());
		modules.add(new RestApiModule());
		modules.add(new WebGuiModule());
		modules.add(new BroadcastModule());
		modules.add(new GeneratorModule());
		
		for (Module module : modules) {
			try {
				L.i("Constructing module " + module.getClass().getSimpleName() + ".");
				module.construct();
			} catch (Exception e) {
				L.e("Failed to construct " + module.getClass().getSimpleName() + ".", e);
			}
		}
		L.i("Modules constructed.");
		
		// generate channel value changed event once, for init
		ChannelValueChangedEvent.generateOnce();
	}
	
	public void shutdown() {
		for (Module m : modules) {
			L.i("Destructing module " + m.getClass().getSimpleName() + ".");
			m.destruct();
		}
		L.i("Modules destructed");
	}
}
