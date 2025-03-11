package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDAO implements GameDAO {
    public int createGame(String gameName) {
        return 0;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return new GameData(0, "", "", "", new ChessGame());
    }

    public Collection<GameData> listGames() {
        return new ArrayList<>();
    }

    public void updatePlayer(int gameID, String playerColor, String username) throws DataAccessException {

    }

    public void clearGames() {

    }
}
