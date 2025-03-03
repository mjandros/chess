package server;

import handler.UserHandler;
import spark.*;

import static spark.Spark.post;

public class Server {

    private final UserHandler userHandler;

    public Server() {
        userHandler = new UserHandler();
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
        post("/user", userHandler::register);
    }
}
