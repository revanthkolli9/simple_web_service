# simple\_web\_service

This is a basic, self-contained, REST web service example designed to be extended by candidates.

The project utilizes the following frameworks and libraries:

- Spring ([https://spring.io/](https://spring.io/))
- RestEasy ([http://resteasy.jboss.org/](http://resteasy.jboss.org/))
- MyBatis ([http://www.mybatis.org/mybatis-3/](http://www.mybatis.org/mybatis-3/))
- JUnit ([https://junit.org/junit4/](https://junit.org/junit4/))
- Mockito ([http://site.mockito.org/](http://site.mockito.org/))
- Postman ([https://www.getpostman.com/](https://www.getpostman.com/))


The following REST endpoints are implemented:

- `GET - /users` - return a list of users
- `GET - /users/{id}` - return a specific user


`/src/test/postman/Users.postman_collection.json` is a Postman collection to verify these endpoints against a running server.


# Git

You can clone the project with:

	git clone https://github.com/revanthkolli9/simple_web_service.git


# Prerequisites

* Java 8

		
# Build

	mvn clean package


# Run

The app can be run from terminal using the Maven command:

	mvn jetty:run


# Instructions
- Clone and build the project.
- Make the following functional enhancements.  Feel free modify the existing code as needed.  Note the existing architecture and do your best to make new code blends in with the existing.
  - adjust `GET - /users` to sort the list of users by name
  - add an optional `name` query parameter to `GET - /users` which will allow the client to search for matches.
  - add the ability to add a new user.
  - add the ability to delete a user.

It's expected that unit tests will be added to verify all functional updates.  

Finally, add postman tests to verify the name search functionality - as well as adding and deleting users (happy path validations are fine for the Postman tests).