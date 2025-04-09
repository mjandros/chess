package server.websocket;

import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.UserGameCommand;
import chess.ChessMove;
import websocket.messages.ServerMessage;
import websocket.messages.types.LoadGameMessage;
import websocket.messages.types.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            //case CONNECT -> connect(command.getUsername(), session);
            //case MAKE_MOVE -> makeMove(command.getMove());
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    private void connect(String username, Session session, GameData game) throws IOException {
        connections.add(username, session);
        String message = String.format("%s has joined %s.", username, game.gameName()); //replace game name with real one
        var loadGame = new LoadGameMessage(game);
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);
    }

    private void makeMove(ChessMove move) throws IOException {

    }
    private void leave() {

    }
    private void resign() {

    }
}
