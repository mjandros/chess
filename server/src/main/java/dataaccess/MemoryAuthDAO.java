package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> auths = new HashMap<>();

    public void createAuth(AuthData authData) throws DataAccessException {
        if (auths.containsKey(authData.authToken())) {
            throw new DataAccessException("Token already exists");
        }
        auths.put(authData.authToken(), authData);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("Token does not exist");
        }
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("Token does not exist");
        }
        auths.remove(authToken);
    }

    public void clearAuths() {
        auths.clear();
    }
}
