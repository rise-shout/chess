package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthTokenDAO implements AuthDataAccess{
    private static AuthTokenDAO instance;
    private static Map<String, AuthData> authTokenMap;

    public AuthTokenDAO() {
        authTokenMap = new HashMap<>();
    }

    public static AuthTokenDAO getInstance() {
        if (instance == null) {
            instance = new AuthTokenDAO();
        }
        return instance;
    }

    public void insertAuth(AuthData authData) throws DataAccessException {
        try {
            authTokenMap.put(authData.authToken(), authData);
            System.out.println("Auth token stored: " + authData.authToken());
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


    @Override
    public void clearAllTokens() throws DataAccessException {
        authTokenMap.clear();
    }

    public void reset() {
    }
}