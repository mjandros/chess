package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.results.*;
import model.requests.*;
import spark.Response;
import spark.Request;
import service.UserService;
import exception.ResponseException;

public class UserHandler {

    private final Gson serializer;
    private final UserService userService;

    public UserHandler(UserService userService) {
        serializer = new Gson();
        this.userService = userService;
    }

    public String register(Request request, Response response) throws ResponseException {
        try {
            RegisterResult res = userService.register(serializer.fromJson(request.body(), RegisterRequest.class));
            response.status(200);
            return serializer.toJson(res);
        } catch (JsonSyntaxException e) {
            throw new ResponseException(400, "Error: bad request");
        } catch (ResponseException e) {
            if (e.getMessage().equals("Error: already taken")){
                throw new ResponseException(403, "Error: already taken");
            } else {
                throw new ResponseException(400, "Error: bad request");
            }
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    public String login(Request request, Response response) throws ResponseException {
        try {
            LoginResult res = userService.login(serializer.fromJson(request.body(), LoginRequest.class));
            response.status(200);
            return serializer.toJson(res);
        } catch (ResponseException e) {
            throw new ResponseException(401, "Error: unauthorized");
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    public String logout(Request request, Response response) throws ResponseException {
        try {
            userService.logout(request.headers("authorization"), serializer.fromJson(request.body(), LogoutRequest.class));
            response.status(200);
            return serializer.toJson(new LogoutResult());
        } catch (ResponseException e) {
            throw new ResponseException(401, "Error: unauthorized");
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }
}
