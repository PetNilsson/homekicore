package com.homeki.core.report;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.NoSuchElementException;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.restlet.resource.ClientResource;

import com.homeki.core.device.Device;
import com.homeki.core.device.HistoryPoint;
import com.homeki.core.logging.L;
import com.homeki.core.main.Configuration;
import com.homeki.core.main.ControlledThread;
import com.homeki.core.main.Setting;
import com.homeki.core.main.Util;
import com.homeki.core.storage.Hibernate;

public class ReportThread extends ControlledThread {
	private String macAddress;
	
	public ReportThread() {
		super(Configuration.REPORTER_INTERVAL);
		this.macAddress = getMacAddress();
	}

	@Override
	protected void iteration() {
		ClientResource cr = new ClientResource(Configuration.REPORTER_URL);
		cr.setRequestEntityBuffering(true);
		InstanceResource resource = cr.wrap(InstanceResource.class);
		
		Session session = Hibernate.openSession();
		
		int deviceCount = ((Number)session.createCriteria(Device.class).setProjection(Projections.rowCount()).uniqueResult()).intValue();
		long historyPointCount = ((Number)session.createCriteria(HistoryPoint.class).setProjection(Projections.rowCount()).uniqueResult()).longValue();
		String serverName = Setting.getString(session, Setting.SERVER_NAME_KEY);
		
		Report report = new Report();
		report.setMacAddress(macAddress);
		report.setVersion(Util.getVersion());
		report.setServerName(serverName);
		report.setDeviceCount(deviceCount);
		report.setHistoryPointRowCount(historyPointCount);
		
		Hibernate.closeSession(session);
		
		try {
			resource.store(report);
			L.i("Instance status was successfully reported.");
		} catch (Exception e) {
			L.w("Failed to report instance status.");
		}
	}
	
	private String getMacAddress() {
		try {
			NetworkInterface ni = NetworkInterface.getByName("eth0");
			
			if (ni == null)
				throw new NoSuchElementException();
			
			StringBuilder sb = new StringBuilder();
			for(byte b : ni.getHardwareAddress())
				sb.append(String.format("%02x:", b&0xff));
			sb.deleteCharAt(sb.length()-1);
			
			return sb.toString();
		} catch (SocketException e) {
			throw new NoSuchElementException();
		}
	}
}
