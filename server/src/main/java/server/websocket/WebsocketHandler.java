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
        System.out.println("Message received: " + message);
        if (message.contains("move")) {
            command = new Gson().fromJson(message, MakeMoveCommand.class);
        } else {
            command = new Gson().fromJson(message, UserGameCommand.class);
        }
        GameData game = gameDAO.getGame(command.getGameID());
        System.out.println("Command username: " + command.getUsername());
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), session, game);
            case MAKE_MOVE -> makeMove(command.getAuthToken(), ((MakeMoveCommand) command).getMove());
            case LEAVE -> leave(command.getAuthToken());
            case RESIGN -> resign(command.getAuthToken());
        }
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
        t.printStackTrace();
    }

    private void connect(String authToken, Session session, GameData game) throws IOException {
        connections.add(authToken, session);
        String message = String.format("%s has joined %s.", "player", game.gameName()); //replace w actual username
        var loadGame = new LoadGameMessage(game);
        var notification = new NotificationMessage(message);
        System.out.println("message: " + notification.getMessage());
        connections.broadcast(authToken, loadGame);
        connections.broadcast(authToken, notification);
    }

    private void makeMove(String authToken, ChessMove move) throws IOException {
        String startPos = String.format("%s%s", ('a' + (move.getStartPosition().getColumn() - 1)), "" + move.getStartPosition().getRow());
        String endPos = String.format("%s%s", ('a' + (move.getEndPosition().getColumn() - 1)), "" + move.getEndPosition().getRow());
        String message = String.format("%s has moved their piece from %s to %s.", "player", startPos, endPos); //replace w username
        var notification = new NotificationMessage(message);
        connections.broadcast(authToken, notification);
    }
    private void leave(String authToken) throws IOException {
        connections.remove(authToken);
        var message = String.format("%s left the game.", "player"); //same here
        var notification = new NotificationMessage(message);
        connections.broadcast(authToken, notification);
    }
    private void resign(String authToken) throws IOException {
        var notification = new NotificationMessage(String.format("%s resigned.", "player")); //yeah
        connections.broadcast(authToken, notification);
    }
}
