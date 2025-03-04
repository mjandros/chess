package service;

import exception.ResponseException;
import service.requests.*;
import service.results.*;
import dataaccess.UserDAO;
import dataaccess.MemoryUserDAO;
import spark.Response;

public class UserService {
    private final UserDAO da;

    public UserService() {
        da = new MemoryUserDAO();
    }
    public RegisterResult register(RegisterRequest req) throws ResponseException {
        String username = req.username();
        if (da.getUser(username) != null) {
            throw new ResponseException(403, "Error: already taken");
        }
        //getUser; if none, createUser
        String authToken = ""; //createAuth
        return new RegisterResult(username, authToken);
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
