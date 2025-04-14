package dataaccess;

import model.AuthData;

import java.util.Map;

import static dataaccess.MySqlUserDataAccess.updateDoer;

public class MySqlAuthDataAccess implements AuthDataAccess {

    public MySqlAuthDataAccess()  {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void insertAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public Map<String, AuthData> getAllAuthTokens() {
        return null;
    }

    @Override
    public void setAuthTokens(Map<String, AuthData> newTokens) {

    }

    private void configureDatabase() throws DataAccessException {
        MySqlUserDataAccess.databaseMaker(createStatements);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {

        return updateDoer(statement, params);
    }

    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS game (
      username VARCHAR(256) NOT NULL UNIQUE,
      auth_token VARCHAR(256) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };
}
