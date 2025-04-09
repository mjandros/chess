package client;

import chess.*;
import exception.ResponseException;
import model.GameData;
import model.results.LoginResult;
import model.results.RegisterResult;
import model.results.*;

import static ui.EscapeSequences.*;
import java.util.*;

public class ChessClient {
    private String name = null;
    public State state = State.LOGGEDOUT;
    private final ServerFacade server;
    private Map<Integer, GameData> gameNumbers;
    private int currentGame;
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
                case "play" -> joinGame("play", params);
                case "observe" -> joinGame("observe", params);
                case "board" -> redrawBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove();
                case "resign" -> resign();
                case "moves" -> highlightMoves(params);
                case "quit" -> "quit";
                case "clear" -> clearDB();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    public String login(String... params) throws ResponseException {
        if (state == State.LOGGEDIN) {
            throw new ResponseException(400, "Already logged in.");
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
                        board = setUpBoard("WHITE", game.game().getBoard(), null);
                    } else if (game.blackUsername() != null && game.blackUsername().equals(name)) {
                        state = State.INGAMEBLACK;
                        board = setUpBoard("BLACK", game.game().getBoard(), null);
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
            throw new ResponseException(400, "Already logged in.");
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
            throw new ResponseException(400, "Already logged out.");
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
            throw new ResponseException(400, "Must be logged in to create a game.");
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
            throw new ResponseException(400, "Must be logged in to view ongoing games.");
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
    public String joinGame(String status, String... params) throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(400, "Must be logged in to join a game.");
        }
        if ((status.equals("play") && params.length == 2 && (params[1].equalsIgnoreCase("WHITE")
                || params[1].equalsIgnoreCase("BLACK"))) ||
                (status.equals("observe") && params.length == 1)) {
            try {
                if (gameNumbers.isEmpty()) {
                    throw new ResponseException(400, "Must view games before joining. Try typing 'list'.");
                }
                currentGame = Integer.parseInt(params[0]);
                GameData game = gameNumbers.get(currentGame);
                if (game == null) {
                    throw new ResponseException(400, "Game does not exist.");
                }
                int id = game.gameID();
                String position = "an observer";
                if (status.equals("play")) {
                    position = params[1].toUpperCase();
                    server.joinGame(position, id, authToken);
                    if (params[1].equalsIgnoreCase("BLACK")) {
                        state = State.INGAMEBLACK;
                    } else {
                        state = State.INGAMEWHITE;
                    }
                    board = setUpBoard(position, game.game().getBoard(), null);
                } else {
                    state = State.INGAMEOBSERVER;
                    board = setUpBoard("WHITE", game.game().getBoard(), null);
                }
                return String.format("Successfully joined %s as %s.", game.gameName(), position);
            } catch (Exception e) {
                throw new ResponseException(400, "Failed to join game: " + e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
    }
    public String redrawBoard() throws ResponseException {
        if (state == State.LOGGEDOUT || state == State.LOGGEDIN) {
            throw new ResponseException(400, "Must be in a game to view the board.");
        }
        try {
            return board;
        } catch (Exception e) {
            throw new ResponseException(400, "Failed to draw board: " + e.getMessage());
        }
    }
    public String leaveGame() throws ResponseException {
        if (state == State.LOGGEDOUT || state == State.LOGGEDIN) {
            throw new ResponseException(400, "Must be in a game to leave the game.");
        }
        try {
            GameData game = gameNumbers.get(currentGame);
            if (game == null) {
                throw new ResponseException(400, "Game does not exist.");
            }
            int id = game.gameID();
            if (state == State.INGAMEWHITE) {
                //update whiteUsername in db
            } else if (state == State.INGAMEBLACK) {
                //update blackUsername in db
            }
            state = State.LOGGEDIN;
            return String.format("Left %s.", game.gameName());
        } catch (Exception e) {
            throw new ResponseException(400, "Failed to leave game: " + e.getMessage());
        }
    }
    public String makeMove() throws ResponseException {
        if (state == State.LOGGEDOUT || state == State.LOGGEDIN || state == State.INGAMEOBSERVER) {
            throw new ResponseException(400, "Must be in a game as a player to make a move.");
        }
        return "";
    }
    public String resign() throws ResponseException {
        if (state == State.LOGGEDOUT || state == State.LOGGEDIN || state == State.INGAMEOBSERVER) {
            throw new ResponseException(400, "Must be in a game as a player to resign.");
        }
        return "";
    }
    public String highlightMoves(String... params) throws ResponseException {
        if (state == State.LOGGEDOUT || state == State.LOGGEDIN || state == State.INGAMEOBSERVER) {
            throw new ResponseException(400, "Must be in a game as a player to show moves.");
        }
        if (params.length == 1 && params[0].length() == 2
        && !Character.isDigit(params[0].charAt(0)) && Character.isDigit(params[0].charAt(1))
        && (params[0].charAt(0) - 'a' + 1) > 0 && (params[0].charAt(0) - 'a' + 1) < 9) {
            try {
                GameData game = gameNumbers.get(currentGame);
                int col = params[0].charAt(0) - 'a' + 1;
                ChessPosition pos = new ChessPosition(Integer.parseInt("" + params[0].charAt(1)), col);
                ChessBoard chessBoard = game.game().getBoard();
                ChessPiece piece = chessBoard.getPiece(pos);
                Collection<ChessMove> moves = piece.pieceMoves(chessBoard, pos);
                ArrayList<ChessPosition> highlights = new ArrayList<>();
                highlights.add(pos);
                for (ChessMove cm : moves) {
                    highlights.add(cm.getEndPosition());
                }
                String side = "WHITE";
                if (state == State.INGAMEBLACK) {
                    side = "BLACK";
                }
                return setUpBoard(side, chessBoard, highlights);
            } catch (Exception e) {
                throw new ResponseException(400, "Failed to show moves: " + e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: [A-H][1-8]");
    }
    public String help() {
        if (state == State.LOGGEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - create an account
                    login <USERNAME> <PASSWORD> - log in to an existing account
                    quit - close the program
                    help - list commands
                    """;
        } else if (state == State.LOGGEDIN) {
            return """
                    create <NAME> - create a game with the given name
                    list - display all ongoing games
                    play <ID> [WHITE|BLACK] - join the given game as the given color
                    observe <ID> - spectate the given game
                    logout - log out of your account
                    quit - close the program
                    help - list commands
                    """;
        } else if (state == State.INGAMEOBSERVER) {
            return """
                    board - redraw and display board
                    leave - leave game
                    """;
        }
        return """
                help - list all available moves
                board - redraw and display board
                leave - leave game
                move [A-H][1-8] [A-H][1-8] - move from the first space listed to the second
                resign - forfeit the game
                moves [A-H][1-8] - display board with available moves highlighted
                """;
    }

    public String clearDB() throws ResponseException {
        server.clear();
        state = State.LOGGEDOUT;
        return "Database has been cleared.";
    }

    public String setUpBoard(String color, ChessBoard board, ArrayList<ChessPosition> highlights) {
        if (highlights == null) {
            highlights = new ArrayList<>();
        }
        boolean white = true;
        String ret = SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE;
        if (color.equals("WHITE")) {
            ret += "    a  b  c  d  e  f  g  h    \n";
            for (int r = 8; r > 0; r--) {
                ret += addRow(white, board, r, color, highlights);
                white = !white;
            }
            ret += SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + "    a  b  c  d  e  f  g  h    ";
        } else {
            ret += "    h  g  f  e  d  c  b  a    \n";
            for (int r = 1; r < 9; r++) {
                ret += addRow(white, board, r, color, highlights);
                white = !white;
            }
            ret += SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + "    h  g  f  e  d  c  b  a    ";
        }

        return ret;
    }

    public String getSquareColor(ChessPosition pos, boolean white, ArrayList<ChessPosition> highlights) {
        String squareColor;
        if (white) {
            squareColor = "WHITE";
        } else {
            squareColor = "BLACK";
        }
        if (highlights.contains(pos)) {
            if (highlights.indexOf(pos) == 0) {
                squareColor = "YELLOW";
            } else {
                if (squareColor.equals("WHITE")) {
                    squareColor = "LIGHTGREEN";
                } else {
                    squareColor = "DARKGREEN";
                }
            }
        }
        return squareColor;
    }

    public String addRow(boolean white, ChessBoard board, int r, String color, ArrayList<ChessPosition> highlights) {
        String row = SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + String.format(" %d ", r);
        if (color.equals("WHITE")){
            for (int c = 1; c < 9; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                row += addSquare(board.getPiece(pos), getSquareColor(pos, white, highlights));
                white = !white;
            }
        }
        else {
            for (int c = 8; c > 0; c--) {
                ChessPosition pos = new ChessPosition(r, c);
                row += addSquare(board.getPiece(pos), getSquareColor(pos, white, highlights));
                white = !white;
            }
        }
        row += SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + String.format(" %d \n", r);
        return row;
    }

    public String addSquare(ChessPiece piece, String squareColor) {
        String square = "";
        if (squareColor.equals("WHITE")) {
            square += SET_BG_COLOR_WHITE;
        } else if (squareColor.equals("BLACK")) {
            square += SET_BG_COLOR_BLACK;
        } else if (squareColor.equals("YELLOW")) {
            square += SET_BG_COLOR_YELLOW;
        } else if (squareColor.equals("LIGHTGREEN")) {
            square += SET_BG_COLOR_GREEN;
        } else if (squareColor.equals("DARKGREEN")) {
            square += SET_BG_COLOR_DARK_GREEN;
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
