package service;

import service.requests.*;
import service.results.*;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {
        String username = ""; //getUser; if none, createUser
        String authToken = ""; //createAuth
        return new RegisterResult(username, authToken);
    }
    public LoginResult login(LoginRequest loginRequest) {
        String username = ""; //getUser
        String authToken = ""; //createAuth
        return new LoginResult(username, authToken);
    }
    public void logout(LogoutRequest logoutRequest) {
        //getAuth, deleteAuth
    }
}
