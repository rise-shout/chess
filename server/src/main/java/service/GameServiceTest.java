package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private GameService gameService;
    private GameDAO gameDAO;
    private AuthTokenDAO authTokenDAO;

    @BeforeEach
    public void setup() {
        gameDAO = new GameDAO();
        authTokenDAO = AuthTokenDAO.getInstance();
        authTokenDAO.reset();  // Reset the singleton state for testing
        gameService = new GameService(gameDAO, authTokenDAO);
    }

    // Positive test case for createGame
    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        String authToken = "validAuthToken";
        authTokenDAO.insertAuth(new AuthData(authToken, "testUser"));

        int gameId = gameService.createGame(authToken, "testGame");
        assertTrue(gameId > 0);

        GameData game = gameDAO.getGameById(gameId);
        assertNotNull(game);
        assertEquals("testGame", game.gameName());
    }

    // Negative test case for createGame with invalid authToken
    @Test
    public void testCreateGameInvalidAuth() {
        String authToken = "invalidAuthToken";

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(authToken, "testGame");
        });
        assertTrue(thrown.getMessage().contains("Error: unauthorized"));
    }

    // Negative test case for createGame with empty gameName
    @Test
    public void testCreateGameEmptyName() throws DataAccessException {
        String authToken = "validAuthToken";
        authTokenDAO.insertAuth(new AuthData(authToken, "testUser"));

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(authToken, "");
        });
        assertTrue(thrown.getMessage().contains("Error: bad request"));
    }

    // Positive test case for listGames
    @Test
    public void testListGamesSuccess() throws DataAccessException {
        String authToken = "validAuthToken";
        authTokenDAO.insertAuth(new AuthData(authToken, "testUser"));

        gameService.createGame(authToken, "testGame1");
        gameService.createGame(authToken, "testGame2");

        List<GameData> games = gameService.listGames(authToken);
        assertNotNull(games);
        assertEquals(2, games.size());
    }

    // Negative test case for listGames with invalid authToken
    @Test
    public void testListGamesInvalidAuth() {
        String authToken = "invalidAuthToken";

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            gameService.listGames(authToken);
        });
        assertTrue(thrown.getMessage().contains("Error: unauthorized"));
    }
}