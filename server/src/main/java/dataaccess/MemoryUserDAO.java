package dataaccess;

import java.util.HashMap;
import model.UserData;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData userData) throws DataAccessException {
        if (users.containsKey(userData.username())) {
            throw new DataAccessException("User already exists");
        }
        users.put(userData.username(), userData);
    }

    public UserData getUser(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("User does not exist");
        }
        return users.get(username);
    }

    public void clear() {
        users.clear();
    }
}
