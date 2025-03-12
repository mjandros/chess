package dataaccess;

import java.util.HashMap;

import exception.ResponseException;
import model.UserData;

public class MemoryUserDAO implements UserDAO {
    final public HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData userData) throws DataAccessException {
        if (users.containsKey(userData.username())) {
            throw new DataAccessException("User already exists");
        }
        if (userData.username() == null || userData.password() == null) {
            throw new DataAccessException("Username or password is null");
        }
        users.put(userData.username(), userData);
    }

    public UserData getUser(String username) throws ResponseException {
        System.out.println("why are we here");
        if (!users.containsKey(username)) {
            return null;
        }
        return users.get(username);
    }

    public void clearUsers() throws ResponseException {
        users.clear();
    }
}
