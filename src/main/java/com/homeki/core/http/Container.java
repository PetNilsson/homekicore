package com.homeki.core.http;

import org.hibernate.Session;
import org.restlet.Request;
import org.restlet.Response;

public class Container {
	public Request req;
	public Response res;
	public Session ses;
}