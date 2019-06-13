package com.blackwaterpragmatic.webservices.resource;

import com.blackwaterpragmatic.webservices.bean.User;
import com.google.common.collect.ImmutableMap;

import javax.ws.rs.core.Response;

/**
 * It's not perfect, but it encapsulates the boiler plate that just makes for a hard read.
 * There could be a lot more refactoring here obviously, but the gist is we're a bit dry-er now
 * and the logic for creating generic entity responses is pulled out of the resources.
 */
public class UserResourceResponseBuilder extends AbstractResourceResponseBuilder<User> {

	private static final UserResourceResponseBuilder INSTANCE = new UserResourceResponseBuilder();

	/**
	 * Preferably, would like to return i18n keys here for common responses
	 */
	private ImmutableMap<Response.Status, String> registeredErrors = ImmutableMap.<Response.Status, String>builder()
			.put(Response.Status.NOT_FOUND, "User not found.")
			.put(Response.Status.INTERNAL_SERVER_ERROR, "An error occurred.")
			.build();

	public static UserResourceResponseBuilder instance() {
		return INSTANCE;
	}

	@Override
	protected String getErrorMessageForStatus(final Response.Status status) {
		return this.registeredErrors.getOrDefault(status, String.format("Unknown error occurred for status: %s.", status.name()));
	}
}
