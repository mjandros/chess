package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import service.exceptions.BadRequestException;
import service.exceptions.UsernameTakenException;
import service.requests.*;
import service.results.*;
import spark.Response;
import spark.Request;
import spark.Response;
import service.UserService;
import exception.ResponseException;

import java.util.Map;

public class UserHandler {

    private final Gson serializer;
    private final UserService userService;

    public UserHandler() {
        serializer = new Gson();
        userService = new UserService();
    }

    public RegisterResult register(Request request, Response response) throws ResponseException {
        try {
            return userService.register(serializer.fromJson(request.body(), RegisterRequest.class));
        } catch (JsonSyntaxException e) {
            throw new ResponseException(400, "Error: bad request");
        } catch (ResponseException e) {
            throw new ResponseException(403, "Error: already taken");
        }
    }

    public LoginResult login(Request request, Response response) {
        try {
            return userService.login(serializer.fromJson(request.body(), LoginRequest.class));
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format: " + e.getMessage());
            return null;
        }
    }

    public LogoutResult logout(Request request, Response response) {
        try {
            userService.logout(serializer.fromJson(request.body(), LogoutRequest.class));
            return new LogoutResult();
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format: " + e.getMessage());
            return null;
        }
    }
}
