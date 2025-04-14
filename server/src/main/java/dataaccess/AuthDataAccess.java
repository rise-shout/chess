package dataaccess;

import model.AuthData;

import java.util.Map;

public interface AuthDataAccess {
    void insertAuth(AuthData authData) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;

    String getAuthToken(String username) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    Map<String, AuthData> getAllAuthTokens() throws DataAccessException;
    void setAuthTokens(Map<String, AuthData> newTokens) throws DataAccessException;

    void clearAllTokens() throws DataAccessException;

}
