package websocket.messages.types;

import websocket.messages.ServerMessage;

public class ErrorMessage extends ServerMessage {
    private final String msg;

    public ErrorMessage(String msg) {
        super(ServerMessageType.ERROR);
        this.msg = msg;
    }

    public String getMsg() { return msg; }
}
