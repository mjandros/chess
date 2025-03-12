package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthDAOTests {
    private static AuthDAO authDAO;

    @BeforeAll
    public static void initAuthDAO() throws Exception {
        authDAO = new MySQLAuthDAO();
    }

    @Test
    @Order(1)
    @DisplayName("Positive createAuth test")
    public void createAuthPosTest() throws ResponseException, DataAccessException {
        AuthData authData = new AuthData("authToken", "username");

        authDAO.clearAuths();
        authDAO.createAuth(authData);

        AuthData expected = new AuthData("authToken", "username");
        AuthData actual = authDAO.getAuth("authToken");

        Assertions.assertEquals(expected.username(), actual.username(), "Usernames do not match");
        Assertions.assertEquals(expected.authToken(), actual.authToken(), "authTokens do not match");
    }

    @Test
    @Order(2)
    @DisplayName("Positive getAuth test")
    public void getAuthPosTest() throws ResponseException, DataAccessException {
        createAuthPosTest();
    }

    @Test
    @Order(3)
    @DisplayName("Positive deleteAuth test")
    public void deleteAuthPosTest() throws ResponseException, DataAccessException {
        authDAO.clearAuths();
        authDAO.createAuth(new AuthData("authToken", "username"));

        authDAO.deleteAuth("authToken");

        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("authToken"), "Auth was not deleted");
    }

    @Test
    @Order(4)
    @DisplayName("Positive clearAuths test")
    public void clearAuthsPosTest() throws ResponseException, DataAccessException {
        AuthData authData1 = new AuthData("token1", "username1");
        AuthData authData2 = new AuthData("token2", "username2");
        AuthData authData3 = new AuthData("token3", "username3");

        authDAO.createAuth(authData1);
        authDAO.createAuth(authData2);
        authDAO.createAuth(authData3);

        authDAO.clearAuths();

        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("token1"), "Auth #1 was not deleted");
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("token2"), "Auth #2 was not deleted");
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("token3"), "Auth #3 was not deleted");
    }

    @Test
    @Order(5)
    @DisplayName("Negative getUser test")
    public void getUserNegTest() throws ResponseException {

    }

}
