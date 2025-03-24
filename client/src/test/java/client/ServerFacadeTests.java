package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDB() throws ResponseException { facade.clear(); }


    @Test
    void registerPos() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData.authToken().length() > 10);
    }
    @Test
    void registerNeg() {
        assertThrows(ResponseException.class, () -> facade.register(null, null, null));
    }
    @Test
    void loginPos() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.logout(authData.authToken());
        assertDoesNotThrow(() -> facade.login("player1", "password"));
    }
    @Test
    void loginNeg() {
        assertThrows(ResponseException.class, () -> facade.login("unregistered_username", "fsdnjskg"));
    }
    @Test
    void logoutPos() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }
    @Test
    void logoutNeg() {
        assertThrows(ResponseException.class, () -> facade.logout("fakeToken"));
    }
    @Test
    void createGamePos() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertDoesNotThrow(() -> facade.createGame("game1", authData.authToken()));
    }
    @Test
    void createGameNeg() {
        assertThrows(ResponseException.class, () -> facade.createGame("game1", "fakeToken"));
    }
    @Test
    void listGamesPos() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertDoesNotThrow(() -> facade.listGames(authData.authToken()));
    }
    @Test
    void listGamesNeg() {
        assertThrows(ResponseException.class, () -> facade.listGames("fakeToken"));
    }
    @Test
    void joinGamePos() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("game1", authData.authToken());
        facade.listGames(authData.authToken());
        assertDoesNotThrow(() -> facade.joinGame("WHITE", 1, authData.authToken()));
    }
    @Test
    void joinGameNeg() throws ResponseException {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("game1", authData.authToken());
        facade.listGames(authData.authToken());
        assertThrows(ResponseException.class, () -> facade.joinGame("WHITE", 2, authData.authToken()));
    }

}
