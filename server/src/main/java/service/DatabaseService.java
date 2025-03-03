package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class DatabaseService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthTokenDAO authTokenDAO;

    public DatabaseService() {
        this.userDAO = new UserDAO();
        this.gameDAO = new GameDAO();
        this.authTokenDAO = new AuthTokenDAO();
    }

    public void clearDatabase() throws DataAccessException {
        userDAO.clearAllUsers();
        gameDAO.clearAllGames();
        authTokenDAO.clearAllAuthTokens();
    }
}