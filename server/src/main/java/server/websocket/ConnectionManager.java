package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session) {
        var connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String username, ServerMessage message) throws IOException {
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            var removeList = new ArrayList<Connection>();
            for (var c : connections.values()) {
                if (c.session.isOpen()) {
                    if (!c.username.equals(username)) {
                        c.send(message.toString());
                    }
                } else {
                    removeList.add(c);
                }
            }

            for (var c : removeList) {
                connections.remove(c.username);
            }
        } else {
            for (var c : connections.values()) {
                if (c.username.equals(username)) {
                    c.send(message.toString());
                    break;
                }
            }
        }
    }
}
