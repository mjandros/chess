package service;

import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Response;
import org.mindrot.jbcrypt.BCrypt;
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
            if (e.getMessage().equals("User already exists")){
                throw new ResponseException(403, "Error: already taken");
            } else {
                throw new ResponseException(400, "Error: bad request");
            }
        }
    }
    public LoginResult login(LoginRequest req) throws ResponseException {
        System.out.println("logging in");
        UserData userData = userDAO.getUser(req.username());
        System.out.println("got user");
        if (userData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        System.out.printf("reg: %s, hashed: %s\n", req.password(), userData.password());
        if (!verifyUser(req.password(), readHashedPassword(req.username()))) {
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
    boolean verifyUser(String providedClearTextPassword, String hashedPassword) {
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }
    public void logout(String authToken, LogoutRequest req) throws ResponseException {
        try {
            authDAO.getAuth(authToken);
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    private String readHashedPassword(String username) throws ResponseException {
        UserData user = userDAO.getUser(username);
        return user.password();
    }
}
