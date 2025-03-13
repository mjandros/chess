package server;

import handler.*;
import service.*;
import dataaccess.*;
import spark.*;
import exception.ResponseException;

import static spark.Spark.*;

public class Server {

    private final UserHandler userHandler;
    private final GameHandler gameHandler;
    private final AppHandler appHandler;

    private final UserService userService;
    private final GameService gameService;
    private final AppService appService;

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public Server() {
        userDAO = new MySQLUserDAO();
        gameDAO = new MySQLGameDAO();
        authDAO = new MySQLAuthDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        appService = new AppService(userDAO, gameDAO, authDAO);

        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);
        appHandler = new AppHandler(appService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        registerEndpoints();
        Spark.exception(ResponseException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void registerEndpoints() {

        //User Endpoints
        post("/user", userHandler::register);
        post("/session", userHandler::login);
        delete("/session", userHandler::logout);

        //Game Endpoints
        get("/game", gameHandler::listGames);
        post("/game", gameHandler::createGame);
        put("/game", gameHandler::joinGame);

        //App Endpoints
        delete("/db", appHandler::clear);
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }
}
