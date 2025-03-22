package client;

import java.net.http.HttpClient;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient client;

    public ServerFacade(String serverUrl){
        this.serverUrl = serverUrl;
        this.client = HttpClient.newHttpClient();
    }
}
