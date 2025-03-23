package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;
    private final String whiteBoard = SET_BG_COLOR_DARK_GREY +
            "   a  b  c  d  e  f  g  h    \n 8 " + SET_BG_COLOR_WHITE +
            " R " + SET_BG_COLOR_BLACK + " N ";
    private final String blackBoard = "";

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
        String status = "[LOGGED_OUT]";
        if (client.state == State.LOGGEDIN) {
            status = "[LOGGED_IN]";
        } else if (client.state == State.INGAMEBLACK) {
            status = whiteBoard + "\n";
        } else {
            status = blackBoard + "\n";
        }
        System.out.print("\n" + RESET_TEXT_COLOR + status + " >>> " + SET_TEXT_COLOR_GREEN);
    }
}
