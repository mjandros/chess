package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import service.UserService;
import service.requests.*;
import service.results.*;
import spark.Response;
import spark.Request;
import spark.Response;
import service.GameService;

public class GameHandler {

    private final Gson serializer;
    private final GameService gameService;

    public GameHandler() {
        serializer = new Gson();
        gameService = new GameService();
    }

    public ListGamesResult listGames(Request request, Response response) {
        try {
            return gameService.listGames(serializer.fromJson(request.body(), ListGamesRequest.class));
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format: " + e.getMessage());
            return null;
        }
    }

    public CreateGameResult createGame(Request request, Response response) {
        try {
            return gameService.createGame(serializer.fromJson(request.body(), CreateGameRequest.class));
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format: " + e.getMessage());
            return null;
        }
    }

    public JoinGameResult joinGame(Request request, Response response) {
        try {
            return gameService.joinGame(serializer.fromJson(request.body(), JoinGameRequest.class));
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format: " + e.getMessage());
            return null;
        }
    }

}
