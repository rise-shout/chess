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
    void testRegisterPositive() throws Exception {
        UserData user = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.register(user);
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void testRegisterNegative() {
        assertThrows(Exception.class, () -> {
            UserData user = new UserData(null, "password", "p1@email.com"); // Missing username
            facade.register(user);
        });
    }

    @Test
    void testLoginPositive() throws Exception {
        UserData user = new UserData("player1", "password", "p1@email.com");
        facade.register(user);
        AuthData authData = facade.login(user);
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void testLoginNegative() {
        assertThrows(Exception.class, () -> {
            UserData user = new UserData("player1", "wrongpassword", "p1@email.com");
            facade.login(user);
        });
    }
    @Test
    void testLogoutPositive() throws Exception {
        UserData user = new UserData("player2", "password", "p2@email.com");
        facade.register(user);
        AuthData authData = facade.login(user);

        assertNotNull(authData);
        facade.logout(authData.authToken());

        assertThrows(Exception.class, () -> {
            facade.listGames(authData.authToken());
        });
    }

    @Test
    void testLogoutNegative() {
        String invalidAuthToken = "invalidToken123";

        assertThrows(Exception.class, () -> {
            facade.logout(invalidAuthToken);
        });
    }


    @Test
    void testListGamesPositive() throws Exception {
        UserData user = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.register(user);

        facade.createGame(new GameData(1,"player1",null,"testGame"), user.username(), authData.authToken());

        List<GameData> games = facade.listGames(authData.authToken());
        assertNotNull(games);
        assertFalse(games.isEmpty());
        assertEquals("testGame", games.get(0).gameName());
    }

    @Test
    void testListGamesNegative() {
        assertThrows(Exception.class, () -> {
            facade.listGames("InvalidToken");
        });
    }

    @Test
    void testCreateGamePositive() throws Exception {
        UserData user = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.register(user);

        GameData game = new GameData(1,"player1",null,"testGame");
        int gameId = facade.createGame(game, user.username(), authData.authToken());
        assertTrue(gameId > 0);
    }

    @Test
    void testCreateGameNegative() {
        assertThrows(Exception.class, () -> {
            GameData game = new GameData(1,"player1",null,"testGame");
            facade.createGame(game, "player1", "InvalidToken");
        });
    }

    @Test
    void testJoinGamePositive() throws Exception {
        UserData user1 = new UserData("player1", "password", "p1@email.com");
        UserData user2 = new UserData("player2", "password", "p2@email.com");
        AuthData auth1 = facade.register(user1);
        AuthData auth2 = facade.register(user2);

        GameData game = new GameData(1,"player1",null,"testGame");
        int gameId = facade.createGame(game, user1.username(), auth1.authToken());

        facade.joinGame(auth2.authToken(), gameId, "BLACK");
        List<GameData> games = facade.listGames(auth1.authToken());
        assertEquals("player2", games.get(0).blackUsername());
    }

    @Test
    void testJoinGameNegative() {
        assertThrows(Exception.class, () -> {
            facade.joinGame("InvalidToken", 1, "BLACK");
        });
    }
}
