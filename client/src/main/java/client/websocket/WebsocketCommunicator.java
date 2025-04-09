package client.websocket;

import exception.ResponseException;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

public class WebsocketCommunicator extends Endpoint {

    public Session session;

    public WebsocketCommunicator(String url) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI uri = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    System.out.println(message);
                }
            });
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
    public void connect() {

    }
    public void makeMove() {

    }
    public void leave() {

    }
    public void resign() {

    }

    public void onOpen(Session session, EndpointConfig endpointConfig){

    }
}
