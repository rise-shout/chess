package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.List;

public class GameService {
    private static GameDAO gameDAO;
    private static AuthTokenDAO authTokenDAO;

    public GameService() {
        gameDAO = new GameDAO();
        authTokenDAO = new AuthTokenDAO();
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        // Validate the auth token
        AuthData authData = authTokenDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        // Retrieve the list of games
        return gameDAO.getAllGames();
    }
}