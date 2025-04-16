package server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage.*;

import java.io.IOException;
import java.util.Timer;



@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserDataAccess userDAO;
    private final AuthDataAccess authDAO;
    private final GameDataAccess gameDAO;

    public WebSocketHandler(UserDataAccess userDAO, AuthDataAccess authDAO, GameDataAccess gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            // Validate the message
            if (message == null || message.trim().isEmpty()) {
                session.getRemote().sendString("Invalid message received.");
                return;
            }

            // Deserialize the message
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            if (command == null || command.commandType == null) {
                session.getRemote().sendString("Invalid command received.");
                return;
            }

            // Process command
            switch (command.commandType) {
                case CONNECT -> {
                    enterPlayer(command.getAuthToken(), session);
                    session.getRemote().sendString("User connected: " + command.getAuthToken());
                }
                default -> {
                    session.getRemote().sendString("Unsupported command: " + command.commandType);
                }
            }
        } catch (JsonSyntaxException e) {
            // Handle JSON parsing errors
            session.getRemote().sendString("Error parsing message: " + e.getMessage());
        } catch (Exception e) {
            // Handle general errors
            session.getRemote().sendString("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace(); // Log for debugging
        }
    }

    private void enterPlayer(String authToken, Session session) throws IOException, DataAccessException {

        //TODO: check if observer instead

        //get the username from the auth token
        AuthData authData = authDAO.getAuth(authToken);


        connections.add(authData.username(), session);
        var message = String.format("Player %s has joined the game", authToken);
        var notification = new Notification(ServerMessageType.NOTIFICATION, Notification.Type.PLAYER_ARRIVAL, message);
        connections.broadcast(authToken, notification);
    }

    private void enterObserver(String userName, Session session) throws IOException {

        connections.add(userName, session);
        var message = String.format("Observer %s has joined the game", userName);
        var notification = new Notification(ServerMessageType.NOTIFICATION, Notification.Type.OBSERVER_ARRIVAL, message);
        connections.broadcast(userName, notification);
    }

    private void exitPlayer(String userName) throws IOException {
        connections.remove(userName);
        var message = String.format("%s has left the game", userName);
        var notification = new Notification(ServerMessageType.NOTIFICATION, Notification.Type.PLAYER_DEPARTURE, message);
        connections.broadcast(userName, notification);
    }

}