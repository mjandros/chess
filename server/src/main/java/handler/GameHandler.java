package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import exception.ResponseException;
import service.requests.*;
import service.results.*;
import spark.Response;
import spark.Request;
import spark.Response;
import service.GameService;

public class GameHandler {

    private final Gson serializer;
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        serializer = new Gson();
        this.gameService = gameService;
    }

    public String listGames(Request request, Response response) throws ResponseException {
        try {
            ListGamesResult res = gameService.listGames(request.headers("authorization"), serializer.fromJson(request.body(), ListGamesRequest.class));
            response.status(200);
            return serializer.toJson(res);
        } catch (ResponseException e) {
            throw new ResponseException(401, "Error: unauthorized");
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    public String createGame(Request request, Response response) throws ResponseException {
        try {
            CreateGameResult res = gameService.createGame(request.headers("authorization"), serializer.fromJson(request.body(), CreateGameRequest.class));
            response.status(200);
            return serializer.toJson(res);
        } catch (JsonSyntaxException e) {
            throw new ResponseException(400, "Error: bad request");
        } catch (ResponseException e) {
            throw new ResponseException(401, "Error: unauthorized");
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
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
