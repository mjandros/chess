package service;

import service.requests.*;
import service.results.*;

public class UserService {
    public RegisterResult register(RegisterRequest req) {
        String username = ""; //getUser; if none, createUser
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
