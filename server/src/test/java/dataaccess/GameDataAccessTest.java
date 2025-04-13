package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class MySqlGameDataAccessTest {

    private MySqlGameDataAccess gameDataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDataAccess = new MySqlGameDataAccess(); // Assume this sets up the database connection
        gameDataAccess.clearAllGames();
    }

    @Test
    void testInsertGameSuccess() throws DataAccessException {
        // Create a GameData object for testing
        GameData game = new GameData(1, "playerWhite", "playerBlack","testGame");

        // Insert the game
        int gameId = gameDataAccess.insertGame(game);

        // Retrieve the game by its ID and check if it was inserted successfully
        GameData retrievedGame = gameDataAccess.getGameById(gameId);
        assertNotNull(retrievedGame);
        assertEquals("playerWhite", retrievedGame.whiteUsername());
        assertEquals("playerBlack", retrievedGame.blackUsername());
        assertEquals("testGame", retrievedGame.gameName());
    }

    @Test
    void testInsertGameFailure() throws DataAccessException {
        // Create a game object
        GameData game = new GameData(1, "playerWhite", "playerBlack","testGame");
        int gameId = gameDataAccess.insertGame(game);

        // Try inserting a game with the same name, assuming game names should be unique
        GameData duplicateGame = new GameData(1, "playerWhite", "playerBlack","testGame");

        try {
            gameDataAccess.insertGame(duplicateGame);
            fail("Expected DataAccessException due to duplicate game name.");
        } catch (DataAccessException e) {
            assertFalse(e.getMessage().contains("Game already exists"));
        }
    }

    @Test
    void testGetGameByIdSuccess() throws DataAccessException {
        // Insert a game first
        GameData game = new GameData(1,"playerWhite", "playerBlack", "testGame");
        int gameId = gameDataAccess.insertGame(game);

        // Retrieve the game by its ID
        GameData retrievedGame = gameDataAccess.getGameById(gameId);

        // Assert the game data is correct
        assertNotNull(retrievedGame);
        assertEquals(gameId, retrievedGame.gameID());
        assertEquals("playerWhite", retrievedGame.whiteUsername());
        assertEquals("playerBlack", retrievedGame.blackUsername());
        assertEquals("testGame", retrievedGame.gameName());
    }

    @Test
    void testGetGameByIdFailure() throws DataAccessException {
        // Try retrieving a game that doesn't exist
        GameData retrievedGame = gameDataAccess.getGameById(999);  // Assuming ID 999 does not exist
        assertNull(retrievedGame);  // Should return null or handle as per the implementation
    }

    @Test
    void testUpdateGameSuccess() throws DataAccessException {
        // Insert a game
        GameData game = new GameData(1,"playerWhite", "playerBlack", "testGame");
        int gameId = gameDataAccess.insertGame(game);

        // Update the game details
        GameData updatedGame = new GameData(gameId, "playerWhite", "playerBlack", "updatedGame");
        gameDataAccess.updateGame(updatedGame, game);

        // Retrieve the updated game and verify changes
        GameData retrievedGame = gameDataAccess.getGameById(gameId);
        assertNotNull(retrievedGame);
        assertEquals("updatedGame", retrievedGame.gameName());  // Ensure game name has been updated
        assertEquals(gameId, retrievedGame.gameID());  // Ensure the game ID remains unchanged
    }

    @Test
    void testUpdateGameFailure() throws DataAccessException {
        GameData nonExistentGame = new GameData(999, "playerWhite", "playerBlack", "nonExistentGame");
        try {
            gameDataAccess.updateGame(nonExistentGame);
            fail("Expected DataAccessException due to game not found.");
        } catch (DataAccessException e) {
            assertTrue(e.getMessage().contains("Game not found"));
        }
    }

    @Test
    void testGetAllGames() throws DataAccessException {
        // Insert a couple of games
        gameDataAccess.insertGame(new GameData(1,"playerWhite1", "playerBlack1", "game1"));
        gameDataAccess.insertGame(new GameData(2,"playerWhite2", "playerBlack2", "game2"));

        // Retrieve all games
        List<GameData> allGames = gameDataAccess.getAllGames();

        // Assert that the list contains the expected number of games
        assertNotNull(allGames);
        assertTrue(allGames.size() >= 2);
    }

    @Test
    void testClearAllGames() throws DataAccessException {
        // Insert a game
        gameDataAccess.insertGame(new GameData(1,"playerWhite", "playerBlack", "gameToClear"));

        // Clear all games
        gameDataAccess.clearAllGames();

        // Assert that no games remain
        List<GameData> allGames = gameDataAccess.getAllGames();
        assertTrue(allGames.isEmpty());
    }
}
