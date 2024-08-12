package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.List;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthTokenDAO authTokenDAO;

    public GameService() {
        this.gameDAO = new GameDAO();
        this.authTokenDAO = new AuthTokenDAO();
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
