package websocket.messages;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    //-----DO NOT CHANGE ABOVE-------//

    public static class Notification extends ServerMessage {
        public Notification(ServerMessageType type) {
            super(type);
        }
        public Notification(ServerMessageType sType, Type type, String message) {
            super(sType);

        }

        public enum Type {
            PLAYER_ARRIVAL,
            OBSERVER_ARRIVAL,
            MADE_MOVE,
            PLAYER_DEPARTURE,
            RESIGNATION,
            IN_CHECK,
            IN_CHECKMATE

        }


    }

    public class Error extends ServerMessage {

        public Error(ServerMessageType type) {
            super(type);
        }
    }

    public class LoadGame extends ServerMessage {
        public LoadGame(ServerMessageType type) {
            super(type);
        }
    }

    //-----DO NOT CHANGE BELOW-------//

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
