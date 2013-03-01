package com.homeki.core.http;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.homeki.core.http.restlets.device.DeviceDeleteRestlet;
import com.homeki.core.http.restlets.device.DeviceGetRestlet;
import com.homeki.core.http.restlets.device.DeviceListRestlet;
import com.homeki.core.http.restlets.device.DeviceMergeRestlet;
import com.homeki.core.http.restlets.device.DeviceSetRestlet;
import com.homeki.core.http.restlets.device.channel.DeviceChannelListRestlet;
import com.homeki.core.http.restlets.device.channel.DeviceChannelValueListRestlet;
import com.homeki.core.http.restlets.device.channel.DeviceChannelValueSetRestlet;
import com.homeki.core.http.restlets.device.mock.DeviceMockAddRestlet;
import com.homeki.core.http.restlets.device.tellstick.DeviceTellStickAddRestlet;
import com.homeki.core.http.restlets.device.tellstick.DeviceTellStickLearnRestlet;
import com.homeki.core.http.restlets.server.ServerGetRestlet;
import com.homeki.core.http.restlets.server.ServerSetRestlet;
import com.homeki.core.http.restlets.trigger.TriggerAddRestlet;
import com.homeki.core.http.restlets.trigger.TriggerDeleteRestlet;
import com.homeki.core.http.restlets.trigger.TriggerListRestlet;
import com.homeki.core.http.restlets.trigger.TriggerSetRestlet;
import com.homeki.core.http.restlets.trigger.action.TriggerActionAddRestlet;
import com.homeki.core.http.restlets.trigger.action.TriggerActionDeleteRestlet;
import com.homeki.core.http.restlets.trigger.action.TriggerActionGetRestlet;
import com.homeki.core.http.restlets.trigger.action.TriggerActionListRestlet;
import com.homeki.core.http.restlets.trigger.action.TriggerActionSetRestlet;
import com.homeki.core.http.restlets.trigger.condition.TriggerConditionAddRestlet;
import com.homeki.core.http.restlets.trigger.condition.TriggerConditionDeleteRestlet;
import com.homeki.core.http.restlets.trigger.condition.TriggerConditionGetRestlet;
import com.homeki.core.http.restlets.trigger.condition.TriggerConditionListRestlet;
import com.homeki.core.http.restlets.trigger.condition.TriggerConditionSetRestlet;

public class JsonRestletApplication extends Application {
	@Override
	public Restlet createInboundRoot() {
		Router r = new Router(getContext().createChildContext());
		
		r.attach("/device/list", new DeviceListRestlet());
		r.attach("/device/{deviceid}/get", new DeviceGetRestlet());
		r.attach("/device/{deviceid}/set", new DeviceSetRestlet());
		r.attach("/device/{deviceid}/merge", new DeviceMergeRestlet());
		r.attach("/device/{deviceid}/delete", new DeviceDeleteRestlet());
		
		r.attach("/device/mock/add", new DeviceMockAddRestlet());
		
		r.attach("/device/tellstick/add", new DeviceTellStickAddRestlet());
		r.attach("/device/{deviceid}/tellstick/learn", new DeviceTellStickLearnRestlet());
		
		r.attach("/device/{deviceid}/channel/list", new DeviceChannelListRestlet());
		r.attach("/device/{deviceid}/channel/{channelid}/list", new DeviceChannelValueListRestlet());
		r.attach("/device/{deviceid}/channel/{channelid}/set", new DeviceChannelValueSetRestlet());
		
		r.attach("/server/get", new ServerGetRestlet());
		r.attach("/server/set", new ServerSetRestlet());
		
		r.attach("/trigger/add", new TriggerAddRestlet());
		r.attach("/trigger/list", new TriggerListRestlet());
		r.attach("/trigger/{triggerid}/set", new TriggerSetRestlet());
		r.attach("/trigger/{triggerid}/delete", new TriggerDeleteRestlet());
		
		r.attach("/trigger/{triggerid}/condition/list", new TriggerConditionListRestlet());
		r.attach("/trigger/{triggerid}/condition/add", new TriggerConditionAddRestlet());
		r.attach("/trigger/{triggerid}/condition/{conditionid}/get", new TriggerConditionGetRestlet());
		r.attach("/trigger/{triggerid}/condition/{conditionid}/set", new TriggerConditionSetRestlet());
		r.attach("/trigger/{triggerid}/condition/{conditionid}/delete", new TriggerConditionDeleteRestlet());
		
		r.attach("/trigger/{triggerid}/action/list", new TriggerActionListRestlet());
		r.attach("/trigger/{triggerid}/action/add", new TriggerActionAddRestlet());
		r.attach("/trigger/{triggerid}/action/{actionid}/get", new TriggerActionGetRestlet());
		r.attach("/trigger/{triggerid}/action/{actionid}/set", new TriggerActionSetRestlet());
		r.attach("/trigger/{triggerid}/action/{actionid}/delete", new TriggerActionDeleteRestlet());
		
		return r;
	}
}
