package com.homeki.core.device.mock;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import com.homeki.core.device.abilities.Queryable;
import com.homeki.core.device.abilities.Switchable;
import com.homeki.core.main.L;
import com.homeki.core.storage.DatumPoint;
import com.homeki.core.storage.Hibernate;
import com.homeki.core.storage.entities.HDevice;
import com.homeki.core.storage.entities.HSwitchHistory;

public class MockSwitch extends MockDevice implements Switchable, Queryable<Boolean> {
	public MockSwitch(String internalId) {
		super(internalId);
	}
	
	@Override
	public void off() {
		L.i("MockSwitchDevice '" + getInternalId() + "' is now OFF.");
		Session session = Hibernate.openSession();
		HDevice dev = (HDevice)session.load(HDevice.class, id);
		HSwitchHistory value = new HSwitchHistory();
		value.setRegistered(new Date());
		value.setDevice(dev);
		value.setValue(false);
		session.save(value);
		Hibernate.closeSession(session);
	}
	
	@Override
	public void on() {
		L.i("MockSwitchDevice '" + getInternalId() + "' is now ON.");
		Session session = Hibernate.openSession();
		HDevice dev = (HDevice)session.load(HDevice.class, id);
		HSwitchHistory value = new HSwitchHistory();
		value.setRegistered(new Date());
		value.setDevice(dev);
		value.setValue(true);
		session.save(value);
		Hibernate.closeSession(session);
	}
	
	@Override
	public Boolean getValue() {
		Session session = Hibernate.openSession();
		Boolean value = (Boolean)session.createQuery("select value from HSwitchHistory as his where his.device = ? order by his.registered desc")
				.setInteger(0, id)
				.setMaxResults(1)
				.uniqueResult();
		Hibernate.closeSession(session);
		
		if (value == null)
			value = false;
		
		return value;
	}
	
	@Override
	public List<DatumPoint> getHistory(Date from, Date to) {
		return null;
		//return historyTable.getValues(from, to);
	}

	@Override
	protected Type getTableValueType() {
		return Boolean.class;
	}

	@Override
	public String getType() {
		return "switch";
	}
}
