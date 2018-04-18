package com.dillonsoftware.webservices.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.dillonsoftware.webservices.bean.Error;
import com.dillonsoftware.webservices.bean.User;
import com.dillonsoftware.webservices.service.UserService;
import com.dillonsoftware.webservices.test.MockHelper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
	public void should_list_users() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final Integer userId = 1;
		final String name = "name";

		final User expectedUser = new User() {
			{
				setId(userId);
				setName(name);
			}
		};

		final List<User> expectedUsers = new ArrayList<User>() {
			{
				add(expectedUser);
			}
		};

		final CollectionType resultType = TypeFactory.defaultInstance().constructCollectionType(List.class, User.class);
		final String expectedResponse = new ObjectMapper().writer().forType(resultType).writeValueAsString(expectedUsers);

		when(userService.listUsers()).thenReturn(expectedUsers);

		final MockHttpRequest request = MockHttpRequest.get("/users");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).listUsers();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_get_user() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final Integer userId = 1;
		final String name = "name";

		final User expectedUser = new User() {
			{
				setId(userId);
				setName(name);
			}
		};

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
	public void should_not_get_missing_user() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final Integer userId = 1;
		final Error error = new Error() {
			{
				setMessage("User not found.");
			}
		};

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

}
