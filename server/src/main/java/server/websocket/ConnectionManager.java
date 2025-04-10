package server.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import websocket.messages.types.ErrorMessage;
import websocket.messages.types.LoadGameMessage;
import websocket.messages.types.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, Session session) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String username, ServerMessage message) throws IOException {
        if (message.getClass() == NotificationMessage.class) {
            String msg = new Gson().toJson(message);
            System.out.println("json message: " + msg);
            var removeList = new ArrayList<Connection>();
            for (var c : connections.values()) {
                if (c.session.isOpen()) {
                    if (!c.username.equals(username)) {
                        c.send(msg);
                    }
                } else {
                    removeList.add(c);
                }
            }

            for (var c : removeList) {
                connections.remove(c.username);
            }
        } else {
            String msg;
            if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                msg = new Gson().toJson(new LoadGameMessage(((LoadGameMessage) message).getGame()));
            } else {
                msg = ((ErrorMessage) message).getMsg();
            }
            for (var c : connections.values()) {
                if (c.username.equals(username)) {
                    c.send(msg);
                    break;
                }
            }
        }
    }
}
