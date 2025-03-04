package service;

import com.google.gson.JsonSyntaxException;
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

    public CreateGameResult createGame(String authToken, CreateGameRequest req) throws ResponseException {
        try {
            authDAO.getAuth(authToken);
            int gameID = gameDAO.createGame(req.gameName());
            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public JoinGameResult joinGame(JoinGameRequest req) {
        //getAuth
        //getGame
        //updateGame
        return new JoinGameResult();
    }
}
