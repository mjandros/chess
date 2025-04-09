package websocket.commands.types;

import chess.ChessMove;
import websocket.commands.UserGameCommand;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;

    public MakeMoveCommand(ChessMove move, String username, String authToken, int gameID) {
        super(CommandType.MAKE_MOVE, username, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}
