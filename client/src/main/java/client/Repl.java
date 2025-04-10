package client;

import client.websocket.ServerMessageObserver;
import websocket.messages.ServerMessage;
import websocket.messages.types.ErrorMessage;
import websocket.messages.types.LoadGameMessage;
import websocket.messages.types.NotificationMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements ServerMessageObserver {
    private final ChessClient client;

    public Repl(int port) {
        var url = "http://localhost:" + port;
        client = new ChessClient(port, url);
    }

    public void run() {
        System.out.println(WHITE_PAWN + " Welcome to 240 chess. Type 'help' to get started." + BLACK_PAWN);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void printPrompt() {
        String status = "";
        if (client.state == State.LOGGEDIN) {
            status = "[LOGGED_IN]";
        } else if (client.state == State.LOGGEDOUT) {
            status = "[LOGGED_OUT]";
        } else {
            status = "[IN_GAME]";
        }
        System.out.print("\n" + RESET_TEXT_COLOR + status + " >>> " + SET_TEXT_COLOR_GREEN);
    }

    public void notify(ServerMessage message) {
        String msg = "";
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            msg = ((NotificationMessage) message).getMsg();
        } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            msg = ((LoadGameMessage) message).getGame().toString();
        } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            msg = ((ErrorMessage) message).getMsg();
        }
        System.out.println(SET_TEXT_COLOR_RED + msg);
        printPrompt();
    }
}
