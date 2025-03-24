package service;

import dataaccess.*;
import exception.ResponseException;
import model.requests.ClearRequest;
import model.results.ClearResult;

public class AppService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public AppService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ClearResult clear(ClearRequest req) throws ResponseException {
        userDAO.clearUsers();
        gameDAO.clearGames();
        authDAO.clearAuths();
        return new ClearResult();
    }
}
