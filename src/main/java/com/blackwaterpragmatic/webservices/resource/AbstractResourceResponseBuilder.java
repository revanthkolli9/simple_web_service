package com.blackwaterpragmatic.webservices.resource;


import com.blackwaterpragmatic.webservices.bean.Error;

import javax.ws.rs.core.Response;
import java.util.List;

public abstract class AbstractResourceResponseBuilder<T> {

	protected abstract String getErrorMessageForStatus(final Response.Status status);

	public Response badRequest(final String message) {
		final Error error = new Error(message);
		return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
	}

	public Response ok(final T entity) {
		return Response.status(Response.Status.OK).entity(entity).build();
	}

	public Response ok(final List<T> entities) {
		return Response.status(Response.Status.OK).entity(entities).build();
	}

	public Response ok(final Integer id) {
		return Response.status(Response.Status.OK).entity(id).build();
	}

	public Response notFound() {
		return getErrorResponse(Response.Status.NOT_FOUND);
	}

	private Response getErrorResponse(final Response.Status status) {
		final Error error = new Error();
		error.setMessage(getErrorMessageForStatus(status));

		return Response.status(status).entity(error).build();
	}
}
