package service;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import service.*;
import service.requests.*;
import service.results.*;
import dataaccess.*;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NegativeTests {

    private static UserDAO userDAO;
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;
    private static UserService userService;
    private static GameService gameService;
    private static AppService appService;

    @BeforeAll
    public static void init() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        appService = new AppService(userDAO, gameDAO, authDAO);
    }

    @Test
    @DisplayName("Negative register test")
    public void registerTest() {
        RegisterRequest req = new RegisterRequest(null, "password", "email");

        Assertions.assertThrows(ResponseException.class, () -> userService.register(req), "Bad request expected");
    }

    @Test
    @DisplayName("Negative logout test")
    public void logoutTest() {
        LogoutRequest req = new LogoutRequest();

        Assertions.assertThrows(ResponseException.class, () -> userService.logout("", req), "unauthorized expected");
    }

    @Test
    @DisplayName("Positive login test")
    public void loginTest() {
        LoginRequest req = new LoginRequest("username", "password");

        Assertions.assertThrows(ResponseException.class, () -> userService.login(req), "unauthorized expected");
    }

    @Test
    @DisplayName("Negative createGame test")
    public void createGameTest() {
        CreateGameRequest req = new CreateGameRequest("gameName");

        Assertions.assertThrows(ResponseException.class, () -> gameService.createGame("", req), "unauthorized expected");
    }

    @Test
    @DisplayName("Negative joinGame test")
    public void joinGameTest() {
        JoinGameRequest req = new JoinGameRequest("WHITE", 1);

        Assertions.assertThrows(ResponseException.class, () -> gameService.joinGame("", req), "unauthorized expected");
    }

    @Test
    @DisplayName("Negative listGames test")
    public void listGamesTest() {
        ListGamesRequest req = new ListGamesRequest();

        Assertions.assertThrows(ResponseException.class, () -> gameService.listGames("", req), "unauthorized expected");
    }

}
