package com.blackwaterpragmatic.webservices.resource;

import com.blackwaterpragmatic.webservices.bean.Error;
import com.blackwaterpragmatic.webservices.bean.User;
import com.blackwaterpragmatic.webservices.exception.UserAlreadyExistsException;
import com.blackwaterpragmatic.webservices.exception.UserNotFoundException;
import com.blackwaterpragmatic.webservices.service.UserService;
import com.blackwaterpragmatic.webservices.test.MockHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {

	@Mock
	private UserService userService;

	@Mock
	private HttpServletRequest httpServletRequest;

	@InjectMocks
	private UserResource userResource;

	@Before
	public void before() {
		ResteasyProviderFactory.getContextDataMap().put(HttpServletRequest.class, httpServletRequest);
	}


	@Test
	public void should_list_users() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final String searchTerm = null;
		final Integer userId = 1;
		final String name = "name";

		final User expectedUser = UserFixture.createUser(userId, name);
		final List<User> expectedUsers = new ArrayList<User>() {
			{
				add(expectedUser);
			}
		};

		final CollectionType resultType = TypeFactory.defaultInstance().constructCollectionType(List.class, User.class);
		final String expectedResponse = new ObjectMapper().writer().forType(resultType).writeValueAsString(expectedUsers);

		when(userService.listUsers(searchTerm)).thenReturn(expectedUsers);

		final MockHttpRequest request = MockHttpRequest.get("/users");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).listUsers(searchTerm);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_get_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final Integer userId = 1;
		final String name = "name";

		final User expectedUser = UserFixture.createUser(userId, name);

		final String expectedResponse = new ObjectMapper().writer().forType(User.class).writeValueAsString(expectedUser);

		when(userService.getUser(userId)).thenReturn(expectedUser);

		final MockHttpRequest request = MockHttpRequest.get("/users/" + userId);
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).getUser(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_not_get_missing_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final Integer userId = 1;
		final Error error = new Error("User not found.");

		final String expectedResponse = new ObjectMapper().writer().forType(Error.class).writeValueAsString(error);

		when(userService.getUser(userId)).thenReturn(null);

		final MockHttpRequest request = MockHttpRequest.get("/users/" + userId);
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).getUser(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}


	@Test
	public void should_list_matching_users() throws Exception {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final String searchTerm = "Sky";

		final List<User> expectedUsers = new ArrayList<User>() {
			{
				add(UserFixture.createUser(1, "Luke Skywalker"));
				add(UserFixture.createUser(5, "Anakin Skywalker"));
			}
		};

		final CollectionType resultType = TypeFactory.defaultInstance().constructCollectionType(List.class, User.class);
		final String expectedResponse = new ObjectMapper().writer().forType(resultType).writeValueAsString(expectedUsers);

		when(userService.listUsers(searchTerm)).thenReturn(expectedUsers);

		final MockHttpRequest request = MockHttpRequest.get("/users?name=" + searchTerm);
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).listUsers(searchTerm);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_return_empty_list_when_no_matching_users() throws Exception {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final String searchTerm = "Darth";

		final List<User> expectedUsers = Collections.emptyList();

		final String expectedResponse = new ObjectMapper().writer().forType(Error.class)
				.writeValueAsString(new Error("User not found."));

		when(userService.listUsers(searchTerm)).thenReturn(expectedUsers);

		final MockHttpRequest request = MockHttpRequest.get("/users?name=" + searchTerm);
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).listUsers(searchTerm);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_return_new_user() throws UserAlreadyExistsException, JsonProcessingException, URISyntaxException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final User user = UserFixture.createUser("Kylo Ren");

		final String content = new ObjectMapper().writer().forType(User.class)
				.writeValueAsString(user);

		final MockHttpRequest request = MockHttpRequest.post("/users");
		request.content(content.getBytes());
		request.contentType(MediaType.APPLICATION_JSON_TYPE);

		final String expectedResponse = new ObjectMapper().writer().forType(User.class)
				.writeValueAsString(user);

		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).createUser(any(User.class));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_return_bad_request_when_creating_user_already_exists() throws UserAlreadyExistsException,
			JsonProcessingException, URISyntaxException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final User user = UserFixture.createUser("Kylo Ren");

		final String content = new ObjectMapper().writer().forType(User.class)
				.writeValueAsString(user);

		final MockHttpRequest request = MockHttpRequest.post("/users");
		request.content(content.getBytes());
		request.contentType(MediaType.APPLICATION_JSON_TYPE);

		when(userService.createUser(any(User.class))).thenThrow(new UserAlreadyExistsException());

		final String expectedResponse = new ObjectMapper().writer().forType(Error.class)
				.writeValueAsString(new Error("User already exists."));

		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).createUser(any(User.class));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());

	}

	@Test
	public void should_delete_user_and_ok() throws JsonProcessingException, URISyntaxException, UserNotFoundException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final Integer userId = 1;

		final MockHttpRequest request = MockHttpRequest.delete("/users/" + userId);
		request.contentType(MediaType.APPLICATION_JSON_TYPE);

		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).deleteUser(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals("1", response.getContentAsString());
	}

	@Test
	public void should_return_not_found_when_deleting_invalid_user() throws UserNotFoundException, URISyntaxException,
			JsonProcessingException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final Integer userId = 1;
		final String expectedResponse = new ObjectMapper().writer().forType(Error.class)
				.writeValueAsString(new Error("User not found."));

		final MockHttpRequest request = MockHttpRequest.delete("/users/" + userId);
		request.contentType(MediaType.APPLICATION_JSON_TYPE);

		final MockHttpResponse response = new MockHttpResponse();

		when(userService.deleteUser(userId)).thenThrow(new UserNotFoundException());

		dispatcher.invoke(request, response);

		verify(userService).deleteUser(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

}
