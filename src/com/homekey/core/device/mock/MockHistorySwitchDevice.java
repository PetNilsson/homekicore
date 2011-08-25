package com.homekey.core.device.mock;

import java.util.Date;

import com.homekey.core.Logs;
import com.homekey.core.device.Queryable;
import com.homekey.core.device.Switchable;
import com.homekey.core.log.L;
import com.homekey.core.storage.IHistoryTable;
import com.homekey.core.storage.ITableFactory;

public class MockHistorySwitchDevice extends MockDevice implements Switchable, Queryable<Boolean> {
	private IHistoryTable historyTable;
	
	public MockHistorySwitchDevice(String internalId, ITableFactory factory) {
		super(internalId, factory);
	}
	
	@Override
	public void off() {
		historyTable.putValue(new Date(), false);
		L.getLogger(Logs.CORE_MOCK).log("MockSwitchDevice '" + getInternalId() + "' is now OFF!");
	}
	
	@Override
	public void on() {
		historyTable.putValue(new Date(), true);
		L.getLogger(Logs.CORE_MOCK).log("MockSwitchDevice '" + getInternalId() + "' is now ON!");
	}
	
	@Override
	public Boolean getValue() {
		return (Boolean)historyTable.getLatestValue();
	}
	
	@Override
	protected void ensureHistoryTable(ITableFactory factory, String tableName) {
		historyTable = factory.getHistoryTable(tableName, Boolean.class);
		historyTable.ensureTable();
	}
}
