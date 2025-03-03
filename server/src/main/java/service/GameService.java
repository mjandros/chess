package service;

import service.requests.*;
import service.results.*;
import java.util.ArrayList;
import model.GameData;

public class GameService {

    public ListGamesResult listGames(ListGamesRequest req) {
        ArrayList<GameData> games = new ArrayList<>();
        //getAuth
        //listGames
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest req) {
        int gameID = 0;
        //getAuth
        //createGame
        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(JoinGameRequest req) {
        //getAuth
        //getGame
        //updateGame
        return new JoinGameResult();
    }
}
