package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.AuthData;
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
import websocket.messages.types.ErrorMessage;

import java.io.IOException;
import java.util.ArrayList;

@WebSocket
public class WebsocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebsocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResponseException, DataAccessException {
        try {
            UserGameCommand command;
            if (message.contains("move")) {
                command = new Gson().fromJson(message, MakeMoveCommand.class);
            } else {
                command = new Gson().fromJson(message, UserGameCommand.class);
            }
            GameData game = gameDAO.getGame(command.getGameID());
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), session, game);
                case MAKE_MOVE -> makeMove(command.getAuthToken(), session, game, ((MakeMoveCommand) command).getMove());
                case LEAVE -> leave(command.getAuthToken(), session);
                case RESIGN -> resign(command.getAuthToken(), session);
            }
        } catch (Exception e) {
            sendError(session, e.getMessage());
        }
    }

    private void sendError(Session session, String errorMessage) throws IOException {
        if (!errorMessage.toLowerCase().contains("error")) {
            errorMessage = "Error: " + errorMessage;
        }

        var error = new ErrorMessage(errorMessage);
        String msg = new Gson().toJson(error);
        session.getRemote().sendString(msg);
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
        t.printStackTrace();
    }

    private void connect(String authToken, Session session, GameData game) throws IOException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            connections.add(authToken, session);
            String message = String.format("%s has joined %s.", authData.username(), game.gameName());
            var loadGame = new LoadGameMessage(game);
            var notification = new NotificationMessage(message);
            connections.broadcast(authToken, loadGame, "root");
            connections.broadcast(authToken, notification, "not root");
        } catch (Exception e) {
            sendError(session, e.getMessage());
        }
    }

    private void makeMove(String authToken, Session session, GameData game, ChessMove move) throws IOException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            if (!isMoveValid(game, move, authData)) {
                sendError(session, "Invalid move.");
                return;
            }
            String startPos = String.format("%s%s", ('a' + (move.getStartPosition().getColumn() - 1)), "" + move.getStartPosition().getRow());
            String endPos = String.format("%s%s", ('a' + (move.getEndPosition().getColumn() - 1)), "" + move.getEndPosition().getRow());
            String message = String.format("%s has moved their piece from %s to %s.", authData.username(), startPos, endPos);
            var notification = new NotificationMessage(message);
            var loadGame = new LoadGameMessage(game);
            connections.broadcast(authToken, notification, "not root");
            connections.broadcast(authToken, loadGame, "all");
        } catch (Exception e) {
            sendError(session, e.getMessage());
        }
    }
    private boolean isMoveValid(GameData gameData, ChessMove move, AuthData authData) {
        try {
            ChessGame game = gameData.game();
            ChessBoard board = game.getBoard();
            ChessPosition startPos = move.getStartPosition();
            if (board.getPiece(startPos) == null) {
                return false;
            }
            ChessGame.TeamColor playerColor;
            if (authData.username().equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (authData.username().equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            } else {
                return false;
            }
            if (game.getTeamTurn() != playerColor) {
                return false;
            }
            if (board.getPiece(startPos).getTeamColor() != playerColor) {
                return false;
            }
            if (game.getTeamTurn() != board.getPiece(startPos).getTeamColor()) {
                return false;
            }
            ArrayList<ChessPosition> validMoves = new ArrayList<>();
            for (ChessMove cm : board.getPiece(startPos).pieceMoves(board, startPos)) {
                validMoves.add(cm.getEndPosition());
            }
            return validMoves.contains(move.getEndPosition());
        } catch (Exception e) {
            return false;
        }
    }
    private void leave(String authToken, Session session) throws IOException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            connections.remove(authToken);
            var message = String.format("%s left the game.", authData.username());
            var notification = new NotificationMessage(message);
            connections.broadcast(authToken, notification, "not root");
        } catch (Exception e) {
            sendError(session, e.getMessage());
        }
    }
    private void resign(String authToken, Session session) throws IOException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            var notification = new NotificationMessage(String.format("%s resigned.", authData.username()));
            connections.broadcast(authToken, notification, "all");
        } catch (Exception e) {
            sendError(session, e.getMessage());
        }
    }
}
