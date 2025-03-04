package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    // In-memory database simulation
    private static Map<String, UserData> userMap;

    public UserDAO() {
        userMap = new HashMap<>();
    }

    // Method to clear all users
    public void clearAllUsers() throws DataAccessException {
        try {
            userMap.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear users: " + e.getMessage());
        }
    }

    // Method to insert a new user
    public void insertUser(UserData user) throws DataAccessException {
        if (userMap.containsKey(user.username())) {
            throw new DataAccessException("User already exists with username: " + user.username());
        }
        try {
            userMap.put(user.username(), user);
        } catch (Exception e) {
            throw new DataAccessException("Failed to insert user: " + e.getMessage());
        }
    }

    // Method to retrieve a user by username
    public UserData getUser(String username) throws DataAccessException {
        try {
            return userMap.get(username);
        } catch (Exception e) {
            throw new DataAccessException("Failed to retrieve user: " + e.getMessage());
        }
    }

    // Additional methods as needed for other functionalities...
}