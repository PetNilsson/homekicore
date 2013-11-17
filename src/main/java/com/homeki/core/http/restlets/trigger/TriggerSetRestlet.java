package com.homeki.core.http.restlets.trigger;

import com.homeki.core.http.ApiException;
import com.homeki.core.http.Container;
import com.homeki.core.http.KiRestlet;
import com.homeki.core.http.json.JsonTrigger;
import com.homeki.core.main.Util;
import com.homeki.core.triggers.Trigger;

public class TriggerSetRestlet extends KiRestlet {
	@Override
	protected void handle(Container c) {
		int triggerId = getInt(c, "triggerid");
		
		Trigger trigger = (Trigger)c.ses.get(Trigger.class, triggerId);
		
		if (trigger == null)
			throw new ApiException("No trigger with the specified ID found.");
		
		JsonTrigger jtrigger = getJsonObject(c, JsonTrigger.class);
		
		if (Util.isNullOrEmpty(jtrigger.name))
			throw new ApiException("New trigger name cannot be empty.");
		
		if (jtrigger.name != null)
			trigger.setName(jtrigger.name);
		
		set200Response(c, msg("Trigger updated successfully."));
	}
}