package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;
    private UserDAO userDAO;
    private AuthTokenDAO authTokenDAO;

    @BeforeEach
    public void setup() {
        userDAO = new UserDAO();
        authTokenDAO = AuthTokenDAO.getInstance();
        authTokenDAO.reset();  // Reset the singleton state for testing
        userService = new UserService(userDAO, authTokenDAO);
    }

    // Positive test case for register
    @Test
    public void testRegisterSuccess() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("testUser", "testPassword", "test@mail.com");
        RegisterResult result = userService.register(request);

        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
    }

    // Negative test case for register with existing username
    @Test
    public void testRegisterDuplicateUser() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("testUser", "testPassword", "test@mail.com");
        userService.register(request);  // First registration should succeed

        // Second registration with the same username should fail
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
        assertTrue(thrown.getMessage().contains("Error: already taken"));
    }

    // Negative test case for register with missing password
    @Test
    public void testRegisterMissingPassword() {
        RegisterRequest request = new RegisterRequest("testUser", null, "test@mail.com");

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
        assertTrue(thrown.getMessage().contains("Error: bad request"));
    }

    // Positive test case for login
    @Test
    public void testLoginSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPassword", "test@mail.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("testUser", "testPassword");
        LoginResult loginResult = userService.login(loginRequest);

        assertNotNull(loginResult);
        assertEquals("testUser", loginResult.username());
        assertNotNull(loginResult.authToken());
    }

    // Negative test case for login with wrong password
    @Test
    public void testLoginWrongPassword() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPassword", "test@mail.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("testUser", "wrongPassword");

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });
        assertTrue(thrown.getMessage().contains("Error: unauthorized"));
    }

    // Negative test case for login with non-existing user
    @Test
    public void testLoginNonExistingUser() {
        LoginRequest loginRequest = new LoginRequest("nonExistingUser", "password");

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });
        assertTrue(thrown.getMessage().contains("Error: unauthorized"));
    }
}