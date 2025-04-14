package client;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import service.*;
import static org.junit.jupiter.api.Assertions.*;
import java.net.*;
import java.util.List;

import chessclient.*;



public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static int myPort;


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        myPort = port;
        System.out.println("Started test HTTP server on " + port);



        // Initialize ServerFacade with the server's port
        facade = new ServerFacade("http://localhost:" + port);

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // Clear database before each test to ensure a clean state
    @BeforeEach
    void clearDatabase() throws Exception {
        // Send DELETE request to /db route to clear the database
        URL url = new URL("http://localhost:" + myPort + "/db");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int statusCode = connection.getResponseCode();
        if (statusCode != 200) {
            throw new Exception("Failed to clear database: " + statusCode);
        }
    }


    @Test
    void register() throws Exception {
        // Prepare test data
        String username = "player1";
        String password = "password";
        String email = "p1@email.com";

        // Register a user
        AuthData result = facade.register(new UserData(username, password, email));

        // Validate the result
        assertNotNull(result);
    }

    // Test the registration failure when trying to register with existing username
    @Test
    void registerFailure() throws Exception {
        String username = "player1";
        String password = "password";
        String email = "p1@email.com";

        // Register a user
        facade.register(new UserData(username, password, email));

        // Try to register again with the same username
        Exception exception = assertThrows(Exception.class, () -> {
            facade.register(new UserData(username, password, email));
        });

        assertTrue(exception.getMessage().contains("Registration failed"));
    }

    // Test login with correct credentials
    @Test
    void login() throws Exception {
        // First register the user
        String username = "player1";
        String password = "password";
        String email = "p1@email.com";
        facade.register(new UserData(username, password, email));

        // Now, login
        AuthData loginResult = facade.login(new UserData(username, password, null));

        // Validate the login result
        assertNotNull(loginResult);
        //assertTrue(loginResult.authToken().length() > 10);
    }

    // Test login with incorrect credentials
    @Test
    void loginFailure() throws Exception {
        // Try logging in with incorrect credentials
        String username = "player1";
        String password = "wrongpassword";

        Exception exception = assertThrows(Exception.class, () -> {
            facade.login(new UserData(username, password, null));
        });

        assertTrue(exception.getMessage().contains("Login failed"));
    }

    // Test listing games when there are no games
    /*
    @Test
    void listGamesEmpty() throws Exception {
        String username = "player1";
        String password = "password";
        String email = "p1@email.com";
        UserData result = facade.register(new UserData(username, password, email));

        String authToken = result.authToken();

        // Call listGames

            List<GameData> games = facade.listGames(authToken);

            // Assert that the returned list is empty
            assertTrue(games.isEmpty());



    }

    // Test creating a game
    @Test
    void createGame() throws Exception {
        String username = "player1";
        String password = "password";
        String email = "p1@email.com";
        UserData result = facade.register(new UserData(username, password, email));

        String authToken = result.authToken();
        String gameName = "Chess Game";

        // Create a game
        int gameId = facade.createGame(authToken, gameName);

        // Assert that the game was created (positive test case)
        assertTrue(gameId > 0);
    }

    // Test failure in creating a game
    @Test
    void createGameFailure() throws Exception {
        String username = "player1";
        String password = "password";
        String email = "p1@email.com";
        UserData result = facade.register(new UserData(username, password, email));

        String authToken = result.authToken();
        String gameName = ""; // Invalid game name (empty)

        // Try creating a game with an invalid name
        Exception exception = assertThrows(Exception.class, () -> {
            facade.createGame(authToken, gameName);
        });

        assertTrue(exception.getMessage().contains("Failed to create game"));
    }

    // Test joining a game
    @Test
    void joinGame() throws Exception {
        String username = "player1";
        String password = "password";
        String email = "p1@email.com";
        UserData result = facade.register(new UserData(username, password, email));

        String authToken = result.authToken();
        int gameId = facade.createGame(authToken, "test game");
        String color = "WHITE";

        // Join the game
        facade.joinGame(authToken, gameId, color);

        // Assert that no exception was thrown and game was joined successfully
        assertTrue(true);
    }

    // Test failure when joining a game
    @Test
    void joinGameFailure() throws Exception {
        String username = "player1";
        String password = "password";
        String email = "p1@email.com";
        UserData result = facade.register(new UserData(username, password, email));

        String authToken = result.authToken();
        int gameId = facade.createGame(authToken, "test game");
        String color = "INVALID_COLOR"; // Invalid color

        // Try joining the game with an invalid color
        Exception exception = assertThrows(Exception.class, () -> {
            facade.joinGame(authToken, gameId, color);
        });

        assertTrue(exception.getMessage().contains("Failed to join game"));
    }

     */



}
