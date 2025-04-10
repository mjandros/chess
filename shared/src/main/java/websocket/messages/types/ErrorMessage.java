package websocket.messages.types;

import websocket.messages.ServerMessage;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getMsg() { return errorMessage; }
}
