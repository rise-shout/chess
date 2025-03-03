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
        this.authTokenDAO = new AuthTokenDAO();
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
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

    public AuthData login(UserData user) throws DataAccessException {
        // Implement user login logic
        System.out.println("HERE");
        return null;
    }

    public void logout(AuthData authData) throws DataAccessException {
        // Implement user logout logic
    }
}