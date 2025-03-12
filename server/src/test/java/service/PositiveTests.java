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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PositiveTests {

    private static UserDAO userDAO;
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;
    private static UserService userService;
    private static GameService gameService;
    private static AppService appService;
    private static String authToken;

    @BeforeAll
    public static void initPositive() throws Exception {
        userDAO = new MySQLUserDAO();
        gameDAO = new MySQLGameDAO();
        authDAO = new MySQLAuthDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        appService = new AppService(userDAO, gameDAO, authDAO);
    }

    @Test
    @Order(1)
    @DisplayName("Positive register test")
    public void registerTest() throws ResponseException {
        RegisterRequest req = new RegisterRequest("username", "password", "email");

        RegisterResult expected = new RegisterResult("username", "");
        RegisterResult actual = userService.register(req);

        Assertions.assertEquals(expected.username(), actual.username(), "Usernames do not match");
        Assertions.assertNotNull(actual.authToken(), "No authToken generated");
        authToken = actual.authToken();
    }

    @Test
    @Order(2)
    @DisplayName("Positive logout test")
    public void logoutTest() throws ResponseException {
        LogoutRequest req = new LogoutRequest();
        Assertions.assertDoesNotThrow(() -> userService.logout(authToken, req), "Should logout successfully");
    }

    @Test
    @Order(3)
    @DisplayName("Positive login test")
    public void loginTest() throws ResponseException {
        LoginRequest req = new LoginRequest("username", "password");

        LoginResult expected = new LoginResult("username", "");
        LoginResult actual = userService.login(req);

        Assertions.assertEquals(expected.username(), actual.username(), "Usernames do not match");
        Assertions.assertNotNull(actual.authToken(), "No authToken generated");
        authToken = actual.authToken();
    }

    @Test
    @Order(4)
    @DisplayName("Positive createGame test")
    public void createGameTest() throws ResponseException, DataAccessException {
        CreateGameRequest req = new CreateGameRequest("gameName");

        CreateGameResult expected = new CreateGameResult(1);
        CreateGameResult actual = gameService.createGame(authToken, req);

        Assertions.assertEquals(expected.gameID(), actual.gameID(), "gameIDs do not match");
        Assertions.assertNotNull(gameDAO.getGame(actual.gameID()), "game does not exist");
    }

    @Test
    @Order(5)
    @DisplayName("Positive joinGame test")
    public void joinGameTest() throws ResponseException, DataAccessException {
        JoinGameRequest req = new JoinGameRequest("WHITE", 1);

        gameService.joinGame(authToken, req);

        Assertions.assertEquals(gameDAO.getGame(1).whiteUsername(), "username", "usernames do not match");
        Assertions.assertNull(gameDAO.getGame(1).blackUsername(), "blackUsername is not null");
    }

    @Test
    @Order(6)
    @DisplayName("Positive listGames test")
    public void listGamesTest() throws ResponseException, DataAccessException {
        ListGamesRequest req = new ListGamesRequest();

        ListGamesResult expected = new ListGamesResult(List.of(new GameData(1, "username", null, "gameName", new ChessGame())));
        ListGamesResult actual = gameService.listGames(authToken, req);

        List<GameData> expectedList = new ArrayList<>(expected.games());
        List<GameData> actualList = new ArrayList<>(actual.games());

        GameData expectedData = expectedList.getFirst();
        GameData actualData = actualList.getFirst();

        Assertions.assertEquals(expectedData.gameID(), actualData.gameID(), "gameIDs do not match");
        Assertions.assertEquals(expectedData.whiteUsername(), actualData.whiteUsername(), "whiteUsernames do not match");
        Assertions.assertEquals(expectedData.blackUsername(), actualData.blackUsername(), "blackUsernames do not match");
        Assertions.assertEquals(expectedData.gameName(), actualData.gameName(), "gameNames do not match");
    }

    @Test
    @Order(7)
    @DisplayName("Positive clear test")
    public void clearTest() throws ResponseException {
        ClearRequest req = new ClearRequest();

        appService.clear(req);

        Assertions.assertNull(userDAO.getUser("username"), "user was not erased");
        Assertions.assertThrows(ResponseException.class, () -> gameDAO.getGame(1), "game was not erased");
    }
}
