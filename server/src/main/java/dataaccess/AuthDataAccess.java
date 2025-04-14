package dataaccess;

import model.AuthData;

import java.util.Map;

public interface AuthDataAccess {
    void insertAuth(AuthData authData) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    Map<String, AuthData> getAllAuthTokens();
    void setAuthTokens(Map<String, AuthData> newTokens);

}
