package server.websocket;

import com.google.gson.Gson;
import dataaccess.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.ServerMessage.*;

import java.io.IOException;
import java.util.Timer;



@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    private void enterPlayer(String userName, Session session) throws IOException {
        connections.add(userName, session);
        var message = String.format("%s has joined the game", userName);
        var notification = new Notification(ServerMessageType.NOTIFICATION, Notification.Type.PLAYER_ARRIVAL, message);
        connections.broadcast(userName, notification);
    }

    private void exitPlayer(String userName) throws IOException {
        connections.remove(userName);
        var message = String.format("%s has left the game", userName);
        var notification = new Notification(ServerMessageType.NOTIFICATION, Notification.Type.PLAYER_DEPARTURE, message);
        connections.broadcast(userName, notification);
    }

}