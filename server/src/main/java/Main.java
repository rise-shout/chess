import chess.*;
import dataaccess.DataAccessException;
import server.Server;
import spark.Spark;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        // Create a server object and run it on port 8080
        Server server = new Server();


        int port = server.run(8080);
        System.out.println("Server is running on port: " + port);

        String serverHost = System.getenv("SERVER_HOST");
        System.out.println("Server Host is: " + serverHost);

        //System.out.println("Available routes:");
        //Spark.routes().forEach(System.out::println);


    }
}