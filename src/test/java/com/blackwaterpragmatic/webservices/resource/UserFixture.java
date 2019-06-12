package com.blackwaterpragmatic.webservices.resource;

import com.blackwaterpragmatic.webservices.bean.User;

/*
	I like using fixtures to encapsulate the creation of test data.
 */
public class UserFixture {
	public static User createUser(final int id, final String name) {
		final User user = new User();
		user.setId(id);
		user.setName(name);

		return user;
	}

	public static User createUser(final String name) {
		final User user = new User();
		user.setName(name);

		return user;
	}
}
