package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import service.results.*;

import static ui.EscapeSequences.*;
import java.util.*;

public class ChessClient {
    private String name = null;
    public State state = State.LOGGEDOUT;
    private final ServerFacade server;
    private Map<Integer, GameData> gameNumbers;
    private String authToken;
    public String board;


    public ChessClient(int port){
        server = new ServerFacade(port);
        gameNumbers = new HashMap<>();
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> playGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    public String login(String... params) throws ResponseException {
        if (state == State.LOGGEDIN) {
            return "Already logged in.";
        }
        if (params.length == 2) {
            try {
                LoginResult res = server.login(params[0], params[1]);
                authToken = res.authToken();
                name = params[0];
                state = State.LOGGEDIN;
                List<GameData> games = server.listGames(authToken).games().stream().toList();
                for (GameData game : games) {
                    if (game.whiteUsername() != null && game.whiteUsername().equals(name)) {
                        state = State.INGAMEWHITE;
                        board = setUpBoard("WHITE", game.game().getBoard());
                    } else if (game.blackUsername() != null && game.blackUsername().equals(name)) {
                        state = State.INGAMEBLACK;
                        board = setUpBoard("BLACK", game.game().getBoard());
                    }
                }
                return String.format("You signed in as %s.", name);
            } catch (Exception e) {
                throw new ResponseException(400, "Failed to log in: " + e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }
    public String register(String... params) throws ResponseException {
        if (state == State.LOGGEDIN) {
            return "Already logged in.";
        }
        if (params.length == 3) {
            try {
                RegisterResult res = server.register(params[0], params[1], params[2]);
                authToken = res.authToken();
                name = params[0];
                state = State.LOGGEDIN;
                return String.format("You signed in as %s.", name);
            } catch (Exception e) {
                throw new ResponseException(400, "Failed to create account: " + e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }
    public String logout() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            return "Already logged out.";
        }
        try {
            server.logout(authToken);
            state = State.LOGGEDOUT;
            return "Successfully logged out.";
        } catch (Exception e) {
            throw new ResponseException(400, "Failed to log out: " + e.getMessage());
        }
    }
    public String createGame(String... params) throws ResponseException {
        if (state == State.LOGGEDOUT) {
            return "Must be logged in to create a game.";
        }
        if (params.length == 1) {
            try {
                server.createGame(params[0], authToken);
                return String.format("Successfully created %s.", params[0]);
            } catch (Exception e) {
                throw new ResponseException(400, "Failed to create game: " + e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }
    public String listGames() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            return "Must be logged in to view ongoing games.";
        }
        try {
            gameNumbers.clear();
            List<GameData> games = server.listGames(authToken).games().stream().toList();
            String output = "";
            for (int i = 1; i <= games.size(); i++) {
                GameData currentGame = games.get(i - 1);
                gameNumbers.put(i, currentGame);
                String white = currentGame.whiteUsername();
                String black = currentGame.blackUsername();
                if (white == null) {
                    white = "Empty";
                }
                if (black == null) {
                    black = "Empty";
                }
                output += String.format("%d - %s\n\tWHITE - %s\n\tBLACK - %s\n",
                        currentGame.gameID(), currentGame.gameName(), white, black);
            }
            if (output.isEmpty()) {
                output = "There are currently no ongoing games.";
            }
            return output;
        } catch (Exception e) {
            throw new ResponseException(400, "Failed to find games: " + e.getMessage());
        }
    }
    public String playGame(String... params) throws ResponseException {
        if (state == State.LOGGEDOUT) {
            return "Must be logged in to play a game.";
        }
        if (params.length == 2 && (params[1].equalsIgnoreCase("WHITE")
                || params[1].equalsIgnoreCase("BLACK"))) {
            try {
                if (gameNumbers.isEmpty()) {
                    return "Must view games before joining. Try typing 'list'.";
                }
                GameData game = gameNumbers.get(Integer.parseInt(params[0]));
                if (game == null) {
                    return "Game does not exist.";
                }
                int id = game.gameID();
                server.joinGame(params[1].toUpperCase(), id, authToken);
                if (params[1].equalsIgnoreCase("WHITE")) {
                    state = State.INGAMEWHITE;
                    board = setUpBoard("WHITE", game.game().getBoard());
                } else {
                    state = State.INGAMEBLACK;
                    board = setUpBoard("BLACK", game.game().getBoard());
                }
                return String.format("Successfully joined %s as %s", game.gameName(), params[1].toUpperCase());
            } catch (Exception e) {
                throw new ResponseException(400, "Failed to join game: " + e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
    }
    public String observeGame(String... params) throws ResponseException {
        if (state == State.LOGGEDOUT) {
            return "Must be logged in to observe a game.";
        }
        if (params.length == 2 && (params[1].equalsIgnoreCase("WHITE")
                || params[1].equalsIgnoreCase("BLACK"))) {
            try {
                if (gameNumbers.isEmpty()) {
                    return "Must view games before joining. Try typing 'list'.";
                }
                GameData game = gameNumbers.get(Integer.parseInt(params[0]));
                if (game == null) {
                    return "Game does not exist.";
                }
                int id = game.gameID();
                server.joinGame(null, id, authToken);
                state = State.INGAMEOBSERVER;
                board = setUpBoard("WHITE", game.game().getBoard());
                return String.format("Successfully joined %s as an observer.", game.gameName());
            } catch (Exception e) {
                throw new ResponseException(400, "Failed to join game: " + e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <ID>");
    }

    public String help() {
        if (state == State.LOGGEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - create an account
                    login <USERNAME> <PASSWORD> - log in to an existing account
                    quit - close the program
                    help - list commands
                    """;
        }
        return """
                create <NAME> - create a game with the given name
                list - display all ongoing games
                join <ID> [WHITE|BLACK] - join the given game as the given color
                observe <ID> - spectate the given game
                logout - log out of your account
                quit - close the program
                help - list commands
                """;
    }

    public String setUpBoard(String color, ChessBoard board) {
        boolean white = true;
        String ret = SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE;
        if (color.equals("WHITE")) {
            ret += "    a  b  c  d  e  f  g  h    \n";
            for (int r = 8; r > 0; r--) {
                ret += addRow(white, board, r);
                white = !white;
            }
            ret += SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + "    a  b  c  d  e  f  g  h    ";
        } else {
            ret += "    h  g  f  e  d  c  b  a    \n";
            for (int r = 1; r < 9; r++) {
                ret += addRow(white, board, r);
                white = !white;
            }
            ret += SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + "    h  g  f  e  d  c  b  a    ";
        }

        return ret;
    }

    public String addRow(boolean white, ChessBoard board, int r) {
        String row = SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + String.format(" %d ", r);
        for (int c = 1; c < 9; c++) {
            row += addSquare(board.getPiece(new ChessPosition(r, c)), white);
            white = !white;
        }
        row += SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + String.format(" %d \n", r);
        return row;
    }

    public String addSquare(ChessPiece piece, boolean whiteSquare) {
        String square = "";
        if (whiteSquare) {
            square += SET_BG_COLOR_WHITE;
        } else {
            square += SET_BG_COLOR_BLACK;
        }
        if (piece == null) {
            square += "   ";
            return square;
        }
        square += " ";
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            square += SET_TEXT_COLOR_RED;
        } else {
            square += SET_TEXT_COLOR_BLUE;
        }
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            square += "P";
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            square += "R";
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            square += "N";
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            square += "B";
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            square += "Q";
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            square += "K";
        }
        square += " ";
        return square;
    }
}
