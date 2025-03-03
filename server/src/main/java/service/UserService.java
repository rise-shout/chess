package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;
    private final AuthTokenDAO authTokenDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.authTokenDAO = new AuthTokenDAO();
    }

    public AuthData register(UserData user) throws DataAccessException {
        // Implement user registration logic
        return null;
    }

    public AuthData login(UserData user) throws DataAccessException {
        // Implement user login logic
        return null;
    }

    public void logout(AuthData authData) throws DataAccessException {
        // Implement user logout logic
    }
}