package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.DatabaseService;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseServiceTest {

    private DatabaseService databaseService;
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthTokenDAO authTokenDAO;

    @BeforeEach
    public void setup() {
        userDAO = new UserDAO();
        gameDAO = new GameDAO();
        authTokenDAO = AuthTokenDAO.getInstance();
        authTokenDAO.reset(); // Reset the singleton state for testing

        databaseService = new DatabaseService(userDAO, gameDAO, authTokenDAO);
    }

    @Test
    public void testClearDatabaseSuccess() throws DataAccessException {
        // Add a user
        UserData user = new UserData("testUser", "testPassword", "test@mail.com");
        userDAO.insertUser(user);

        // Add a game
        GameData game = new GameData(1, "testUser", null, "testGame");
        gameDAO.insertGame(game);

        // Add an auth token
        AuthData authToken = new AuthData("testToken", "testUser");
        authTokenDAO.insertAuth(authToken);

        // Verify that the data is in the database
        assertEquals(1, userDAO.getAllUsers().size(), "User should be present before clearing the database");
        assertEquals(1, gameDAO.getAllGames().size(), "Game should be present before clearing the database");
        assertNotNull(authTokenDAO.getAuth("testToken"), "Auth token should be present before clearing the database");

        // Clear the database
        databaseService.clearDatabase();

        // Verify that the database is cleared
        assertEquals(0, userDAO.getAllUsers().size(), "Users should be cleared from the database");
        assertEquals(0, gameDAO.getAllGames().size(), "Games should be cleared from the database");
        assertNull(authTokenDAO.getAuth("testToken"), "Auth tokens should be cleared from the database");
    }

    @Test
    public void testClearDatabaseWhenEmpty() throws DataAccessException {
        // Ensure the database is initially empty
        assertEquals(0, userDAO.getAllUsers().size(), "Users should be empty initially");
        assertEquals(0, gameDAO.getAllGames().size(), "Games should be empty initially");
        assertNull(authTokenDAO.getAuth("testToken"), "Auth tokens should be empty initially");

        // Clear the database
        databaseService.clearDatabase();

        // Verify that clearing an already empty database does not cause issues
        assertEquals(0, userDAO.getAllUsers().size(), "Users should still be empty after clearing");
        assertEquals(0, gameDAO.getAllGames().size(), "Games should still be empty after clearing");
        assertNull(authTokenDAO.getAuth("testToken"), "Auth tokens should still be empty after clearing");
    }
}
