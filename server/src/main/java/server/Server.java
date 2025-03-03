package server;

import handler.*;
import spark.*;

import static spark.Spark.*;

public class Server {

    private final UserHandler userHandler;
    private final GameHandler gameHandler;

    public Server() {
        userHandler = new UserHandler();
        gameHandler = new GameHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        registerEndpoints();

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
    }
}
