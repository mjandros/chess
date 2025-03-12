package service;

import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
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

    public JoinGameResult joinGame(String authToken, JoinGameRequest req) throws ResponseException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            gameDAO.updatePlayer(req.gameID(), req.playerColor(), authData.username());
            return new JoinGameResult();
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Token does not exist")){
                throw new ResponseException(401, "Error: unauthorized");
            } else if (e.getMessage().equals("already taken")) {
                throw new ResponseException(403, "Error: already taken");
            } else {
                throw new ResponseException(400, "Error: bad request");
            }
        } catch (Exception e) {
            throw new ResponseException(400, "Error: bad request");
        }
    }

}
