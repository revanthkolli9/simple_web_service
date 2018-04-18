package com.dillonsoftware.webservices.resource;

import com.dillonsoftware.webservices.bean.Error;
import com.dillonsoftware.webservices.bean.User;
import com.dillonsoftware.webservices.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.util.List;

@Service
@Path("/users")
public class UserResource {

	private final UserService userService;

	@Autowired
	public UserResource(
			final UserService userService) {
		this.userService = userService;
	}


	@GET
	@Produces("application/json;charset=utf-8")
	public Response getUsers() {
		final List<User> users = userService.listUsers();

		return Response.status(Response.Status.OK).entity(users).build();
	}


	@Path("/{userId}")
	@GET
	@Produces("application/json;charset=utf-8")
	public Response getUser(@PathParam("userId") final Integer userId) {
		final User user = userService.getUser(userId);

		if (null == user) {
			final Error error = new Error();
			error.setMessage("User not found.");
			return Response.status(Response.Status.NOT_FOUND).entity(error).build();
		} else {
			return Response.status(Response.Status.OK).entity(user).build();
		}
	}

}
