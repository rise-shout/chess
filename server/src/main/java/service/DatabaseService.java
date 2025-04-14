package service;

import dataaccess.*;


public class DatabaseService {
    private final UserDataAccess userDAO;
    private final GameDataAccess gameDAOVar;
    private final AuthDataAccess authTokenDAO;


    public DatabaseService(UserDataAccess userDAO, GameDataAccess gameDAO, AuthDataAccess authTokenDAO) {
        this.userDAO = userDAO;
        this.gameDAOVar = gameDAO;
        this.authTokenDAO =authTokenDAO;
    }

    public void clearDatabase() throws DataAccessException {

        // Clear users and games
        userDAO.clearAllUsers();
        gameDAOVar.clearAllGames();
        authTokenDAO.clearAllTokens();

    }
}