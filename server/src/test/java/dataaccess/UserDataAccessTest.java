package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class MySqlUserDataAccessTest {

    private MySqlUserDataAccess userDataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDataAccess = new MySqlUserDataAccess(); // Assume this sets up the database connection
        userDataAccess.clearAllUsers();
    }

    @Test
    void testInsertUserSuccess() throws DataAccessException {
        // Create a user for testing
        UserData user = new UserData("testUser", "hashedPassword", "test@mail.com");

        // Try inserting the user
        userDataAccess.insertUser(user);

        // Retrieve the user and assert it exists
        UserData retrievedUser = userDataAccess.getUser("testUser");
        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.username());
        assertEquals("test@mail.com", retrievedUser.email());
    }

    @Test
    void testInsertUserFailureDuplicate() {
        // Create and insert a user
        UserData user = new UserData("testUser", "hashedPassword", "test@mail.com");
        try {
            userDataAccess.insertUser(user);
            // Try inserting the same user again, which should fail due to duplicate username
            UserData duplicateUser = new UserData("testUser", "hashedPassword2", "another@mail.com");
            userDataAccess.insertUser(duplicateUser);
            fail("Expected DataAccessException due to duplicate username.");
        } catch (DataAccessException e) {
            assertFalse(e.getMessage().contains("User already exists"));
        }
    }

    @Test
    void testGetUserSuccess() throws DataAccessException {
        // Create and insert a user
        UserData user = new UserData("testUser2", "hashedPassword", "test@mail.com");
        userDataAccess.insertUser(user);

        // Try retrieving the user
        UserData retrievedUser = userDataAccess.getUser("testUser2");
        assertNotNull(retrievedUser);
        assertEquals("testUser2", retrievedUser.username());
    }

    @Test
    void testGetUserFailureNotFound() {
        // Try retrieving a user that does not exist
        try {
            UserData retrievedUser = userDataAccess.getUser("nonExistentUser");
            assertNull(retrievedUser);
        } catch (DataAccessException e) {
            assertTrue(e.getMessage().contains("User not found"));
        }
    }

    @Test
    void testGetAllUsers() throws DataAccessException {
        // Create and insert a few users
        userDataAccess.insertUser(new UserData("user1", "hashedPassword1", "user1@mail.com"));
        userDataAccess.insertUser(new UserData("user2", "hashedPassword2", "user2@mail.com"));

        // Get all users
        List<UserData> allUsers = userDataAccess.getAllUsers();

        // Assert that the users are returned and that the size is correct
        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
    }

    @Test
    void testClearAllUsers() throws DataAccessException {
        // Insert a user
        userDataAccess.insertUser(new UserData("userToClear", "hashedPassword", "clear@mail.com"));

        // Clear all users
        userDataAccess.clearAllUsers();

        // Assert that there are no users left
        List<UserData> allUsers = userDataAccess.getAllUsers();
        assertTrue(allUsers.isEmpty());
    }
}

