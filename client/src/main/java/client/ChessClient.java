package client;

import exception.ResponseException;
import model.GameData;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ChessClient {
    private String name = null;
    public boolean loggedIn = false;
    private final int port;
    private final ServerFacade server;
    private Map<Integer, GameData> gameNumbers;

    public ChessClient(int port){
        this.port = port;
        server = new ServerFacade(port);
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
        if (params.length == 2) {
            try {
                server.login(params[0], params[1]);
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
        if (params.length == 3) {
            try {
                server.register(params[0], params[1], params[2]);
                name = params[0];
                loggedIn = true;
                return String.format("You signed in as %s.", name);
            } catch (Exception e) {
                throw new ResponseException(400, "Failed to create account: " + e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }
    public String logout(){
        return "logged out";
    }
    public String createGame(String... params) throws ResponseException {
        return "created";
    }
    public String listGames() throws ResponseException {
        try {
            gameNumbers.clear();
            List<GameData> games = server.listGames().games().stream().toList();
            String output = "";
            for (int i = 1; i <= games.size(); i++) {
                GameData currentGame = games.get(i - 1);
                gameNumbers.put(i, currentGame);
                output += String.format("%d - %s\n\tWHITE - %s | BLACK - %s\n",
                        currentGame.gameID(), currentGame.gameName(), currentGame.whiteUsername(), currentGame.blackUsername());
            }
            return output;
        } catch (Exception e) {
            throw new ResponseException(400, "Failed to find games: " + e.getMessage());
        }
    }
    public String playGame(String... params) throws ResponseException {
        return "playing";
    }
    public String observeGame(String... params) throws ResponseException {
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
