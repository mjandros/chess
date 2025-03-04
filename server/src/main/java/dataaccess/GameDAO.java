package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames();
    void updatePlayer(int gameID, String playerColor, String username) throws DataAccessException;
    void clearGames();
}
