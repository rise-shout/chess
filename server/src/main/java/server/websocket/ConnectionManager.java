package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, Session session) {

        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
    }

    public void remove(String userName) {
        connections.remove(userName);
    }

    public void broadcast(String excludeUserName, Notification notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        String jsonNotification = new Gson().toJson(notification); // Serialize to JSON

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.userName.equals(excludeUserName)) {
                    c.send(jsonNotification); // Send JSON string
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.userName);
        }
    }
}