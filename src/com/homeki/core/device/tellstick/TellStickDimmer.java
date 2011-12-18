package com.homeki.core.device.tellstick;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import com.homeki.core.device.Device;
import com.homeki.core.device.abilities.Dimmable;
import com.homeki.core.device.abilities.Queryable;
import com.homeki.core.device.abilities.Switchable;
import com.homeki.core.storage.Hibernate;
import com.homeki.core.storage.HistoryPoint;
import com.homeki.core.storage.entities.HDevice;
import com.homeki.core.storage.entities.HDimmerHistoryPoint;

public class TellStickDimmer extends Device implements Dimmable, Switchable, Queryable<Integer> {
	public TellStickDimmer(String internalId) {
		super(internalId);
	}

	@Override
	public void dim(int level) {
		TellStickNative.dim(Integer.parseInt(getInternalId()), level);
		Session session = Hibernate.openSession();
		HDevice dev = (HDevice)session.load(HDevice.class, id);
		HDimmerHistoryPoint value = new HDimmerHistoryPoint();
		value.setRegistered(new Date());
		value.setDevice(dev);
		value.setValue(level);
		session.save(value);
		Hibernate.closeSession(session);
	}

	@Override
	public void off() {
		dim(0);
	}

	@Override
	public Integer getValue() {
		Session session = Hibernate.openSession();
		Integer value = (Integer)session.createQuery("select value from HDimmerHistoryPoint as his where his.device = ? order by his.registered desc")
				.setInteger(0, id)
				.setMaxResults(1)
				.uniqueResult();
		Hibernate.closeSession(session);
		
		if (value == null)
			value = 0;
		
		return value;
	}
	
	@Override
	public void on() {
		dim(255);
	}

	@Override
	public List<HistoryPoint> getHistory(Date from, Date to) {
		Session session = Hibernate.openSession();
		@SuppressWarnings("unchecked")
		List<HistoryPoint> list = session.createQuery("from HDimmerHistoryPoint as p where p.registered between ? and ? order by p.registered asc")
				.setDate(0, from)
				.setDate(1, to)
				.list();
		Hibernate.closeSession(session);
		return list;
	}

	@Override
	public String getType() {
		return "dimmer";
	}
}
