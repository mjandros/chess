package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
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
            case MAKE_MOVE -> makeMove(command.getUsername(), ((MakeMoveCommand) command).getMove());
            case LEAVE -> leave(command.getUsername());
            case RESIGN -> resign(command.getUsername());
        }
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }

    private void connect(String username, Session session, GameData game) throws IOException {
        connections.add(username, session);
        String message = String.format("%s has joined %s.", username, game.gameName());
        var loadGame = new LoadGameMessage(game);
        var notification = new NotificationMessage(message);
        connections.broadcast(username, loadGame);
        connections.broadcast(username, notification);
    }

    private void makeMove(String username, ChessMove move) throws IOException {
        String startPos = String.format("%s%s", ('a' + (move.getStartPosition().getColumn() - 1)), "" + move.getStartPosition().getRow());
        String endPos = String.format("%s%s", ('a' + (move.getEndPosition().getColumn() - 1)), "" + move.getEndPosition().getRow());
        String message = String.format("%s has moved their piece from %s to %s.", username, startPos, endPos);
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);
    }
    private void leave(String username) throws IOException {
        connections.remove(username);
        var message = String.format("%s left the game.", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);
    }
    private void resign(String username) throws IOException {
        var notification = new NotificationMessage(String.format("%s resigned.", username));
        connections.broadcast(username, notification);
    }
}
