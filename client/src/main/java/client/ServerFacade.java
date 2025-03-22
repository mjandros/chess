package client;

import com.google.gson.Gson;
import exception.ResponseException;
import service.requests.*;
import service.results.*;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final int port;

    public ServerFacade(int port){
        this.port = port;
    }
    public LoginResult login(String username, String password) throws ResponseException {
        LoginRequest req = new LoginRequest(username, password);
        return makeRequest("POST", "/session", req, LoginResult.class);
    }
    public RegisterResult register(String username, String password, String email) throws ResponseException {
        RegisterRequest req = new RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", req, RegisterResult.class);
    }
    public LogoutResult logout() throws ResponseException {
        LogoutRequest req = new LogoutRequest();
        return makeRequest("DELETE", "/session", req, LogoutResult.class);
    }
    public CreateGameResult createGame(String gameName) throws ResponseException {
        CreateGameRequest req = new CreateGameRequest(gameName);
        return makeRequest("POST", "/game", req, CreateGameResult.class);
    }
    public ListGamesResult listGames() throws ResponseException {
        ListGamesRequest req = new ListGamesRequest();
        return makeRequest("GET", "/game", req, ListGamesResult.class);
    }
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI("http://localhost:" + port + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
