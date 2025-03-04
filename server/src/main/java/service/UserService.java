package service;

import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import service.requests.*;
import service.results.*;
import dataaccess.*;
import java.util.UUID;
import spark.Response;

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
    public LoginResult login(LoginRequest req) {
        String username = ""; //getUser
        String authToken = ""; //createAuth
        return new LoginResult(username, authToken);
    }
    public void logout(LogoutRequest req) {
        //getAuth, deleteAuth
    }
}
