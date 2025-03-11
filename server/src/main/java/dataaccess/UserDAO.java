package dataaccess;

import exception.ResponseException;
import model.UserData;

public interface UserDAO {

    void createUser(UserData userData) throws DataAccessException, ResponseException;
    UserData getUser(String username) throws ResponseException;
    void clearUsers() throws ResponseException;
}
