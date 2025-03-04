package service;

import dataaccess.*;
import exception.ResponseException;
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

    public ListGamesResult listGames(String authToken, ListGamesRequest req) throws ResponseException {
        try {
            authDAO.getAuth(authToken);
            return new ListGamesResult(gameDAO.listGames());
        } catch (DataAccessException e) {
            throw new ResponseException(401, "Error: unauthorized");
        }
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
