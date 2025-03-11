package dataaccess;

import model.UserData;

public class MySQLUserDAO implements UserDAO {
    public void createUser(UserData userData) throws DataAccessException {
    }
    public UserData getUser(String username) {
        return new UserData("", "", "");
    }
    public void clearUsers() {

    }
}
