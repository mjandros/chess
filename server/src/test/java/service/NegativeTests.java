package service;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import service.*;
import model.requests.*;
import model.results.*;
import dataaccess.*;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NegativeTests {

    private static UserDAO userDAONeg;
    private static GameDAO gameDAONeg;
    private static AuthDAO authDAONeg;
    private static UserService userServiceNeg;
    private static GameService gameServiceNeg;
    private static AppService appServiceNeg;

    @BeforeAll
    public static void initNegative() throws Exception {
        userDAONeg = new MySQLUserDAO();
        gameDAONeg = new MySQLGameDAO();
        authDAONeg = new MySQLAuthDAO();

        userServiceNeg = new UserService(userDAONeg, authDAONeg);
        gameServiceNeg = new GameService(gameDAONeg, authDAONeg);
        appServiceNeg = new AppService(userDAONeg, gameDAONeg, authDAONeg);
    }

    @Test
    @DisplayName("Negative register test")
    public void registerTest() {
        RegisterRequest req = new RegisterRequest(null, "password", "email");

        Assertions.assertThrows(ResponseException.class, () -> userServiceNeg.register(req), "Bad request expected");
    }

    @Test
    @DisplayName("Negative logout test")
    public void logoutTest() {
        LogoutRequest req = new LogoutRequest();

        Assertions.assertThrows(ResponseException.class, () -> userServiceNeg.logout("", req), "unauthorized expected");
    }

    @Test
    @DisplayName("Negative login test")
    public void loginTest() {
        LoginRequest req = new LoginRequest("username", "password");

        Assertions.assertThrows(ResponseException.class, () -> userServiceNeg.login(req), "unauthorized expected");
    }

    @Test
    @DisplayName("Negative createGame test")
    public void createGameTest() {
        CreateGameRequest req = new CreateGameRequest("gameName");

        Assertions.assertThrows(ResponseException.class, () -> gameServiceNeg.createGame("", req), "unauthorized expected");
    }

    @Test
    @DisplayName("Negative joinGame test")
    public void joinGameTest() {
        JoinGameRequest req = new JoinGameRequest("WHITE", 1);

        Assertions.assertThrows(ResponseException.class, () -> gameServiceNeg.joinGame("", req), "unauthorized expected");
    }

    @Test
    @DisplayName("Negative listGames test")
    public void listGamesTest() {
        ListGamesRequest req = new ListGamesRequest();

        Assertions.assertThrows(ResponseException.class, () -> gameServiceNeg.listGames("", req), "unauthorized expected");
    }

}
