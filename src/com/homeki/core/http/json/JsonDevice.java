package com.homeki.core.http.json;

import java.util.Date;
import java.util.List;

import com.homeki.core.device.Device;

public class JsonDevice {
	public String type;
	public Integer id;
	public String name;
	public Date added;
	public Boolean active;
	
	public JsonDevice(Device d){
		type = d.getClass().getSimpleName();
		id = d.getId();
		name = d.getName();
		added = d.getAdded();
		active = d.isActive();
	}

	public static JsonDevice[] convertList(List<Device> devices) {
		JsonDevice[] jsonDevices = new JsonDevice[devices.size()];
		for (int i = 0; i < jsonDevices.length; i++) {
			jsonDevices[i] = new JsonDevice(devices.get(i));
		}
		return jsonDevices;
	}
}
