package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(int port) {
        client = new ChessClient(port);
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
        String status = client.board + "\n";
        if (client.state == State.LOGGEDIN) {
            status = "[LOGGED_IN]";
        } else if (client.state == State.LOGGEDOUT) {
            status = "[LOGGED_OUT]";
        }
        System.out.print("\n" + RESET_TEXT_COLOR + status + " >>> " + SET_TEXT_COLOR_GREEN);
    }
}
