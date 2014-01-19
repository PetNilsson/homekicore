package com.homeki.core.web.rest;

import com.homeki.core.http.ApiException;
import com.homeki.core.http.json.JsonServerInfo;
import com.homeki.core.http.json.JsonVoid;
import com.homeki.core.main.Setting;
import com.homeki.core.main.Util;
import com.homeki.core.storage.Hibernate;
import org.hibernate.Session;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/server")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ServerResource {
	@GET
	@Path("/get")
	public Response get() {
		Session ses = Hibernate.currentSession();
		
		String name = Setting.getString(ses, Setting.SERVER_NAME);
		Double longitude = Setting.getDouble(ses, Setting.LOCATION_LONGITUDE);
		Double latitude = Setting.getDouble(ses, Setting.LOCATION_LATITUDE);
		String smtpHost = Setting.getString(ses, Setting.SMTP_HOST);
		Integer smtpPort = Setting.getInt(ses, Setting.SMTP_PORT);
		Boolean smtpAuth = Setting.getBoolean(ses, Setting.SMTP_AUTH);
		Boolean smtpTls = Setting.getBoolean(ses, Setting.SMTP_TLS);
		String smtpUser = Setting.getString(ses, Setting.SMTP_USER);
		String smtpPassword = Setting.getString(ses, Setting.SMTP_PASSWORD);

		return Response.ok(new JsonServerInfo(name, longitude, latitude, smtpHost, smtpPort, smtpAuth, smtpTls, smtpUser, smtpPassword)).build();
	}

	@POST
	@Path("/set")
	public Response set(JsonServerInfo jinfo) {
		Session ses = Hibernate.currentSession();

		if (Util.isNotNullAndEmpty(jinfo.name))
			throw new ApiException("Attribute 'name' exists, but is empty. An empty name is not allowed as a server name.");

		if (jinfo.name != null)
			Setting.putString(ses, Setting.SERVER_NAME, jinfo.name);
		if (jinfo.locationLongitude != null)
			Setting.putDouble(ses, Setting.LOCATION_LONGITUDE, jinfo.locationLongitude);
		if (jinfo.locationLatitude != null)
			Setting.putDouble(ses, Setting.LOCATION_LATITUDE, jinfo.locationLatitude);
		if (jinfo.smtpHost != null)
			Setting.putString(ses, Setting.SMTP_HOST, jinfo.smtpHost);
		if (jinfo.smtpPort != null)
			Setting.putInt(ses, Setting.SMTP_PORT, jinfo.smtpPort);
		if (jinfo.smtpAuth != null)
			Setting.putBoolean(ses, Setting.SMTP_AUTH, jinfo.smtpAuth);
		if (jinfo.smtpTls != null)
			Setting.putBoolean(ses, Setting.SMTP_TLS, jinfo.smtpTls);
		if (jinfo.smtpUser != null)
			Setting.putString(ses, Setting.SMTP_USER, jinfo.smtpUser);
		if (jinfo.smtpPassword != null)
			Setting.putString(ses, Setting.SMTP_PASSWORD, jinfo.smtpPassword);

		return Response.ok(new JsonVoid("Server information updated successfully.")).build();
	}
}
