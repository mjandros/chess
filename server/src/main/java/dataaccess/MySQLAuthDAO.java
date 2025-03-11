package dataaccess;

import model.AuthData;

public class MySQLAuthDAO implements AuthDAO {
    public void createAuth(AuthData authData) throws DataAccessException {

    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        return new AuthData("", "");
    }
    public void deleteAuth(String authToken) throws DataAccessException {

    }
    public void clearAuths() {

    }
}
