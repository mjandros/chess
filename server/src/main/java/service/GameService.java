package service;

import dataaccess.*;
import service.requests.*;
import service.results.*;
import java.util.ArrayList;
import model.GameData;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

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
