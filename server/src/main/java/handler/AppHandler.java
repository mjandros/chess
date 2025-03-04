package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import service.requests.*;
import service.results.*;
import spark.Response;
import spark.Request;
import spark.Response;
import service.AppService;

public class AppHandler {

    private final Gson serializer;
    private final AppService appService;

    public AppHandler(AppService appService) {
        serializer = new Gson();
        this.appService = appService;
    }

    public ClearResult clear(Request request, Response response) {
        try {
            return appService.clear(serializer.fromJson(request.body(), ClearRequest.class));
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format: " + e.getMessage());
            return null;
        }
    }

}
