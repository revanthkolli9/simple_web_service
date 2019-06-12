package com.blackwaterpragmatic.webservices.service;

import com.blackwaterpragmatic.webservices.bean.User;
import com.blackwaterpragmatic.webservices.exception.UserAlreadyExistsException;
import com.blackwaterpragmatic.webservices.exception.UserNotFoundException;
import com.blackwaterpragmatic.webservices.mybatis.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@Service
public class UserService {

	private final UserMapper userMapper;

	@Autowired
	public UserService(
			final UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	public List<User> listUsers() {
		return userMapper.list(null);
	}

	/**
	 *
	 * @param name the string to search for.  Partial matches will return results!
	 * @return a list of {@link User} matching the given criteria
	 */
	public List<User> listUsers(final String name) {
		return userMapper.list(name);
	}

	public User getUser(final Integer userId) {
		return userMapper.fetch(userId);
	}

	/**
	 *
	 * @param user the user that is to be created
	 * @return the user
	 * @throws UserAlreadyExistsException if the user already exists.
	 */
	public User createUser(final User user) throws UserAlreadyExistsException {
		final List<User> users = userMapper.list(user.getName());

		if (!users.isEmpty()) {
			throw new UserAlreadyExistsException();
		}

		userMapper.create(user);
		return user;
	}

	/**
	 * Delete the given user.
	 *
	 * @param userId the id of the user to delete
	 * @return the number of users deleted, should only be 1 or zero
	 * @throws UserNotFoundException if the given user doesn't exist
	 */
	public Integer deleteUser(final Integer userId) throws UserNotFoundException {
		final User user = userMapper.fetch(userId);

		if (isNull(user)) {
			throw new UserNotFoundException();
		}

		return userMapper.delete(userId);
	}
}
