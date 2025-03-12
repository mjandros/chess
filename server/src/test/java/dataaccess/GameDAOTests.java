package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameDAOTests {
    private static GameDAO gameDAO;

    @BeforeAll
    public static void initGameDAO() throws Exception {
        gameDAO = new MySQLGameDAO();
    }

    @Test
    @Order(1)
    @DisplayName("Positive createGame test")
    public void createGamePosTest() throws ResponseException, DataAccessException {
        gameDAO.clearGames();

        gameDAO.createGame("testGame");
        GameData actual = gameDAO.getGame(1);

        Assertions.assertNotNull(actual, "Game was not created");
        Assertions.assertEquals("testGame", actual.gameName(), "Game was given the wrong name");
    }

    @Test
    @Order(2)
    @DisplayName("Positive getGame test")
    public void getGamePosTest() throws ResponseException, DataAccessException {
        createGamePosTest();
    }

    @Test
    @Order(3)
    @DisplayName("Positive listGames test 1")
    public void listGamesPosTest1() throws ResponseException {
        gameDAO.clearGames();
        gameDAO.createGame("testGame");

        Collection<GameData> expected = List.of(new GameData(1, null, null, "testGame", new ChessGame()));
        Collection<GameData> actual = gameDAO.listGames();

        GameData expectedData = expected.iterator().next();
        GameData actualData = actual.iterator().next();

        Assertions.assertEquals(expectedData.gameID(), actualData.gameID(), "gameIDs do not match");
        Assertions.assertEquals(expectedData.whiteUsername(), actualData.whiteUsername(), "whiteUsernames do not match");
        Assertions.assertEquals(expectedData.blackUsername(), actualData.blackUsername(), "blackUsernames do not match");
        Assertions.assertEquals(expectedData.gameName(), actualData.gameName(), "gameNames do not match");
    }

    @Test
    @Order(4)
    @DisplayName("Positive updatePlayer test")
    public void updatePlayerPosTest() throws ResponseException, DataAccessException {
        gameDAO.clearGames();
        gameDAO.createGame("testGame");

        gameDAO.updatePlayer(1, "WHITE", "whiteUsername");

        GameData actual = gameDAO.getGame(1);
        Assertions.assertEquals("whiteUsername", actual.whiteUsername(), "whiteUsername was not updated correctly");
        Assertions.assertNull(actual.blackUsername(), "blackUsername should not have been updated");

        gameDAO.updatePlayer(1, "BLACK", "blackUsername");

        actual = gameDAO.getGame(1);
        Assertions.assertEquals("blackUsername", actual.blackUsername(), "blackUsername was not updated correctly");
    }

    @Test
    @Order(5)
    @DisplayName("Positive clearGames test")
    public void clearGamesPosTest() throws ResponseException, DataAccessException {
        gameDAO.createGame("testGame1");
        gameDAO.createGame("testGame2");
        gameDAO.createGame("testGame3");

        gameDAO.clearGames();

        Assertions.assertThrows(ResponseException.class, () -> gameDAO.getGame(1), "game #1 was not erased");
        Assertions.assertThrows(ResponseException.class, () -> gameDAO.getGame(2), "game #2 was not erased");
        Assertions.assertThrows(ResponseException.class, () -> gameDAO.getGame(3), "game #3 was not erased");
    }

    @Test
    @Order(6)
    @DisplayName("Negative createGame test")
    public void createGameNegTest() {
        Assertions.assertThrows(Exception.class, () -> gameDAO.createGame(null));
    }

    @Test
    @Order(7)
    @DisplayName("Negative getGame test")
    public void getGameNegTest() throws ResponseException {
        gameDAO.clearGames();

        Assertions.assertThrows(ResponseException.class, () -> gameDAO.getGame(1), "Game should not exist");
    }

    @Test
    @Order(8)
    @DisplayName("Positive listGames test 2")
    public void listGamesPosTest2() throws ResponseException {
        gameDAO.clearGames();

        Collection<GameData> actual = gameDAO.listGames();

        Assertions.assertEquals(0, actual.size(), "List should be empty");
    }

    @Test
    @Order(9)
    @DisplayName("Negative updatePlayer test")
    public void updatePlayerNegTest() throws ResponseException, DataAccessException {
        gameDAO.clearGames();
        gameDAO.createGame("testGame");

        gameDAO.updatePlayer(1, "WHITE", "whiteUsername");

        Assertions.assertThrows(DataAccessException.class, () ->
                gameDAO.updatePlayer(1, "WHITE", "otherWhiteUsername"), "whiteUsername should already be taken");
    }
}
