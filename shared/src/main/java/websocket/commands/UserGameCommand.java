package websocket.commands;

import websocket.messages.ServerMessage;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    //-----DO NOT CHANGE ABOVE-------//

    public class Connect extends UserGameCommand {

        public Connect(CommandType commandType, String authToken, Integer gameID) {
            super(commandType, authToken, gameID);
        }
    }

    public class Leave extends UserGameCommand {

        public Leave(CommandType commandType, String authToken, Integer gameID) {
            super(commandType, authToken, gameID);
        }
    }

    public class Resign extends UserGameCommand {

        public Resign(CommandType commandType, String authToken, Integer gameID) {
            super(commandType, authToken, gameID);
        }
    }

    public class MakeMove extends UserGameCommand {

        public MakeMove(CommandType commandType, String authToken, Integer gameID) {
            super(commandType, authToken, gameID);
        }
    }

    //-----DO NOT CHANGE BELOW-------//

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand)) {
            return false;
        }
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
