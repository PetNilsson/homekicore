package com.homeki.core.http.filters;

import com.homeki.core.logging.L;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

public class LogRequestFilter implements ContainerRequestFilter {
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		L.i("Received request " + requestContext.getMethod() + " " + requestContext.getUriInfo().getPath() + ".");
	}
}
