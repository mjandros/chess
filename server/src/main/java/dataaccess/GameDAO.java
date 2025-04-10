package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName) throws ResponseException;
    GameData getGame(int gameID) throws DataAccessException, ResponseException;
    Collection<GameData> listGames() throws ResponseException;
    void updatePlayer(int gameID, String playerColor, String username) throws DataAccessException, ResponseException;
    void clearGames() throws ResponseException;
    void updateGame(int gameID, ChessGame game) throws ResponseException;
}
