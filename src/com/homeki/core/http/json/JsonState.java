package com.homeki.core.http.json;

import org.hibernate.Session;

import com.homeki.core.device.Device;
import com.homeki.core.device.mock.MockDimmer;
import com.homeki.core.device.tellstick.TellStickDimmer;

public class JsonState {
	public static JsonState create(Session ses, Device d) {
		if (d instanceof TellStickDimmer)
			return new TellStickDimmerJsonState(ses, (TellStickDimmer)d);
		else if (d instanceof MockDimmer)
			return new MockDimmerJsonState(ses, (MockDimmer)d);
		else
			return new ObjectJsonState(ses, d);
	}
}
