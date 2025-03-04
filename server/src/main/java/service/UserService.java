package service;

import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Response;
import service.requests.*;
import service.results.*;
import dataaccess.*;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }
    public RegisterResult register(RegisterRequest req) throws ResponseException {
        try {
            UserData userData = new UserData(req.username(), req.password(), req.email());
            userDAO.createUser(userData);
            AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
            authDAO.createAuth(authData);
            return new RegisterResult(userData.username(), authData.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(403, "Error: already taken");
        }
    }
    public LoginResult login(LoginRequest req) throws ResponseException {
        UserData userData = userDAO.getUser(req.username());
        if (userData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        if (!userData.password().equals(req.password())) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        boolean success = false;
        while (!success) {
            try {
                authData = new AuthData(UUID.randomUUID().toString(), userData.username());
                authDAO.createAuth(authData);
                success = true;
            } catch (DataAccessException ignored) {
            }
        }
        return new LoginResult(userData.username(), authData.authToken());
    }
    public void logout(String authToken, LogoutRequest req) throws ResponseException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }
}
