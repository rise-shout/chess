package websocket;

import websocket.messages.ServerMessage.Notification;

public interface NotificationHandler {
    void notify(Notification notification);
}
