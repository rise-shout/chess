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

    public DatabaseService(UserDAO userDAO, GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authTokenDAO =authTokenDAO;
    }

    public void clearDatabase() throws DataAccessException {
        userDAO.clearAllUsers();
        gameDAO.clearAllGames();
        authTokenDAO.clearAllAuthTokens();
    }
}