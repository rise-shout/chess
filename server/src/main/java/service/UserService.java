package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthTokenDAO authTokenDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.authTokenDAO = AuthTokenDAO.getInstance();
    }

    public UserService(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
        this.userDAO = userDAO;
        this.authTokenDAO = authTokenDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        // Validate the request
        if (request.username() == null || request.username().isEmpty() ||
                request.password() == null || request.password().isEmpty() ||
                request.email() == null || request.email().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }

        // Check if the username is already taken
        UserData existingUser = userDAO.getUser(request.username());
        if (existingUser != null) {
            throw new DataAccessException("Error: already taken");
        }

        // Create a new UserData object
        UserData newUser = new UserData(request.username(), request.password(), request.email());

        // Insert the new user into the database
        userDAO.insertUser(newUser);

        // Generate a new auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, newUser.username());
        authTokenDAO.insertAuth(authData);

        // Return the result
        return new RegisterResult(newUser.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        // Retrieve the user by username
        UserData user = userDAO.getUser(request.username());

        // Check if the user exists and the password matches
        if (user == null || !user.password().equals(request.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        // Generate a new auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, user.username());
        authTokenDAO.insertAuth(authData);

        // Return the result
        return new LoginResult(user.username(), authToken);
    }

    public void logout(String authToken) throws DataAccessException {
        // Check if the auth token exists
        AuthData authData = authTokenDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        // Delete the auth token to log out the user
        authTokenDAO.deleteAuth(authToken);
    }
}