package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import chess.ChessMove;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(session);
            case MAKE_MOVE -> makeMove(command.getMove());
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    private void connect(Session session) throws IOException {

    }

    private void makeMove(ChessMove move) throws IOException {

    }
    private void leave() {

    }
    private void resign() {

    }
}
