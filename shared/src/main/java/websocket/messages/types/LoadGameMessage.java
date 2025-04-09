package websocket.messages.types;

import model.GameData;
import websocket.messages.ServerMessage;

public class LoadGameMessage extends ServerMessage {
    private final GameData game;

    public LoadGameMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public GameData getGame() { return game; }
}
