package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;


import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDataAccess userDAO;
    private final AuthDataAccess authTokenDAO;

    public UserService(UserDataAccess userDAO, AuthDataAccess authTokenDAO) {
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
        //String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt()); double hashing??
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
        //System.out.println("About to check password");

        if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
           //System.out.println("No match!");

           throw new DataAccessException("Error: unauthorized");
        }
        //System.out.println("Password match!");

        // Generate a new auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, user.username());
        authTokenDAO.insertAuth(authData);

        // Return the result
        System.out.println(authToken);
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