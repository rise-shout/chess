package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class DatabaseService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthTokenDAO authTokenDAO;

    public DatabaseService() {
        this.userDAO = new UserDAO();
        this.gameDAO = new GameDAO();
        this.authTokenDAO = new AuthTokenDAO();
    }

    public DatabaseService(UserDAO userDAO, GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authTokenDAO =authTokenDAO;
    }

    public void clearDatabase() throws DataAccessException {
        // Clear users and games
        userDAO.clearAllUsers();
        gameDAO.clearAllGames();

        // Instead of clearing all tokens, clear only those that belong to deleted users
        // Get all auth tokens
        Map<String, AuthData> authTokenMap = authTokenDAO.getAllAuthTokens();

        // Create a new map to store valid tokens
        Map<String, AuthData> validTokens = new HashMap<>();

        // Retain only valid tokens (for users who are not deleted)
        for (Map.Entry<String, AuthData> entry : authTokenMap.entrySet()) {
            String username = entry.getValue().username();
            if (userDAO.getUser(username) != null) {
                validTokens.put(entry.getKey(), entry.getValue());
            }
        }

        // Set the authTokenDAO map to the filtered valid tokens
        authTokenDAO.setAuthTokens(validTokens);
    }
}