package client;

import exception.ResponseException;
import model.GameData;
import service.results.*;
import service.requests.*;

import java.util.*;

public class ChessClient {
    private String name = null;
    public boolean loggedIn = false;
    private final int port;
    private final ServerFacade server;
    private Map<Integer, GameData> gameNumbers;
    private String authToken;

    public ChessClient(int port){
        this.port = port;
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
        if (loggedIn) {
            return "Already logged in.";
        }
        if (params.length == 2) {
            try {
                LoginResult res = server.login(params[0], params[1]);
                authToken = res.authToken();
                name = params[0];
                loggedIn = true;
                return String.format("You signed in as %s.", name);
            } catch (Exception e) {
                throw new ResponseException(400, "Username or password is incorrect.");
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }
    public String register(String... params) throws ResponseException {
        if (loggedIn) {
            return "Already logged in.";
        }
        if (params.length == 3) {
            try {
                RegisterResult res = server.register(params[0], params[1], params[2]);
                authToken = res.authToken();
                name = params[0];
                loggedIn = true;
                return String.format("You signed in as %s.", name);
            } catch (Exception e) {
                throw new ResponseException(400, "Failed to create account: " + e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }
    public String logout() throws ResponseException {
        if (!loggedIn) {
            return "Already logged out.";
        }
        try {
            server.logout(authToken);
            loggedIn = false;
            return "Successfully logged out.";
        } catch (Exception e) {
            throw new ResponseException(400, "Failed to create account: " + e.getMessage());
        }
    }
    public String createGame(String... params) throws ResponseException {
        if (!loggedIn) {
            return "Must be logged in to create a game.";
        }
        try {
            server.createGame(params[0], authToken);
            return String.format("Successfully created game %s.", params[0]);
        } catch (Exception e) {
            throw new ResponseException(400, "Failed to create account: " + e.getMessage());
        }
    }
    public String listGames() throws ResponseException {
        if (!loggedIn) {
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
        if (!loggedIn) {
            return "Must be logged in to play a game.";
        }
        return "playing";
    }
    public String observeGame(String... params) throws ResponseException {
        if (!loggedIn) {
            return "Must be logged in to observe a game.";
        }
        return "observing";
    }

    public String help() {
        if (!loggedIn) {
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
}
