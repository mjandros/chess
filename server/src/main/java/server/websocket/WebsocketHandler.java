package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.UserGameCommand;
import chess.ChessMove;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getUsername(), session);
            case MAKE_MOVE -> makeMove(command.getMove());
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    private void connect(String username, Session session) throws IOException {
        connections.add(username, session);
        String message = String.format("%s has joined %s.", username, "game name"); //replace game name with real one
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message);
        connections.broadcast(username, notification);
    }

    private void makeMove(ChessMove move) throws IOException {

    }
    private void leave() {

    }
    private void resign() {

    }
}
