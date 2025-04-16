package dataaccess;

import model.AuthData;


public interface AuthDataAccess {
    void insertAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;


    void deleteAuth(String authToken) throws DataAccessException;

    void clearAllTokens() throws DataAccessException;

}
