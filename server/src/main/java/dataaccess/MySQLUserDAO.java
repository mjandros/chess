package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLUserDAO implements UserDAO {

    //private final UserDAO memory;

    public MySQLUserDAO() throws Exception {
        configureDatabaseCaller();
        //memory = new MemoryUserDAO();
    }
    public void createUser(UserData userData) throws ResponseException, DataAccessException {
        if (userExists(userData.username())) {
            throw new DataAccessException("User already exists");
        }
        if (userData.username() == null || userData.password() == null) {
            throw new DataAccessException("Username or password is null");
        }
        var statement = "INSERT INTO users (username, pw, email) VALUES (?, ?, ?)";
        String hashedPassword = hashPassword(userData.password());
        System.out.printf("clear pw: %s; hashed pw: %s\n", userData.password(), hashedPassword);
        executeUpdate(statement, userData.username(), hashedPassword, userData.email());
    }
    public UserData getUser(String username) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, pw, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }
    public void clearUsers() throws ResponseException {
        executeUpdate("TRUNCATE users");
    }

    String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    private UserData readUser(ResultSet rs) throws Exception {
        var username = rs.getString("username");
        var password = rs.getString("pw");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    static void executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(255) NOT NULL,
              `pw` varchar(255) NOT NULL,
              `email` varchar(255) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """
    };

    private void configureDatabaseCaller() throws Exception {
        configureDatabase(createStatements);
    }

    static void configureDatabase(String[] createStatements) throws Exception {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new Exception(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public boolean userExists(String username) throws ResponseException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
        return false;
    }
}
