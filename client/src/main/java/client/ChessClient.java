package client;

import exception.ResponseException;

import java.util.Arrays;

public class ChessClient {
    private String name = null;
    private boolean signedIn = false;
    private final String serverUrl;
    private final ServerFacade server;

    public ChessClient(String serverUrl){
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
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
        return "";
    }
    public String register(String... params) throws ResponseException {
        return "";
    }
    public String logout(){
        return "";
    }
    public String createGame(String... params) throws ResponseException {
        return "";
    }
    public String listGames() throws ResponseException {
        return "";
    }
    public String playGame(String... params) throws ResponseException {
        return "";
    }
    public String observeGame(String... params) throws ResponseException {
        return "";
    }

    public String help() {
        if (signedIn) {
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
