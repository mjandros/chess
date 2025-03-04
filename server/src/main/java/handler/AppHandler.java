package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import exception.ResponseException;
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

    public String clear(Request request, Response response) throws ResponseException {
        try {
            ClearResult res = appService.clear(serializer.fromJson(request.body(), ClearRequest.class));
            response.status(200);
            return serializer.toJson(res);
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

}
