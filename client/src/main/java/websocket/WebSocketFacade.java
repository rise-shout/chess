package websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.Notification;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception(ex.getMessage());
        }
    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void playerJoinGame (String userName, String authToken, Integer gameID) throws Exception {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, userName, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }
}
