package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;

import javax.websocket.*;
import java.net.URI;
import websocket.commands.UserGameCommand;

public class WebsocketCommunicator extends Endpoint {

    public Session session;
    public String authToken;
    public int gameID;

    public WebsocketCommunicator(String url, String authToken, int gameID) throws Exception {
        try {
            this.authToken = authToken;
            this.gameID = gameID;
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
    public void connect() throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void makeMove(ChessMove move) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void leave() throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void resign() throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void onOpen(Session session, EndpointConfig endpointConfig){

    }
}
