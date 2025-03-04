package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthTokenDAO {
    private final Map<String, AuthData> authTokenMap;

    public AuthTokenDAO() {
        this.authTokenMap = new HashMap<>();
    }

    public void insertAuth(AuthData authData) throws DataAccessException {
        try {
            authTokenMap.put(authData.authToken(), authData);
        } catch (Exception e) {
            throw new DataAccessException("Failed to insert auth token: " + e.getMessage());
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try {
            return authTokenMap.get(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Failed to retrieve auth token: " + e.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        try {
            authTokenMap.remove(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Failed to delete auth token: " + e.getMessage());
        }
    }

    // Method to clear all auth tokens
    public void clearAllAuthTokens() throws DataAccessException {
        try {
            authTokenMap.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear all auth tokens: " + e.getMessage());
        }
    }
}