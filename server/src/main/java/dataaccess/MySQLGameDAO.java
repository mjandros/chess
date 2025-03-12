package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLGameDAO implements GameDAO {

    public MySQLGameDAO() throws Exception {
        configureDatabaseCaller();
    }
    public int createGame(String gameName) throws ResponseException {
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        ChessGame game = new ChessGame();
        var json = new Gson().toJson(game);
        return executeUpdate(statement, null, null, gameName, json);
    }

    public GameData getGame(int id) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    } else {
                        System.out.println("Game don't exist");
                        throw new DataAccessException("Game does not exist");
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public Collection<GameData> listGames() throws ResponseException {
        System.out.println("listing games");
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            System.out.println("a");
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                System.out.println("b");
                try (var rs = ps.executeQuery()) {
                    System.out.println("c");
                    while (rs.next()) {
                        System.out.println("d");
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            System.out.printf("error: %s\n", e.getMessage());
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        System.out.println(result);
        System.out.println(result.getFirst());
        System.out.println(result.getLast());
        return result;
    }

    public void updatePlayer(int gameID, String playerColor, String username) throws ResponseException, DataAccessException {
        System.out.println("updating player");
        getGame(gameID);
        var statement = "";
        if (Objects.equals(playerColor, "WHITE")) {
            statement = "UPDATE games SET whiteUsername = ? WHERE id = ?";
        } else if (Objects.equals(playerColor, "BLACK")) {
            statement = "UPDATE games SET blackUsername = ? WHERE id = ?";
        } else {
            throw new DataAccessException("Error: Invalid player color");
        }
        executeUpdate(statement, username, gameID);
    }

    public void clearGames() throws ResponseException {
        executeUpdate("TRUNCATE games");
    }

    private GameData readGame(ResultSet rs) throws Exception {
        var id = rs.getInt("id");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var json = rs.getString("game");
        var game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(id, whiteUsername, blackUsername, gameName, game);
    }

    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            System.out.println("A");
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                System.out.println("B");
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case ChessGame p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                System.out.println(ps);
                int rows = ps.executeUpdate();
                System.out.println(rows);
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    System.out.println("E");
                    return rs.getInt(1);
                }
                System.out.println("F");
                return 0;
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
             `id` int NOT NULL auto_increment,
             `whiteUsername` varchar(255),
             `blackUsername` varchar(255),
             `gameName` varchar(255) NOT NULL,
             `game` text NOT NULL,
             PRIMARY KEY (id)
             )
            """
    };

    private void configureDatabaseCaller() throws Exception {
        MySQLUserDAO.configureDatabase(createStatements);
    }
}
