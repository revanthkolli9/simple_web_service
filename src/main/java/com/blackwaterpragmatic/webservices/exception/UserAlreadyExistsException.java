package com.blackwaterpragmatic.webservices.exception;

public class UserAlreadyExistsException extends Throwable {

	private static final String DEFAULT_MESSAGE = "User already exists.";

	public UserAlreadyExistsException() {
		super(DEFAULT_MESSAGE);
	}
}

	
