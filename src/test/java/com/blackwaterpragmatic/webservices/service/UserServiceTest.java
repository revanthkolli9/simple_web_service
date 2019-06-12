package com.blackwaterpragmatic.webservices.service;

import com.blackwaterpragmatic.webservices.bean.User;
import com.blackwaterpragmatic.webservices.exception.UserAlreadyExistsException;
import com.blackwaterpragmatic.webservices.exception.UserNotFoundException;
import com.blackwaterpragmatic.webservices.mybatis.mapper.UserMapper;
import com.blackwaterpragmatic.webservices.resource.UserFixture;
import com.blackwaterpragmatic.webservices.test.MockHelper;
import com.google.common.collect.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private UserMapper userMapper;

	@InjectMocks
	private UserService userService;

	@Test
	public void should_list_users() {
		final List<User> expectedUsers = new ArrayList<>();

		when(userMapper.list(null)).thenReturn(expectedUsers);

		final List<User> users = userService.listUsers();

		verify(userMapper).list(null);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(expectedUsers, users);
	}

	@Test
	public void should_list_users_matching_name() {
		final String name = "yoda";
		final List<User> expectedUsers = new ArrayList<>();

		expectedUsers.add(new User());

		when(userMapper.list(name)).thenReturn(expectedUsers);

		final List<User> users = userService.listUsers(name);

		verify(userMapper, times(1)).list(name);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(expectedUsers, users);
	}

	@Test
	public void should_fetch_user() {
		final Integer userId = 1;
		final User expectedUser = new User();

		when(userMapper.fetch(userId)).thenReturn(expectedUser);

		final User user = userService.getUser(userId);

		verify(userMapper).fetch(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(expectedUser, user);
	}

	@Test
	public void should_create_user() throws UserAlreadyExistsException {
		final User expectedUser = UserFixture.createUser("Yoda");
		when(userMapper.list(expectedUser.getName())).thenReturn(Collections.emptyList());

		final User user = userService.createUser(expectedUser);

		verify(userMapper).create(expectedUser);
		verify(userMapper).list(expectedUser.getName());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(expectedUser, user);
	}

	@Test
	public void should_throw_UserAlreadyExistsException() throws UserAlreadyExistsException {

		final User expectedUser = UserFixture.createUser("Yoda");
		when(userMapper.list(expectedUser.getName())).thenReturn(Lists.newArrayList(expectedUser));

		expectedException.expect(UserAlreadyExistsException.class);

		userService.createUser(expectedUser);

		verify(userMapper).list(expectedUser.getName());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_delete_user() throws UserNotFoundException {
		final User user = UserFixture.createUser(1, "Yoda");

		when(userMapper.fetch(user.getId())).thenReturn(user);

		userService.deleteUser(user.getId());

		verify(userMapper).fetch(user.getId());
		verify(userMapper).delete(user.getId());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_throw_UserNotFoundException() throws UserNotFoundException {
		final User user = UserFixture.createUser(1, "Yoda");

		when(userMapper.fetch(user.getId())).thenReturn(null);

		expectedException.expect(UserNotFoundException.class);

		userService.deleteUser(user.getId());

		verify(userMapper).fetch(user.getId());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}
}
