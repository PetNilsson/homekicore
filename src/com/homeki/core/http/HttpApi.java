package com.homeki.core.http;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.homeki.core.device.Device;
import com.homeki.core.device.abilities.Dimmable;
import com.homeki.core.device.abilities.Switchable;
import com.homeki.core.http.json.JsonDevice;
import com.homeki.core.http.json.JsonState;
import com.homeki.core.main.Monitor;

public class HttpApi {
	private Gson gson;
	private Monitor monitor;

	public HttpApi(Monitor monitor) {
		this.gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		this.monitor = monitor;
	}

	public String getDevices() {
		return gson.toJson(JsonDevice.convertList(monitor.getDevices()));
	}

	public void switchOn(int id) {
		Device d = monitor.getDevice(id);
		Switchable s = (Switchable) d;
		s.on();
	}

	public void switchOff(int id) {
		Device d = monitor.getDevice(id);
		Switchable s = (Switchable) d;
		s.off();
	}

	public void dim(int id, int level) {
		Device dev = monitor.getDevice(id);
		Dimmable d = (Dimmable) dev;
		d.dim(level);
	}

	public String getHistory(int id, Date from, Date to) {
		Device dev = monitor.getDevice(id);
		//Queryable<?> q = (Queryable<?>)dev;
		//List<HistoryPoint> points = q.getHistory(from, to);
		//return gson.toJson(JsonPair.convertList(points));
		return "";
	}

	public String getStatus(int id) {
		//Device d = monitor.getDevice(id);
		//Queryable<?> q = (Queryable<?>) d;
		JsonState status = new JsonState(null);
		return gson.toJson(status);
	}
	
	public void setDevice(int id, String gsonString) {
		Device d = monitor.getDevice(id);
		JsonDevice gsond = gson.fromJson(gsonString, JsonDevice.class);
		
		if (gsond.name != null)
			d.setName(gsond.name);
	}
}
