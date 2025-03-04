package dataaccess;

import java.util.Collection;
import java.util.HashMap;
import model.GameData;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    public void createGame(GameData gameData) throws DataAccessException {
        if (games.containsKey(gameData.gameID())) {
            throw new DataAccessException("Game already exists");
        }
        games.put(gameData.gameID(), gameData);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game does not exist");
        }
        return games.get(gameID);
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void updatePlayer(int gameID, String playerColor, String username) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game does not exist");
        }
        GameData currentGame = games.get(gameID);
        GameData updatedGame;
        if (playerColor.equals("WHITE")) {
            updatedGame = new GameData(gameID, username, currentGame.blackUsername(), currentGame.gameName(), currentGame.game());
        } else if (playerColor.equals("BLACK")) {
            updatedGame = new GameData(gameID, currentGame.whiteUsername(), username, currentGame.gameName(), currentGame.game());
        } else {
            throw new DataAccessException("Invalid player color");
        }
        games.put(gameID, updatedGame);
    }

    public void clearGames() {
        games.clear();
    }
}
