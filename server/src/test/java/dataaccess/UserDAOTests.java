package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTests {
    private static UserDAO userDAO;

    @BeforeAll
    public static void initUserDAO() throws Exception {
        userDAO = new MySQLUserDAO();
    }

    @Test
    @Order(1)
    @DisplayName("Positive createUser test")
    public void createUserPosTest() throws ResponseException, DataAccessException {
        UserData userData = new UserData("username", "password", "email");

        userDAO.clearUsers();
        userDAO.createUser(userData);

        UserData expected = new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email");
        UserData actual = userDAO.getUser("username");

        Assertions.assertEquals(expected.username(), actual.username(), "Usernames do not match");
        Assertions.assertTrue(BCrypt.checkpw("password", actual.password()), "Passwords do not match");
        Assertions.assertEquals(expected.email(), actual.email(), "Emails do not match");
    }

    @Test
    @Order(2)
    @DisplayName("Positive getUser test")
    public void getUserPosTest() throws ResponseException, DataAccessException {
        createUserPosTest();
    }

    @Test
    @Order(3)
    @DisplayName("Positive clearUsers test")
    public void clearUsersPosTest() throws ResponseException, DataAccessException {
        UserData userData1 = new UserData("username1", "password1", "email1");
        UserData userData2 = new UserData("username2", "password2", "email2");
        UserData userData3 = new UserData("username3", "password3", "email3");

        userDAO.createUser(userData1);
        userDAO.createUser(userData2);
        userDAO.createUser(userData3);

        userDAO.clearUsers();

        Assertions.assertNull(userDAO.getUser("username1"), "user #1 was not erased");
        Assertions.assertNull(userDAO.getUser("username2"), "user #2 was not erased");
        Assertions.assertNull(userDAO.getUser("username3"), "user #3 was not erased");
    }

    @Test
    @Order(4)
    @DisplayName("Negative createUser test")
    public void createUserNegTest() throws ResponseException, DataAccessException {
        UserData userData = new UserData("username", "password", "email");

        userDAO.clearUsers();
        userDAO.createUser(userData);

        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(userData), "Username should have already been taken");
    }

    @Test
    @Order(5)
    @DisplayName("Negative getUser test")
    public void getUserNegTest() throws ResponseException {
        userDAO.clearUsers();

        Assertions.assertNull(userDAO.getUser("username"), "User does not exist");
    }

}
