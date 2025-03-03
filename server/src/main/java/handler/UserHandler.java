package handler;

import com.google.gson.Gson;
import model.*;
import com.google.gson.JsonSyntaxException;
import spark.Response;
import spark.Request;
import spark.Response;

public class UserHandler {

    private final Gson serializer;

    public UserHandler() {
        serializer = new Gson();
    }

    public UserData register(Request request, Response response) {
        try {
            return serializer.fromJson(request.body(), UserData.class);
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format: " + e.getMessage());
            return null;
        }
    }

    public UserData login(Request request, Response response) {
        try {
            return serializer.fromJson(request.body(), UserData.class);
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format: " + e.getMessage());
            return null;
        }
    }
}
