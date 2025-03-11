package dataaccess;

import exception.ResponseException;
import model.AuthData;

public interface AuthDAO {

    void createAuth(AuthData authData) throws DataAccessException, ResponseException;
    AuthData getAuth(String authToken) throws DataAccessException, ResponseException;
    void deleteAuth(String authToken) throws DataAccessException, ResponseException;
    void clearAuths() throws ResponseException;
}
