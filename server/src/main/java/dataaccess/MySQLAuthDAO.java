package dataaccess;

import exception.ResponseException;
import model.AuthData;

import java.sql.ResultSet;

public class MySQLAuthDAO implements AuthDAO {

    public MySQLAuthDAO() {
        configureDatabaseCaller();
    }
    public void createAuth(AuthData authData) throws ResponseException {
        var statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, authData.authToken(), authData.username());
    }
    public AuthData getAuth(String authToken) throws ResponseException, DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auths WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        throw new DataAccessException("Token does not exist");
    }
    public void deleteAuth(String authToken) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auths WHERE authToken = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new DataAccessException("Token does not exist");
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }
    public void clearAuths() throws ResponseException {
        executeUpdate("TRUNCATE auths");
    }

    private AuthData readAuth(ResultSet rs) throws Exception {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private void executeUpdate(String statement, Object... params) throws ResponseException {
        MySQLUserDAO.executeUpdate(statement, params);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
             `authToken` varchar(255) NOT NULL,
             `username` varchar(255) NOT NULL,
             PRIMARY KEY (authToken)
             )
            """
    };

    private void configureDatabaseCaller() {
        MySQLUserDAO.configureDatabase(createStatements);
    }
}
