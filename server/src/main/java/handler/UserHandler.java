package handler;

import com.google.gson.Gson;
import model.*;
import com.google.gson.JsonSyntaxException;

public class UserHandler {

    public UserData registerHandler(spark.Request request) {
        Gson serializer = new Gson();
        try {
            return serializer.fromJson(request.body(), UserData.class);
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format: " + e.getMessage());
            return null;
        }
    }
}
