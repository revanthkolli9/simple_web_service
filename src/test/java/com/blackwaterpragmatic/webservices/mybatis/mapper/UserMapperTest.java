package com.blackwaterpragmatic.webservices.mybatis.mapper;

import com.blackwaterpragmatic.webservices.bean.User;
import com.blackwaterpragmatic.webservices.resource.UserFixture;
import com.blackwaterpragmatic.webservices.spring.DataConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {DataConfiguration.class})
@Rollback
@Transactional
@Component
public class UserMapperTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Autowired
	private UserMapper userMapper;

	@Test
	public void should_list_users() {
		final List<User> users = userMapper.list(null);

		assertEquals(4, users.size());
	}

	@Test
	public void should_fetch_users_with_matching_name() {
		final List<User> users = userMapper.list("Sky");

		assertEquals(1, users.size());
		final User user = users.get(0);

		assertEquals(new Integer(1), user.getId());
		assertEquals("Luke Skywalker", user.getName());
	}

	@Test
	public void should_fetch_empty_list_when_no_matches() {
		final List<User> users = userMapper.list("Sly");

		assertEquals(0, users.size());
	}

	@Test
	public void should_fetch_user() {
		final List<User> users = userMapper.list(null);
		final User firstUser = users.get(0);

		final User user = userMapper.fetch(firstUser.getId());

		assertEquals(firstUser.getId(), user.getId());
		assertEquals(firstUser.getName(), user.getName());
	}

	@Test
	public void should_not_fetch_user_null_case() {
		final User user = userMapper.fetch(null);
		assertNull(user);
	}


	@Test
	public void should_create_new_user() {
		final User user = UserFixture.createUser("Lando");

		userMapper.create(user);
		final User actual = userMapper.fetch(user.getId());

		assertNotNull(actual);
		assertEquals(user.getId(), actual.getId());
		assertEquals(user.getName(), actual.getName());
	}

	@Test
	public void should_not_create_new_user_constraint_violation() {
		final User user = UserFixture.createUser("Lando");
		userMapper.create(user);

		final User user2 = UserFixture.createUser(user.getName());

		expectedException.expect(DuplicateKeyException.class);
		userMapper.create(user2);
	}

	@Test
	public void should_delete_user() {
		final User user = UserFixture.createUser("R2D2");
		userMapper.create(user);

		final Integer deletedRecords = userMapper.delete(user.getId());

		final User fetchedUser = userMapper.fetch(user.getId());

		assertEquals(new Integer(1), deletedRecords);
		assertNull(fetchedUser);
	}

	@Test
	public void should_not_delete_user_not_found() {
		final User user = UserFixture.createUser("R2D2");
		final User wrongUser = UserFixture.createUser("R2D3");

		userMapper.create(user);

		final Integer deletedRecords = userMapper.delete(wrongUser.getId());

		final User fetchedUser = userMapper.fetch(user.getId());

		assertEquals(new Integer(0), deletedRecords);
		assertNotNull(fetchedUser);
	}
}
