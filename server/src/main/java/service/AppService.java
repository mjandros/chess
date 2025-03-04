package service;

import dataaccess.*;
import service.requests.*;
import service.results.*;

public class AppService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public AppService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ClearResult clear(ClearRequest req) {
        //clear(UserDAO)
        //clear(GameDAO)
        //clear(AuthDAO)
        return new ClearResult();
    }
}
