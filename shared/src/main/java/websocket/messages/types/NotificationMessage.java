package websocket.messages.types;

import websocket.messages.ServerMessage;

public class NotificationMessage extends ServerMessage {
    private final String msg;

    public NotificationMessage(String msg) {
        super(ServerMessageType.NOTIFICATION);
        this.msg = msg;
    }

    public String getMsg() { return msg; }
}
