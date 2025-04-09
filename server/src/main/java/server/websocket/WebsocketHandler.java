package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.UserGameCommand;
import chess.ChessMove;
import websocket.commands.types.MakeMoveCommand;
import websocket.messages.ServerMessage;
import websocket.messages.types.LoadGameMessage;
import websocket.messages.types.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;

    public WebsocketHandler(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResponseException, DataAccessException {
        UserGameCommand command;
        if (message.contains("move")) {
            command = new Gson().fromJson(message, MakeMoveCommand.class);
        } else {
            command = new Gson().fromJson(message, UserGameCommand.class);
        }
        GameData game = gameDAO.getGame(command.getGameID());
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getUsername(), session, game);
            case MAKE_MOVE -> makeMove(((MakeMoveCommand) command).getMove());
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    private void connect(String username, Session session, GameData game) throws IOException {
        connections.add(username, session);
        String message = String.format("%s has joined %s.", username, game.gameName());
        var loadGame = new LoadGameMessage(game);
        var notification = new NotificationMessage(message);
        connections.broadcast(username, loadGame);
        connections.broadcast(username, notification);
    }

    private void makeMove(ChessMove move) throws IOException {

    }
    private void leave() {

    }
    private void resign() {

    }
}
