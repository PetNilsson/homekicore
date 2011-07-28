package com.homekey.core.device.mock;

import java.util.Date;

import com.homekey.core.device.Device;
import com.homekey.core.device.Queryable;
import com.homekey.core.device.Switchable;
import com.homekey.core.storage.ColumnType;
import com.homekey.core.storage.DatabaseTable;

public class MockDeviceSwitcher extends Device implements Switchable, Queryable<Boolean> {
	private boolean talk;
	private boolean on;
	
	public MockDeviceSwitcher(String internalId, boolean talk) {
		super(internalId);
		setValue(false);
		this.talk = talk;
		if (talk)
			System.out.println("MockInfo: Created MockDeviceSwitcher called '" + getName() + "'.");
	}
	
	@Override
	public boolean off() {
		setValue(false);
		if (talk)
			System.out.println("MockInfo: MockDeviceSwitcher called '" + getName() + "' is now OFF!");
		return true;
	}
	
	@Override
	public boolean on() {
		setValue(true);
		if (talk)
			System.out.println("MockInfo: MockDeviceSwitcher called '" + getName() + "' is now ON!");
		return true;
	}
	
	@Override
	public Boolean getValue() {
		return on;
	}
	
	@Override
	public DatabaseTable getTableDesign() {
		DatabaseTable table = new DatabaseTable(2);
		table.setColumn(0, "registered", ColumnType.DateTime);
		table.setColumn(1, "value", ColumnType.Boolean);
		return table;
	}

	@Override
	public Object[] getDataRow() {
		return new Object[] { new Date(), getValue() };
	}

	@Override
	public void setValue(Boolean value) {
		on = value;
	}
}
