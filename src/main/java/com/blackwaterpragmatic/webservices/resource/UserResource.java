package com.blackwaterpragmatic.webservices.resource;

import com.blackwaterpragmatic.webservices.bean.User;
import com.blackwaterpragmatic.webservices.exception.UserAlreadyExistsException;
import com.blackwaterpragmatic.webservices.exception.UserNotFoundException;
import com.blackwaterpragmatic.webservices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Path("/users")
public class UserResource {

	private final UserService userService;

	private final UserResourceResponseBuilder responseBuilder = UserResourceResponseBuilder.instance();

	@Autowired
	public UserResource(
			final UserService userService) {
		this.userService = userService;
	}


	@GET
	@Produces("application/json;charset=utf-8")
	public Response getUsers(@QueryParam("name") final String name) {
		final List<User> users = userService.listUsers(name).stream()
				.sorted(Comparator.comparing(User::getName))
				.collect(Collectors.toList());

		return users.isEmpty() ?
				responseBuilder.notFound() : responseBuilder.ok(users);
	}


	@Path("/{userId}")
	@GET
	@Produces("application/json;charset=utf-8")
	public Response getUser(@PathParam("userId") final Integer userId) {
		final User user = userService.getUser(userId);

		return isNull(user) ?
				responseBuilder.notFound() : responseBuilder.ok(user);
		}


	@POST
	@Produces("application/json;charset=utf-8")
	public Response createUser(final User user) {
		try {
			userService.createUser(user);
		} catch (final UserAlreadyExistsException e) {
			return responseBuilder.badRequest(e.getMessage());
	}

		return responseBuilder.ok(user);
	}


	@Path("/{userId}")
	@DELETE
	@Produces("application/json;charset=utf-8")
	public Response deleteUser(@PathParam("userId") final Integer userId) {

		try {
			userService.deleteUser(userId);
		} catch (final UserNotFoundException e) {
			return responseBuilder.notFound();
		}

		return responseBuilder.ok(userId);
	}
}
